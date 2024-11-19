package Client;

import API.DentalService;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DeltaDentistryRegister extends JFrame {

    private DentalService dentalService;

    public DeltaDentistryRegister(DentalService dentalService) {
        this.dentalService = dentalService;

        // Set the title of the JFrame
        setTitle("Delta Dentistry");

        // Create components
        JLabel titleLabel = new JLabel("Delta Dentistry");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22)); // Adjust font size as needed
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(0, 200, 0)); // Set text color to green

        // Instruction label for entering details
        JLabel instructionLabel = new JLabel("Enter your details below:");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        final int TEXTBOX_SIZE = 20;
        // JTextField for email with placeholder
        JTextField emailField = new JTextField(TEXTBOX_SIZE);
        setPlaceholder(emailField, "Email");
        styleTextField(emailField);

        // JTextField for first name with placeholder
        JTextField firstNameField = new JTextField(TEXTBOX_SIZE);
        setPlaceholder(firstNameField, "First Name");
        styleTextField(firstNameField);

        // JTextField for last name with placeholder
        JTextField lastNameField = new JTextField(TEXTBOX_SIZE);
        setPlaceholder(lastNameField, "Last Name");
        styleTextField(lastNameField);

        // JTextField for date of birth with placeholder
        JTextField dobField = new JTextField(TEXTBOX_SIZE);
        setPlaceholder(dobField, "Date of Birth (DD-MM-YYYY)");
        styleTextField(dobField);

        // CustomPasswordField for password with placeholder
        CustomPasswordField passwordField = new CustomPasswordField(TEXTBOX_SIZE);
        setPlaceholder(passwordField, "Password");
        styleTextField(passwordField);

        // CustomPasswordField for confirm password with placeholder
        CustomPasswordField confirmPasswordField = new CustomPasswordField(TEXTBOX_SIZE);
        setPlaceholder(confirmPasswordField, "Confirm Password");
        styleTextField(confirmPasswordField);

        // Create rounded button for register using your RoundedButton class
        RoundedButton registerButton = new RoundedButton("Register");
        registerButton.setBackground(new Color(0, 200, 0));
        registerButton.setForeground(Color.BLACK);
        registerButton.setPreferredSize(new Dimension(150, 30));
        registerButton.addActionListener(e -> registerUser(
            emailField.getText(),
            firstNameField.getText(),
            lastNameField.getText(),
            dobField.getText(),
            new String(passwordField.getPassword()),
            new String(confirmPasswordField.getPassword())
        ));

        // Panel to hold the login prompt
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.X_AXIS));
        loginPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel loginText = new JLabel("Already have an account? ");
        loginText.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel loginLink = new JLabel("Login here.");
        loginLink.setFont(new Font("Arial", Font.PLAIN, 12));
        loginLink.setForeground(Color.BLUE);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Open the login form
                new DeltaDentistryLogin(dentalService);
                dispose(); // Dispose of the current registration form
            }
        });

        loginPanel.add(loginText);
        loginPanel.add(loginLink);

        // Create a JPanel to hold the components
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // Add components to the panel with spacing
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(instructionLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(emailField);
        mainPanel.add(Box.createVerticalStrut(10));

        firstNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(firstNameField);
        mainPanel.add(Box.createVerticalStrut(10));

        lastNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lastNameField);
        mainPanel.add(Box.createVerticalStrut(10));

        dobField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(dobField);
        mainPanel.add(Box.createVerticalStrut(10));

        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(10));

        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(confirmPasswordField);
        mainPanel.add(Box.createVerticalStrut(20));

        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(registerButton);
        mainPanel.add(Box.createVerticalStrut(10));

        // Add login panel at the bottom
        mainPanel.add(loginPanel);

        // Set layout for the content pane to center the main panel
        setLayout(new GridBagLayout());
        add(mainPanel);

        // Pack the frame to fit all components
        pack();

        // Set default close operation and size of the JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);  // Increased size for better fit
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

    // Method to register user using RMI
    private void registerUser(String email, String firstName, String lastName, String dob, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean success = dentalService.registerUser(email, firstName, lastName, dob, password);
            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
                // Open the login form after successful registration
                new DeltaDentistryLogin(dentalService);
                dispose(); // Close the registration form
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Username may already exist.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error registering user: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(420);
            DentalService dentalService = (DentalService) Naming.lookup("rmi://localhost:420/DentalService");
            SwingUtilities.invokeLater(() -> new DeltaDentistryRegister(dentalService));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
