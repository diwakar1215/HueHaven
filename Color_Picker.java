import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class Color_Picker extends JFrame {
    private final JPanel colorPanel = new JPanel();
    private final JTextField colorNameField = new JTextField();
    private final Stack<Color> undoStack = new Stack<>();
    private final Stack<Color> redoStack = new Stack<>();
    private final DefaultListModel<Color> colorListModel = new DefaultListModel<>();
    private final HashMap<Color, String> colorNameMap = new HashMap<>();
    private final JList<Color> colorList = new JList<>(colorListModel);

    public Color_Picker(String username) {
        super("HueHaven - Palette Designer");

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.decode("#FEFAE0"));
        setLayout(null);

        JLabel titleLabel = new JLabel("Create Your Palette", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.decode("#D4A373"));
        titleLabel.setBounds(200, 30, 400, 50);
        add(titleLabel);

        JButton colorChooserBtn = new JButton("Choose Color");
        colorChooserBtn.setBounds(100, 100, 200, 40);
        colorChooserBtn.setBackground(Color.decode("#E9EDC9"));
        colorChooserBtn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        add(colorChooserBtn);

        colorPanel.setBounds(320, 100, 60, 40);
        colorPanel.setBorder(new LineBorder(Color.decode("#D4A373"), 2));
        add(colorPanel);

        JLabel nameLabel = new JLabel("Color Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(Color.decode("#D4A373"));
        nameLabel.setBounds(100, 160, 200, 30);
        add(nameLabel);

        colorNameField.setBounds(100, 200, 200, 40);
        colorNameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        colorNameField.setBorder(new LineBorder(Color.decode("#E9EDC9"), 2));
        add(colorNameField);

        JButton addButton = new JButton("Add to Palette");
        addButton.setBounds(320, 200, 200, 40);
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        addButton.setBackground(Color.decode("#E9EDC9"));
        add(addButton);

        colorList.setBounds(100, 270, 600, 100);
        colorList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Color color = (Color) value;
                String name = colorNameMap.getOrDefault(color, "Unnamed");
                String hex = "#" + Integer.toHexString(color.getRGB()).substring(2).toUpperCase();
                label.setText(name + " - " + hex);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBackground(color);
                label.setOpaque(true);
                label.setForeground(getContrastColor(color));
                return label;
            }
        });
        add(colorList);

        JButton undoBtn = new JButton("Undo");
        undoBtn.setBounds(100, 400, 150, 40);
        add(undoBtn);

        JButton redoBtn = new JButton("Redo");
        redoBtn.setBounds(270, 400, 150, 40);
        add(redoBtn);

        JButton savePaletteBtn = new JButton("Save Palette");
        savePaletteBtn.setBounds(450, 400, 150, 40);
        add(savePaletteBtn);

        JButton loadPaletteBtn = new JButton("Load Palette");
        loadPaletteBtn.setBounds(620, 400, 150, 40);
        add(loadPaletteBtn);

        colorChooserBtn.addActionListener(e -> {
            Color chosenColor = JColorChooser.showDialog(this, "Pick a Color", Color.WHITE);
            if (chosenColor != null) {
                colorPanel.setBackground(chosenColor);
            }
        });

        addButton.addActionListener(e -> {
            Color selectedColor = colorPanel.getBackground();
            String name = colorNameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please name the color!");
                return;
            }

            colorListModel.addElement(selectedColor);
            colorNameMap.put(selectedColor, name);
            undoStack.push(selectedColor);
            redoStack.clear();
        });

        undoBtn.addActionListener(e -> {
            if (!undoStack.isEmpty()) {
                Color last = undoStack.pop();
                redoStack.push(last);
                colorListModel.removeElement(last);
                colorNameMap.remove(last);
            }
        });

        redoBtn.addActionListener(e -> {
            if (!redoStack.isEmpty()) {
                Color redoColor = redoStack.pop();
                undoStack.push(redoColor);
                colorListModel.addElement(redoColor);
                
            }
        });

        savePaletteBtn.addActionListener(e -> {
            String paletteName = JOptionPane.showInputDialog(this, "Enter palette name:");
            if (paletteName == null || paletteName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Palette name is required.");
                return;
            }

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/color_palette_app", "root", "shubham@#1215")) {

                String getUserId = "SELECT id FROM users WHERE username = ?";
                PreparedStatement psUser = conn.prepareStatement(getUserId);
                psUser.setString(1, username);
                ResultSet rs = psUser.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("id");

                    String insertPalette = "INSERT INTO palettes (user_id, name) VALUES (?, ?)";
                    PreparedStatement psInsert = conn.prepareStatement(insertPalette, Statement.RETURN_GENERATED_KEYS);
                    psInsert.setInt(1, userId);
                    psInsert.setString(2, paletteName);
                    psInsert.executeUpdate();

                    ResultSet generatedKeys = psInsert.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int paletteId = generatedKeys.getInt(1);

                        String insertColor = "INSERT INTO user_palettes (user_id, color_code, palette_id) VALUES (?, ?, ?)";
                        PreparedStatement psColor = conn.prepareStatement(insertColor);

                        for (int i = 0; i < colorListModel.size(); i++) {
                            Color color = colorListModel.getElementAt(i);
                            String hex = "#" + Integer.toHexString(color.getRGB()).substring(2).toUpperCase();
                            psColor.setInt(1, userId);
                            psColor.setString(2, hex);
                            psColor.setInt(3, paletteId);
                            psColor.addBatch();
                        }

                        psColor.executeBatch();
                        JOptionPane.showMessageDialog(this, "Palette saved successfully!");
                    }
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Save Error: " + ex.getMessage());
            }
        });

        loadPaletteBtn.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/color_palette_app", "root", "shubham@#1215")) {

                String getUserId = "SELECT id FROM users WHERE username = ?";
                PreparedStatement psUser = conn.prepareStatement(getUserId);
                psUser.setString(1, username);
                ResultSet rs = psUser.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("id");

                    String getPalettes = "SELECT id, name FROM palettes WHERE user_id = ?";
                    PreparedStatement psPalettes = conn.prepareStatement(getPalettes);
                    psPalettes.setInt(1, userId);
                    ResultSet paletteRs = psPalettes.executeQuery();

                    HashMap<String, Integer> paletteMap = new HashMap<>();
                    ArrayList<String> paletteNames = new ArrayList<>();

                    while (paletteRs.next()) {
                        int pid = paletteRs.getInt("id");
                        String pname = paletteRs.getString("name");
                        paletteMap.put(pname, pid);
                        paletteNames.add(pname);
                    }

                    if (paletteNames.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No saved palettes found.");
                        return;
                    }

                    String selection = (String) JOptionPane.showInputDialog(this, "Select a Palette",
                            "Load Palette", JOptionPane.PLAIN_MESSAGE, null,
                            paletteNames.toArray(), paletteNames.get(0));

                    if (selection != null) {
                        int paletteId = paletteMap.get(selection);

                        String getColors = "SELECT color_code FROM user_palettes WHERE palette_id = ?";
                        PreparedStatement psColors = conn.prepareStatement(getColors);
                        psColors.setInt(1, paletteId);
                        ResultSet colorsRs = psColors.executeQuery();

                        colorListModel.clear();
                        undoStack.clear();
                        redoStack.clear();
                                                colorListModel.clear();
                        undoStack.clear();
                        redoStack.clear();
                        colorNameMap.clear();

                        while (colorsRs.next()) {
                            String hex = colorsRs.getString("color_code");
                            Color color = Color.decode(hex);

                            
                            String name = "Color"; 
                            colorListModel.addElement(color);
                            colorNameMap.put(color, name);
                            undoStack.push(color);
                        }

                        JOptionPane.showMessageDialog(this, "Palette '" + selection + "' loaded!");
                    }
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Load Error: " + ex.getMessage());
            }
        });

        setVisible(true);
    }

    private Color getContrastColor(Color color) {
        int y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.BLACK : Color.WHITE;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Color_Picker("testuser")); 
    }
}