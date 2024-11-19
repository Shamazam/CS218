package Dentist;

import API.DentalService;
import Client.DeltaDentistryLogin;

import javax.swing.*;
import java.awt.*;

public class DentistMainScreen extends JFrame {

    private DentalService dentalService;

    public DentistMainScreen(DentalService dentalService) {
        this.dentalService = dentalService;

        // Set up the JFrame
        setTitle("Dentist Dashboard - Delta Dentistry");
        setSize(400, 450); // Increased height slightly for better spacing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Top panel for title
        JPanel topPanel = new JPanel();
        JLabel titleLabel = new JLabel("Dentist Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 200, 0)); // Set text color to green
        topPanel.add(titleLabel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Center panel for buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        // Set a fixed button size for consistency and increase width
        Dimension buttonSize = new Dimension(300, 50);

        // Create buttons for different admin functions
        RoundedButton viewAppointmentsButton = new RoundedButton("View Appointment Requests");
        styleButton(viewAppointmentsButton, buttonSize);
        viewAppointmentsButton.addActionListener(e -> viewAppointmentRequests());

        RoundedButton viewApprovedButton = new RoundedButton("View Approved/Rejected Appointments");
        styleButton(viewApprovedButton, buttonSize);
        viewApprovedButton.addActionListener(e -> viewApprovedAppointments());

        RoundedButton logoutButton = new RoundedButton("Logout");
        styleButton(logoutButton, buttonSize);
        logoutButton.addActionListener(e -> logout());

        // Add buttons to center panel with spacing
        centerPanel.add(viewAppointmentsButton);
        centerPanel.add(Box.createVerticalStrut(20)); // Space between buttons
        centerPanel.add(viewApprovedButton);
        centerPanel.add(Box.createVerticalStrut(20)); // Space between buttons
        centerPanel.add(logoutButton);

        // Add panels to the frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
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

    

    // Method to handle viewing appointment requests
    private void viewAppointmentRequests() {
        new ViewAppointmentRequestsScreen(dentalService);  // Instantiate the screen to view appointment requests
        dispose(); // Close the current admin main screen
    }
    
    private void viewApprovedAppointments() {
        new ViewApprovedRejectedAppointmentsScreen(dentalService);
        dispose();
    }

    // Method to handle logout functionality
    private void logout() {
        new DeltaDentistryLogin(dentalService);
        dispose(); // Close the current admin screen
    }
}
