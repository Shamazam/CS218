package Client;

import API.DentalService;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ChangePasswordScreen extends JFrame {

    private DentalService dentalService;
    private String email;

    public ChangePasswordScreen(DentalService dentalService, String email) {
        this.dentalService = dentalService;
        this.email = email;

        // Set up the JFrame
        setTitle("Delta Dentistry - Change Password");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Top panel for title message
        JPanel topPanel = new JPanel();
        JLabel titleLabel = new JLabel("Change Password");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 200, 0)); // Set text color to green
        topPanel.add(titleLabel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Center panel for password fields
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
        
        final int TEXTBOX_SIZE = 20;
        // Text fields for passwords
        CustomPasswordField currentPasswordField = new CustomPasswordField(TEXTBOX_SIZE);
        setPlaceholder(currentPasswordField, "Current Password");
        styleTextField(currentPasswordField);

        CustomPasswordField newPasswordField = new CustomPasswordField(TEXTBOX_SIZE);
        setPlaceholder(newPasswordField, "New Password");
        styleTextField(newPasswordField);

        CustomPasswordField confirmPasswordField = new CustomPasswordField(TEXTBOX_SIZE);
        setPlaceholder(confirmPasswordField, "Confirm New Password");
        styleTextField(confirmPasswordField);

        // Add fields to center panel with spacing
        centerPanel.add(currentPasswordField);
        centerPanel.add(Box.createVerticalStrut(15)); // Space between fields
        centerPanel.add(newPasswordField);
        centerPanel.add(Box.createVerticalStrut(15)); // Space between fields
        centerPanel.add(confirmPasswordField);
        centerPanel.add(Box.createVerticalStrut(30)); // Space before button

        // Button to submit the change
        RoundedButton submitButton = new RoundedButton("Change Password");
        styleButton(submitButton, new Dimension(200, 40));
        submitButton.addActionListener(e -> changePassword(
            new String(currentPasswordField.getPassword()),
            new String(newPasswordField.getPassword()),
            new String(confirmPasswordField.getPassword())
        ));
        centerPanel.add(submitButton);

        // Add back to main menu button
        RoundedButton backButton = new RoundedButton("Back to Main Menu");
        styleButton(backButton, new Dimension(200, 40));
        backButton.addActionListener(e -> goBackToMainMenu());
        centerPanel.add(Box.createVerticalStrut(15)); // Space before back button
        centerPanel.add(backButton);

        // Add panels to the frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);

        // Correct the focus issue by forcing the frame to refocus
        SwingUtilities.invokeLater(() -> {
            setFocusableWindowState(false);
            setFocusableWindowState(true);
            requestFocus();
        });
    }

    // Method to style buttons
    private void styleButton(RoundedButton button, Dimension size) {
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(0, 200, 0)); // Set button background to green
        button.setMaximumSize(size);
        button.setMinimumSize(size);
        button.setPreferredSize(size);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
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

    // Method to change the password
    private void changePassword(String currentPassword, String newPassword, String confirmPassword) {
        try {
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "New passwords do not match. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dentalService.changePassword(email, currentPassword, newPassword)) {
                JOptionPane.showMessageDialog(this, "Password changed successfully!");
                dispose(); // Close the Change Password screen

                // Redirect to the login screen after successful password change
                SwingUtilities.invokeLater(() -> new DeltaDentistryLogin(dentalService));
            } else {
                JOptionPane.showMessageDialog(this, "Current password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error changing password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to go back to the main menu
    private void goBackToMainMenu() {
        new DentalMainScreen(dentalService, email);
        dispose(); // Close the current screen
    }

    
}
