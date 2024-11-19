package Client;

import Dentist.DentistMainScreen;
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

public class DeltaDentistryLogin extends JFrame {

    private DentalService dentalService;

    public DeltaDentistryLogin(DentalService dentalService) {
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

        // Create register prompt
        JLabel registerPrompt = new JLabel("Don't have an account? ");
        registerPrompt.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel registerLink = new JLabel("Register here.");
        registerLink.setFont(new Font("Arial", Font.PLAIN, 12));
        registerLink.setForeground(Color.BLUE);

        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Implement the logic to open the registration form
                DeltaDentistryRegister registerForm = new DeltaDentistryRegister(dentalService);
                registerForm.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        setVisible(true); // Show the login form again when closing the registration form
                    }
                });
                setVisible(false); // Hide the login form
            }
        });

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

        // Panel to hold the register prompt and link
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerPanel.add(registerPrompt);
        registerPanel.add(registerLink);

        mainPanel.add(registerPanel);

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

    // Method to style text fields
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 200, 0), 1), 
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }

    // Method to check if the user is an admin
    private boolean isDentist(String username) {
        // Placeholder for actual check - Replace with your actual logic
        // Check if the username exists in the "Dentist Credentials" table
        // and has a role of "Dentist" or another admin role
        try {
            return dentalService.isDentistUser(username);  
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Updated login method
    private void loginUser(String username, String password) {
        try {
            if (dentalService.loginUser(username, password)) {
                JOptionPane.showMessageDialog(this, "Login successful!");

                // Check if the user is an admin
                if (isDentist(username)) {
                    new DentistMainScreen(dentalService);  // Open admin main screen
                } else {
                    new DentalMainScreen(dentalService, username);  // Open user main screen
                }

                dispose();  // Close the login form
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error logging in: " + e.getMessage());
        }
    }

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

    public static void main(String[] args) {
        try {
            // Locate the RMI registry and get the remote object
            Registry registry = LocateRegistry.getRegistry(420);
            DentalService dentalService = (DentalService) Naming.lookup("rmi://localhost:420/DentalService");
            
            // Launch the client GUI
            SwingUtilities.invokeLater(() -> new DeltaDentistryLogin(dentalService));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error connecting to server: " + e.getMessage());
        }
    }


}
