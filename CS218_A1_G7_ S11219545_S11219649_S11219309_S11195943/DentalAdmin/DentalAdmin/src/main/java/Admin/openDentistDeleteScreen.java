/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Admin;

import API.DentalService;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Shamal Prasad
 */
public class openDentistDeleteScreen extends JFrame{
    private DentalService dentalService;
    private String email;
    
    public openDentistDeleteScreen(DentalService dentalService, String email){
        
        this.dentalService = dentalService;
        this.email = email;
        
         // Set the title of the JFrame
        setTitle("Delta Dentistry");

        // Create components
        JLabel titleLabel = new JLabel("Delta Dentistry");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22)); // Adjust font size as needed
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(0, 200, 0)); // Set text color to green

        // Instruction label for entering details
        JLabel instructionLabel = new JLabel("Enter Dentist Account details below:");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        final int TEXTBOX_SIZE = 20;
        // JTextField for email with placeholder
        JTextField emailField = new JTextField(TEXTBOX_SIZE);
        setPlaceholder(emailField, "Email");
        styleTextField(emailField);
        
        // Text fields for passwords
        CustomPasswordField currentPasswordField = new CustomPasswordField(TEXTBOX_SIZE);
        setPlaceholder(currentPasswordField, "Current Password");
        styleTextField(currentPasswordField);

        
        CustomPasswordField confirmPasswordField = new CustomPasswordField(TEXTBOX_SIZE);
        setPlaceholder(confirmPasswordField, "Confirm Password");
        styleTextField(confirmPasswordField);
        
         // Create rounded button for register using your RoundedButton class
        RoundedButton updateButton = new RoundedButton("Delete Account");
        updateButton.setBackground(new Color(0, 200, 0));
        updateButton.setForeground(Color.BLACK);
        updateButton.setPreferredSize(new Dimension(150, 30));
        
         updateButton.addActionListener(e -> deleteDentist(
            emailField.getText(),
            
            new String(currentPasswordField.getPassword()),
            
            new String(confirmPasswordField.getPassword())
        ));
         
         RoundedButton gobackButton = new RoundedButton("Go Back");
        gobackButton.setBackground(new Color(0, 200, 0));
        gobackButton.setForeground(Color.BLACK);
        gobackButton.setPreferredSize(new Dimension(150, 30));
        gobackButton.addActionListener(e -> goBackToMainMenu());
        
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

        currentPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(currentPasswordField);
        mainPanel.add(Box.createVerticalStrut(20));

        

        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(confirmPasswordField);
        mainPanel.add(Box.createVerticalStrut(20));

        updateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(updateButton);
        mainPanel.add(Box.createVerticalStrut(10));
        
        gobackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(gobackButton);
        mainPanel.add(Box.createVerticalStrut(10));

        
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
    
    // Method to register user using RMI
    private void deleteDentist(String email, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean success = dentalService.deleteDentist(email,password );
            if (success) {
                JOptionPane.showMessageDialog(this, "Delete successful!");
                new AdminMainScreen(dentalService, email);
                dispose(); // Close the current screen
                
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed. Username may already exist.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage());
        }
    }
    
    // Method to go back to the main menu
    private void goBackToMainMenu() {
        new AdminMainScreen(dentalService, email);
        dispose(); // Close the current screen
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
}
