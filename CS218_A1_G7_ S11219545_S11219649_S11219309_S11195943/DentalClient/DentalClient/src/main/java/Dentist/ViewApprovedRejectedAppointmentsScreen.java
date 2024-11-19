package Dentist;

import API.DentalService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewApprovedRejectedAppointmentsScreen extends JFrame {

    private DentalService dentalService;
    private JTable appointmentTable;
    private DefaultTableModel tableModel;

    public ViewApprovedRejectedAppointmentsScreen(DentalService dentalService) {
        this.dentalService = dentalService;

        // Set up the JFrame
        setTitle("View Approved/Rejected Appointments - Delta Dentistry");
        setSize(800, 500); // Set size for better spacing
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Top panel for title
        JPanel topPanel = new JPanel();
        JLabel titleLabel = new JLabel("Approved/Rejected Appointments");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 200, 0)); // Set text color to green
        topPanel.add(titleLabel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Center panel for the table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Set up the table model and table
        tableModel = new DefaultTableModel(new Object[]{"ID", "User Email", "Date", "Time Slot", "Status", "Rejection Reason"}, 0);
        appointmentTable = new JTable(tableModel);
        
        // Set custom renderer for "Rejection Reason" column to wrap text
        appointmentTable.getColumnModel().getColumn(5).setCellRenderer(new TextAreaRenderer());

        // Adjust row height for multiline content
        appointmentTable.setRowHeight(appointmentTable.getRowHeight() * 2);

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel bottomPanel = new JPanel();
        RoundedButton backButton = new RoundedButton("Back");
        
        backButton.addActionListener(e -> goBack());

        bottomPanel.add(backButton);

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
            List<String> approvedRejectedAppointments = dentalService.getApprovedRejectedAppointments();
            tableModel.setRowCount(0); // Clear existing rows

            for (String appointment : approvedRejectedAppointments) {
                String[] parts = appointment.split(",");

                if (parts.length >= 5) {
                    String id = parts[0].trim();
                    String userEmail = parts[1].trim();
                    String date = parts[2].trim();
                    String timeSlot = parts[3].trim();
                    String status = parts[4].trim();
                    String rejectionReason = parts.length > 5 ? parts[5].trim() : ""; // Handle missing reason

                    tableModel.addRow(new Object[]{id, userEmail, date, timeSlot, status, rejectionReason});
                } else {
                    System.err.println("Unexpected appointment format: " + appointment);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Custom renderer for text wrapping
    private static class TextAreaRenderer extends DefaultTableCellRenderer {
        private final JTextArea textArea;

        public TextAreaRenderer() {
            textArea = new JTextArea();
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setOpaque(true); // This is necessary to ensure the background is painted
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int column) {
            textArea.setText(value != null ? value.toString() : "");
            if (isSelected) {
                textArea.setBackground(table.getSelectionBackground());
                textArea.setForeground(table.getSelectionForeground());
            } else {
                textArea.setBackground(table.getBackground());
                textArea.setForeground(table.getForeground());
            }
            textArea.setFont(table.getFont());
            return textArea;
        }
    }

    // Method to go back to the admin main screen
    private void goBack() {
        new DentistMainScreen(dentalService);
        dispose(); // Close the current screen
    }

    
}
