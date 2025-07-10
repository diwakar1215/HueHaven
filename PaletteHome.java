import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class PaletteHome {
    public PaletteHome(String username) {
        JFrame f = new JFrame("Welcome to HueHaven " + username);
        f.setSize(600, 500);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setLayout(null);
        f.setResizable(false);

        f.getContentPane().setBackground(Color.decode("#FEFAE0")); 


        ImageIcon icon = new ImageIcon("images/bright.png");
        f.setIconImage(icon.getImage());

        ImageIcon icon2 = new ImageIcon("images/HueHaven.png");
        Image image = icon2.getImage().getScaledInstance(250, 100, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(image));
        logo.setBounds((600 - 250) / 2, 30, 250, 100); 
        f.add(logo);

      
        Font titleFont = new Font("Segoe UI", Font.BOLD, 28);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 20);

       
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", JLabel.CENTER);
        welcomeLabel.setFont(titleFont);
        welcomeLabel.setForeground(Color.decode("#D4A373"));
        welcomeLabel.setBounds(100, 210, 400, 40);
        f.add(welcomeLabel);

      
        JLabel infoLabel = new JLabel(
            "<html> <div style='text-align:center;'>HueHaven - Your personalized color palette manager. <br>" +
            "Build and discover palettes for design, art, and more!</div></html>",
            JLabel.CENTER
        );
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoLabel.setForeground(Color.decode("#7F5539"));
        infoLabel.setBounds(100, 160, 400, 60);
        f.add(infoLabel);


        JButton createPaletteBtn = new JButton("🎨 Create Your Own Palette");
        createPaletteBtn.setFont(buttonFont);
        createPaletteBtn.setBounds(150, 270, 300, 50);
        createPaletteBtn.setBackground(Color.decode("#E9EDC9"));
        createPaletteBtn.setForeground(Color.decode("#D4A373"));
        createPaletteBtn.setFocusPainted(false);
        createPaletteBtn.setBorder(new LineBorder(Color.decode("#D4A373"), 2, true));
        createPaletteBtn.setToolTipText("""
            -> Pick your favorite colors
            -> Save and manage your palettes
            -> Use them in your projects!
        """);
        f.add(createPaletteBtn);

        JButton suggestedPaletteBtn = new JButton("🧠 Suggested Palette");
        suggestedPaletteBtn.setFont(buttonFont);
        suggestedPaletteBtn.setBounds(150, 340, 300, 50);
        suggestedPaletteBtn.setBackground(Color.decode("#E9EDC9"));
        suggestedPaletteBtn.setForeground(Color.decode("#D4A373"));
        suggestedPaletteBtn.setFocusPainted(false);
        suggestedPaletteBtn.setBorder(new LineBorder(Color.decode("#D4A373"), 2, true));
        suggestedPaletteBtn.setToolTipText("""
            -> Get color ideas based on themes
            -> Preview palettes with images
            -> Add suggested palettes to your collection!
        """);
        f.add(suggestedPaletteBtn);

     
        createPaletteBtn.addActionListener(e -> { 
            new Color_Picker(username);
        });

        suggestedPaletteBtn.addActionListener(e -> {
            new SuggestedPaletteViewer();
        });

        f.setVisible(true);
    }

    public static void main(String[] args) {
        new PaletteHome("TestUser");
    }
}
