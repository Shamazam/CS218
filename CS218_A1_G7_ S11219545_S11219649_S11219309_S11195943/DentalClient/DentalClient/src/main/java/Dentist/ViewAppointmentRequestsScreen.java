package Dentist;

import API.DentalService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewAppointmentRequestsScreen extends JFrame {

    private DentalService dentalService;
    private JTable appointmentTable;
    private DefaultTableModel tableModel;

    public ViewAppointmentRequestsScreen(DentalService dentalService) {
        this.dentalService = dentalService;

        // Set up the JFrame
        setTitle("View Appointment Requests - Delta Dentistry");
        setSize(800, 600); // Increased height slightly to accommodate all buttons
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Top panel for title
        JPanel topPanel = new JPanel();
        JLabel titleLabel = new JLabel("Appointment Requests");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 200, 0)); // Set text color to green
        topPanel.add(titleLabel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Center panel for the table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Set up the table model and table
        tableModel = new DefaultTableModel(new Object[]{"ID", "User Email", "Date", "Time Slot", "Reason"}, 0);
        appointmentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Added gap for better spacing

        // Set a fixed button size for consistency
        Dimension buttonSize = new Dimension(150, 40);

        // Create the buttons
        RoundedButton approveButton = new RoundedButton("Approve");
        
        approveButton.addActionListener(e -> approveAppointment());

        RoundedButton rejectButton = new RoundedButton("Reject");
        
        rejectButton.addActionListener(e -> rejectAppointment());

        RoundedButton refreshButton = new RoundedButton("Refresh");
        
        refreshButton.addActionListener(e -> loadAppointments());

        RoundedButton backButton = new RoundedButton("Back");
        
        backButton.addActionListener(e -> goBack());

        // Add buttons to the bottom panel
        bottomPanel.add(approveButton);
        bottomPanel.add(rejectButton);
        bottomPanel.add(refreshButton);
        bottomPanel.add(backButton); // Make sure the back button is added here

        // Add panels to the frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);

        // Load the appointments into the table
        loadAppointments();
    }

    private void loadAppointments() {
        try {
            List<String> pendingAppointments = dentalService.getPendingAppointments();
            tableModel.setRowCount(0); // Clear existing rows

            for (String appointment : pendingAppointments) {
                String[] parts = appointment.split(","); // Updated to split by comma

                if (parts.length >= 5) { // Check if there are enough parts to avoid ArrayIndexOutOfBoundsException
                    String id = parts[0].trim();
                    String userEmail = parts[1].trim();
                    String date = parts[2].trim();
                    String timeSlot = parts[3].trim();
                    String reason = parts[4].trim();

                    tableModel.addRow(new Object[]{id, userEmail, date, timeSlot, reason});
                } else {
                    // Log or handle unexpected data format here
                    System.err.println("Unexpected appointment format: " + appointment);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void approveAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow >= 0) {
            String appointmentId = (String) tableModel.getValueAt(selectedRow, 0);
            try {
                boolean success = dentalService.approveAppointment(Integer.parseInt(appointmentId));
                if (success) {
                    JOptionPane.showMessageDialog(this, "Appointment approved successfully.");
                    loadAppointments();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to approve appointment. Potentially taken.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error approving appointment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an appointment to approve.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void rejectAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow >= 0) {
            String appointmentId = (String) tableModel.getValueAt(selectedRow, 0);
            String reason = JOptionPane.showInputDialog(this, "Enter reason for rejection:");

            if (reason != null && !reason.trim().isEmpty()) {
                try {
                    boolean success = dentalService.rejectAppointment(Integer.parseInt(appointmentId), reason);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Appointment rejected successfully.");
                        loadAppointments();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to reject appointment.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error rejecting appointment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Rejection reason cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an appointment to reject.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    // New method to handle going back to the Admin Main Screen
    private void goBack() {
        new DentistMainScreen(dentalService); // Reopen the Admin Main Screen
        dispose(); // Close the current View Appointment Requests screen
    }

    

    

    // Removed the main method as this class should not be run directly
}
