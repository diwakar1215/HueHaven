import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class LoginFrame {
    public LoginFrame() {
        // Frame Setup
        JFrame f = new JFrame("HueHaven");
        f.setSize(540, 500);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.getContentPane().setBackground(Color.decode("#FEFAE0"));
        f.setLayout(null);

        // App Icon
        ImageIcon icon = new ImageIcon("images/bright.png");
        f.setIconImage(icon.getImage());

        // Fonts
        Font headingFont = new Font("Segoe UI", Font.BOLD, 28);
        Font labelFont = new Font("Segoe UI", Font.BOLD, 22);
        Font fieldFont = new Font("Segoe UI", Font.ITALIC, 18);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 30);

        // Top Welcome Message
        JLabel heading = new JLabel("Welcome Back to HueHaven", JLabel.CENTER);
        heading.setFont(headingFont);
        heading.setForeground(Color.decode("#D4A373"));
        heading.setBounds(0, 20, 520, 40);
        f.add(heading);

        // Username Label
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(labelFont);
        userLabel.setForeground(Color.decode("#D4A373"));
        userLabel.setBounds(60, 80, 400, 30);
        f.add(userLabel);

        // Username Field
        JTextField tf = new JTextField();
        tf.setFont(fieldFont);
        tf.setBounds(60, 115, 400, 40);
        tf.setBorder(new LineBorder(Color.decode("#E9EDC9"), 2, true));
        f.add(tf);

        // Password Label
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        passLabel.setForeground(Color.decode("#D4A373"));
        passLabel.setBounds(60, 170, 400, 30);
        f.add(passLabel);

        // Password Field
        JPasswordField pf = new JPasswordField();
        pf.setFont(fieldFont);
        pf.setBounds(60, 205, 400, 40);
        pf.setBorder(new LineBorder(Color.decode("#E9EDC9"), 2, true));
        f.add(pf);

        // Login Button
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(buttonFont);
        loginBtn.setBounds(60, 270, 400, 55);
        loginBtn.setBackground(Color.decode("#E9EDC9"));
        loginBtn.setForeground(Color.decode("#D4A373"));
        loginBtn.setFocusPainted(false);
        f.add(loginBtn);

        // Reset Password Button
        JButton resetBtn = new JButton("Reset Password");
        resetBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        resetBtn.setBounds(180, 345, 160, 35);
        resetBtn.setBackground(Color.decode("#FAEDCD"));
        resetBtn.setVisible(false);
        f.add(resetBtn);

        // Login Logic
        loginBtn.addActionListener(e -> {
            String username = tf.getText().trim();
            String password = new String(pf.getPassword()).trim();

            try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/color_palette_app", "root", "shubham@#1215")) {

                String sql = "SELECT password FROM users WHERE username = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    if (password.equals(rs.getString("password"))) {
                        JOptionPane.showMessageDialog(f, "Login Successful!");
                        f.dispose();
                        new PaletteHome(username); // Open user dashboard
                    } else {
                        JOptionPane.showMessageDialog(f, "Incorrect password!");
                        resetBtn.setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(f, "User not found!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage());
            }
        });

        // Reset Logic
        resetBtn.addActionListener(e -> {
            String username = tf.getText().trim();
            String newPass = JOptionPane.showInputDialog(f, "Enter new password:");

            if (newPass == null || newPass.trim().isEmpty()) {
                JOptionPane.showMessageDialog(f, "Password reset cancelled.");
                return;
            }

            try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/color_palette_app", "root", "shubham@#1215")) {

                String update = "UPDATE users SET password = ? WHERE username = ?";
                PreparedStatement ps = conn.prepareStatement(update);
                ps.setString(1, newPass.trim());
                ps.setString(2, username);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(f, "Password reset successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage());
            }
        });

        f.setVisible(true);
    }
}