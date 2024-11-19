/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Admin;

import API.DentalService;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/**
 *
 * @author Shamal Prasad
 */
public class Adminlogin extends JFrame {
    
    private DentalService dentalService;
    
    // Method to add placeholder text to JTextField and JPasswordField
    private void setPlaceholder(JTextComponent field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('•');
                    }
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }
            }
        });
        if (field instanceof JPasswordField) {
            ((JPasswordField) field).setEchoChar((char) 0); // Make the password field display text like a normal field initially
        }
    }

    // Custom class to handle placeholders in JPasswordField
    static class CustomPasswordField extends JPasswordField {
        private String placeholder;

        public CustomPasswordField(int columns) {
            super(columns);
        }

        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
            setText(placeholder);
            setForeground(Color.GRAY);
            setEchoChar((char) 0);
        }

        public String getPlaceholder() {
            return placeholder;
        }
    }

    public Adminlogin(DentalService dentalService) {
        this.dentalService = dentalService;

        // Set the title of the JFrame
        setTitle("Delta Dentistry");

        // Create components
        JLabel titleLabel = new JLabel("Delta Dentistry");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22)); // Adjust font size as needed
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(0, 200, 0)); // Set text color to green
        
        final int TEXTBOX_SIZE = 20;
        // JTextField for username with placeholder
        JTextField usernameField = new JTextField(TEXTBOX_SIZE);
        setPlaceholder(usernameField, "Username");
        styleTextField(usernameField);

        // CustomPasswordField for password with placeholder
        CustomPasswordField passwordField = new CustomPasswordField(TEXTBOX_SIZE);
        setPlaceholder(passwordField, "Password");
        styleTextField(passwordField);

        // Create rounded button for login
        RoundedButton loginButton = new RoundedButton("Login");
        loginButton.setBackground(new Color(0, 200, 0));
        loginButton.setForeground(Color.BLACK);
        loginButton.setPreferredSize(new Dimension(150, 30));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> loginUser(usernameField.getText(), passwordField.getText()));

        

        

        // Create a JPanel to hold the components
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center alignment for main panel

        // Add components to the panel with spacing
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(usernameField);
        mainPanel.add(Box.createVerticalStrut(10));

        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(15));

        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(loginButton);
        mainPanel.add(Box.createVerticalStrut(20));

        

        

        // Set layout for the content pane to center the main panel
        setLayout(new GridBagLayout());
        add(mainPanel);

        // Pack the frame to fit all components
        pack();

        // Set default close operation and size of the JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);  // Increased size for better fit
        setLocationRelativeTo(null); // Center the window

        // Prevent focus on text fields when the window is first displayed
        setFocusableWindowState(false);
        setVisible(true);
        setFocusableWindowState(true);

        // Request focus on the frame itself to prevent initial focus on the text field
        this.requestFocusInWindow();
    }
    private void loginUser(String username, String password) {
        try {
            if (dentalService.loginUser(username, password)) {
                JOptionPane.showMessageDialog(this, "Login successful!");

                // Check if the user is an admin
                if (dentalService.isAdminUser(username)) {
                    new AdminMainScreen(dentalService, username);  // Open admin main screen
                } 

                dispose();  // Close the login form
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error logging in: " + e.getMessage());
        }
    }


    // Method to style text fields
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 200, 0), 1), 
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }
    public static void main(String[] args) {
        
        
            
            
            try {
            // Locate the RMI registry and get the remote object
            Registry registry = LocateRegistry.getRegistry(420);
            DentalService dentalService = (DentalService) Naming.lookup("rmi://localhost:420/DentalService");
            
            // Launch the client GUI
            SwingUtilities.invokeLater(() -> new Adminlogin(dentalService));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error connecting to server: " + e.getMessage());
        }
    }
    
}
