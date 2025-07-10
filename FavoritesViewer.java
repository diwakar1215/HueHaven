import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class FavoritesViewer {
    private JFrame frame;
    private JLabel imageLabel;
    private JButton nextButton, prevButton;
    private ArrayList<String> favoriteImages = new ArrayList<>();
    private int currentIndex = 0;

    public FavoritesViewer() {
        frame = new JFrame("Your Favorite Palettes");
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(Color.decode("#FEFAE0"));

        imageLabel = new JLabel("No favorites yet!", SwingConstants.CENTER);
        imageLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        imageLabel.setForeground(Color.decode("#D4A373"));
        imageLabel.setBorder(new EmptyBorder(30, 30, 30, 30));
        frame.add(imageLabel, BorderLayout.CENTER);

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.X_AXIS));
        navPanel.setBorder(new EmptyBorder(10, 230, 20, 230));
        navPanel.setBackground(Color.decode("#FAEDCD"));

        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");

        for (JButton btn : new JButton[]{prevButton, nextButton}) {
            btn.setFocusPainted(false);
            btn.setBackground(Color.decode("#FEFAE0"));
            btn.setForeground(Color.decode("#D4A373"));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            btn.setBorder(new LineBorder(Color.decode("#FEFAE0"), 1, true));
        }

        prevButton.addActionListener(e -> showImage(currentIndex - 1));
        nextButton.addActionListener(e -> showImage(currentIndex + 1));

        navPanel.add(prevButton);
        navPanel.add(Box.createHorizontalGlue());
        navPanel.add(nextButton);

        frame.add(navPanel, BorderLayout.SOUTH);

        loadFavorites();
        frame.setVisible(true);
    }

    private void loadFavorites() {
        favoriteImages.clear();
        currentIndex = 0;

        try (Connection con = DBUtil.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT image_path FROM favorite_palettes");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                favoriteImages.add(rs.getString("image_path"));
            }

            if (!favoriteImages.isEmpty()) {
                showImage(0);
            } else {
                imageLabel.setIcon(null);
                imageLabel.setText("You haven't added any favorites yet.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            imageLabel.setIcon(null);
            imageLabel.setText("Error loading favorites.");
        }
    }

    private void showImage(int index) {
        if (index >= 0 && index < favoriteImages.size()) {
            currentIndex = index;
            String path = favoriteImages.get(currentIndex);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FavoritesViewer::new);
    }
}