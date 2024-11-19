package Client;

import API.DentalService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AppointmentHistoryScreen extends JFrame {

    private DentalService dentalService;
    private String email;
    private JComboBox<String> dateComboBox;
    private JTextArea reasonTextArea;
    private JLabel timeLabel;
    private JLabel statusLabel;

    // Define the date format as day-month-year
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public AppointmentHistoryScreen(DentalService dentalService, String email) {
        this.dentalService = dentalService;
        this.email = email;

        // Set up the JFrame
        setTitle("View Appointment History - Delta Dentistry");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Top panel for title
        JPanel topPanel = new JPanel();
        JLabel titleLabel = new JLabel("View Appointment History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 200, 0));
        topPanel.add(titleLabel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Center panel for layout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Date ComboBox for selecting available dates
        dateComboBox = new JComboBox<>();
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        centerPanel.add(dateComboBox, gbc);

        // Time Label
        timeLabel = new JLabel("");
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(new JLabel("Time:"), gbc);
        gbc.gridx = 1;
        centerPanel.add(timeLabel, gbc);

        // Reason TextArea
        reasonTextArea = new JTextArea(5, 20);
        reasonTextArea.setEditable(false);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        centerPanel.add(new JLabel("Reason:"), gbc);
        gbc.gridy = 3;
        centerPanel.add(new JScrollPane(reasonTextArea), gbc);

        // Status Label
        statusLabel = new JLabel("");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        centerPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        centerPanel.add(statusLabel, gbc);

        // Button to load appointments
        RoundedButton loadButton = new RoundedButton("Show Appointments");
        styleButton(loadButton, new Dimension(200, 40));
        loadButton.addActionListener(e -> showAppointmentsForSelectedDate());
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        centerPanel.add(loadButton, gbc);

        // Back to Main Menu Button
        RoundedButton backButton = new RoundedButton("Back to Main Menu");
        styleButton(backButton, new Dimension(200, 40));
        backButton.addActionListener(e -> {
            new DentalMainScreen(dentalService, email);
            dispose(); // Close the current screen
        });
        gbc.gridy = 6;
        centerPanel.add(backButton, gbc);

        // Add panels to frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);

        // Load available dates when initializing the screen
        loadAvailableDates();
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

private void loadAvailableDates() {
    try {
        List<String> availableDates = dentalService.getAvailableAppointmentDates(email);
        dateComboBox.removeAllItems(); // Clear existing items

        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (String date : availableDates) {
            // Directly use the date if it's already in 'dd-MM-yyyy'
            LocalDate localDate = LocalDate.parse(date, displayFormatter);
            dateComboBox.addItem(localDate.format(displayFormatter)); // Keep format as 'dd-MM-yyyy' for display
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error fetching available dates: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}




private void showAppointmentsForSelectedDate() {
    String selectedDate = (String) dateComboBox.getSelectedItem();
    
    if (selectedDate == null || selectedDate.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please select a valid date.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        List<String> appointmentDetails = dentalService.getAppointmentHistory(email, selectedDate);

        // Clear previous details
        timeLabel.setText("");
        reasonTextArea.setText("");
        statusLabel.setText("");

        if (appointmentDetails.isEmpty()) {
            reasonTextArea.setText("No appointments found for this date.");
        } else {
            for (String detail : appointmentDetails) {
                String[] parts = detail.split(", ");

                if (parts.length >= 4) {
                    try {
                        String time = parts[1].split(": ")[1];
                        String reason = parts[2].split(": ")[1];
                        String status = parts[3].split(": ")[1];

                        timeLabel.setText(time);
                        reasonTextArea.setText(reason);
                        statusLabel.setText(status);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        JOptionPane.showMessageDialog(this, "Error: Appointment details are incorrectly formatted.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Handle cases where the split does not result in 4 parts
                    JOptionPane.showMessageDialog(this, "Error: Appointment details are incomplete or missing.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();  // Print the stack trace to the console
        JOptionPane.showMessageDialog(this, "Error fetching appointment details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    
}
