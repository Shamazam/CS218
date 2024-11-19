package Client;

import API.DentalService;

import javax.swing.*;
import java.awt.*;

public class DentalMainScreen extends JFrame {

    private DentalService dentalService;
    private String email;

    public DentalMainScreen(DentalService dentalService, String email) {
        this.dentalService = dentalService;
        this.email = email;

        // Set up the JFrame
        setTitle("Delta Dentistry");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Top panel for welcome message
        JPanel topPanel = new JPanel();
        JLabel welcomeLabel = new JLabel("Welcome to Delta Dentistry");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(0, 200, 0)); // Set text color to green
        topPanel.add(welcomeLabel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Center panel for buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        // Set a fixed button size for consistency
        Dimension buttonSize = new Dimension(250, 40);

        // Create the buttons
        RoundedButton makeAppointmentButton = new RoundedButton("Make an Appointment");
        styleButton(makeAppointmentButton, buttonSize);
        makeAppointmentButton.addActionListener(e -> openAppointmentBookingScreen());

        RoundedButton viewInboxButton = new RoundedButton("View Inbox");
        styleButton(viewInboxButton, buttonSize);
         viewInboxButton.addActionListener(e -> openInboxScreen());

        RoundedButton viewAppointmentHistoryButton = new RoundedButton("View Appointment History");
        styleButton(viewAppointmentHistoryButton, buttonSize);
        viewAppointmentHistoryButton.addActionListener(e -> openAppointmentHistoryScreen());  

        RoundedButton changePasswordButton = new RoundedButton("Change Password");
        styleButton(changePasswordButton, buttonSize);
        changePasswordButton.addActionListener(e -> openChangePasswordScreen());

        RoundedButton logoutButton = new RoundedButton("Logout");
        styleButton(logoutButton, buttonSize);
        logoutButton.addActionListener(e -> logout());

        // Add buttons to center panel with spacing
        centerPanel.add(makeAppointmentButton);
        centerPanel.add(Box.createVerticalStrut(20)); // Space between buttons
        centerPanel.add(viewInboxButton);
        centerPanel.add(Box.createVerticalStrut(20)); // Space between buttons
        centerPanel.add(viewAppointmentHistoryButton);
        centerPanel.add(Box.createVerticalStrut(20)); // Space between buttons
        centerPanel.add(changePasswordButton);
        centerPanel.add(Box.createVerticalStrut(20)); // Space between buttons
        centerPanel.add(logoutButton);

        // Add panels to the frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // Method to open AppointmentBookingScreen
    private void openAppointmentBookingScreen() {
        new AppointmentBookingScreen(dentalService, email);
        dispose();  // Close the current main screen
    }

    // Method to open AppointmentHistoryScreen
    private void openAppointmentHistoryScreen() {
        new AppointmentHistoryScreen(dentalService, email); // Opens the Appointment History Screen
        dispose();  // Close the current main screen
    }

    // Method to open ChangePasswordScreen
    private void openChangePasswordScreen() {
        new ChangePasswordScreen(dentalService, email);
        dispose();  // Close the current main screen
    }

    // Method to handle logout
    private void logout() {
        new DeltaDentistryLogin(dentalService);
        dispose();  // Close the current main screen
    }
    
    private void openInboxScreen() {
        new ViewInboxScreen(dentalService, email);
        dispose();  // Close the current main screen
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

    

    // Removed the main method as this class should not be run directly
}
