import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SuggestedPaletteViewer {
    private JFrame frame;
    private JLabel imageLabel;
    private JButton nextButton, prevButton, favoriteButton, viewFavoritesButton;
    private JPanel themePanel;
    private ArrayList<String> currentImages = new ArrayList<>();
    private int currentIndex = 0;

    public SuggestedPaletteViewer() {
        frame = new JFrame("Suggested Palettes");
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(Color.decode("#FEFAE0"));

        ImageIcon icon = new ImageIcon("images/bright.png"); // update path if needed
        frame.setIconImage(icon.getImage());

        // Image label
        imageLabel = new JLabel("Please select a theme", SwingConstants.CENTER);
        imageLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        imageLabel.setForeground(Color.decode("#D4A373"));
        imageLabel.setBorder(new EmptyBorder(30, 30, 30, 30));
        frame.add(imageLabel, BorderLayout.CENTER);

        // Theme buttons
        themePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        themePanel.setBackground(Color.decode("#FAEDCD"));
        String[] themes = {"spring", "autumn", "dark", "red", "blue", "green"};

        for (String theme : themes) {
            JButton themeBtn = new JButton(theme);
            styleButton(themeBtn);
            themeBtn.addActionListener(e -> loadImagesFromTheme(theme));
            themePanel.add(themeBtn);
        }

        // View Favorites button
        viewFavoritesButton = new JButton("View Favorites");
        styleButton(viewFavoritesButton);
        viewFavoritesButton.addActionListener(e -> new FavoritesViewer());
        themePanel.add(viewFavoritesButton);

        frame.add(themePanel, BorderLayout.NORTH);

        // Navigation panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.X_AXIS));
        navPanel.setBorder(new EmptyBorder(10, 100, 20, 100));
        navPanel.setBackground(Color.decode("#FAEDCD"));

        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        favoriteButton = new JButton("Add to Favorites");

        for (JButton btn : new JButton[]{prevButton, nextButton, favoriteButton}) {
            styleButton(btn);
        }

        prevButton.addActionListener(e -> showImage(currentIndex - 1));
        nextButton.addActionListener(e -> showImage(currentIndex + 1));
        favoriteButton.addActionListener(e -> saveToFavorites());

        navPanel.add(prevButton);
        navPanel.add(Box.createHorizontalGlue());
        navPanel.add(favoriteButton);
        navPanel.add(Box.createHorizontalGlue());
        navPanel.add(nextButton);

        frame.add(navPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(Color.decode("#FEFAE0"));
        btn.setForeground(Color.decode("#D4A373"));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        //btn.setBorder(new LineBorder(Color.decode("#FEFAE0"), 1, true));
    }

    private void loadImagesFromTheme(String theme) {
        currentImages.clear();
        currentIndex = 0;

        try (Connection con = DBUtil.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT image_path FROM suggested_palettes WHERE theme = ?");
            ps.setString(1, theme);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                currentImages.add(rs.getString("image_path"));
            }

            if (!currentImages.isEmpty()) {
                showImage(0);
            } else {
                imageLabel.setIcon(null);
                imageLabel.setText("No images found for theme: " + theme);
            }

        } catch (Exception e) {
            e.printStackTrace();
            imageLabel.setIcon(null);
            imageLabel.setText("Database error.");
        }
    }

    private void showImage(int index) {
        if (index >= 0 && index < currentImages.size()) {
            currentIndex = index;
            String path = currentImages.get(currentIndex);
            File imgFile = new File(path);

            if (imgFile.exists()) {
                ImageIcon icon = new ImageIcon(path);
                Image scaled = icon.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaled));
                imageLabel.setText(null);
            } else {
                imageLabel.setIcon(null);
                imageLabel.setText("Image not found: " + path);
            }
        }
    }

    private void saveToFavorites() {
        if (currentImages.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No image selected.");
            return;
        }

        String imagePath = currentImages.get(currentIndex);

        try (Connection con = DBUtil.getConnection()) {
            PreparedStatement check = con.prepareStatement(
                "SELECT COUNT(*) FROM favorite_palettes WHERE image_path = ?");
            check.setString(1, imagePath);
            ResultSet rs = check.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(frame, "Already added to Favorites.");
                return;
            }

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO favorite_palettes (image_path) VALUES (?)");
            ps.setString(1, imagePath);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Added to Favorites!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error saving to Favorites.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SuggestedPaletteViewer::new);
    }
}