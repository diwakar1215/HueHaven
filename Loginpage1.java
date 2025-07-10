import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class Loginpage1 {
    public static void main(String[] args) {
        JFrame f = new JFrame("HueHaven");
        f.setSize(700, 700);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setResizable(false);

        f.getContentPane().setBackground(Color.decode("#FEFAE0")); 
        f.setLayout(null);
        ImageIcon icon = new ImageIcon("images/bright.png");
        f.setIconImage(icon.getImage());

        Font font1 = new Font("Segoe UI", Font.BOLD, 28);
        Font labelFont = new Font("Segoe UI", Font.BOLD, 22);

        JLabel heading = new JLabel("Get Started", JLabel.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 36));
        heading.setBounds(80, 40, 540, 50);
        heading.setForeground(Color.decode("#D4A373"));
        f.add(heading);

        JLabel subheading = new JLabel("Welcome to HueHaven - Let's create your account", JLabel.CENTER);
        subheading.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subheading.setBounds(80, 90, 540, 30);
        subheading.setForeground(Color.decode("#D4A373"));
        f.add(subheading);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(labelFont);
        userLabel.setForeground(Color.decode("#D4A373"));
        userLabel.setBounds(200, 150, 300, 30);
        f.add(userLabel);

        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", 3, 20));
        tf.setBounds(200, 190, 300, 50);
        tf.setBorder(new LineBorder(Color.decode("#E9EDC9"), 2, true));
        f.add(tf);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        passLabel.setForeground(Color.decode("#D4A373"));
        passLabel.setBounds(200, 260, 300, 30);
        f.add(passLabel);

        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Segoe UI", 3, 20));
        pf.setEchoChar('*'); 
        pf.setBounds(200, 300, 300, 50);
        pf.setBorder(new LineBorder(Color.decode("#E9EDC9"), 2, true));
        f.add(pf);

        JButton signUp = new JButton("Sign Up");
        signUp.setFont(font1);
        signUp.setBounds(200, 380, 300, 50);
        signUp.setBackground(Color.decode("#E9EDC9"));
        signUp.setForeground(Color.decode("#D4A373"));
        signUp.setFocusPainted(false);
        f.add(signUp);

        JLabel alreadyLabel = new JLabel("Already have an account?");
        alreadyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        alreadyLabel.setForeground(Color.decode("#D4A373"));
        alreadyLabel.setBounds(200, 450, 300, 30);
        alreadyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        f.add(alreadyLabel);

        JButton login = new JButton("Login");
        login.setFont(font1);
        login.setBounds(200, 490, 300, 50);
        login.setBackground(Color.decode("#E9EDC9"));
        login.setForeground(Color.decode("#D4A373"));
        login.setFocusPainted(false);
        f.add(login);

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/color_palette_app", "root", "shubham@#1215")) {
            String createTable = "CREATE TABLE IF NOT EXISTS users (username VARCHAR(255) PRIMARY KEY, password VARCHAR(255))";
            conn.createStatement().execute(createTable);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(f, "DB Error: " + e.getMessage());
        }

        signUp.addActionListener(e -> {
            String username = tf.getText().trim();
            String password = new String(pf.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(f, "Please fill all fields.");
                return;
            }

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/color_palette_app", "root", "shubham@#1215")) {
                String insert = "INSERT INTO users (username, password) VALUES (?, ?)";
                PreparedStatement ps = conn.prepareStatement(insert);
                ps.setString(1, username);
                ps.setString(2, password);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(f, "User Registered Successfully!");

                new PaletteHome(username);

                f.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(f, "User already exists or DB Error!");
            }
        });

        login.addActionListener(e -> {
            f.setVisible(false);
            new LoginFrame();
        });

        f.setVisible(true);
    }
}