package Client;

import API.DentalService;
import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AppointmentBookingScreen extends JFrame {

    private DentalService dentalService;
    private String email;
    private JSpinner datePicker;
    private JComboBox<String> timeSlotComboBox;
    private JTextArea reasonTextArea;

    // Define the date format as day-month-year
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public AppointmentBookingScreen(DentalService dentalService, String email) {
        this.dentalService = dentalService;
        this.email = email;

        // Set up the JFrame
        setTitle("Book an Appointment - Delta Dentistry");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Top panel for title
        JPanel topPanel = new JPanel();
        JLabel titleLabel = new JLabel("Book an Appointment");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 200, 0));
        topPanel.add(titleLabel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Center panel for booking components using GridBagLayout for better alignment control
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0); // Add some vertical spacing

        // Date Picker
        JLabel dateLabel = new JLabel("Select Date:");
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center-align the label
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(dateLabel, gbc);

        datePicker = new JSpinner(new SpinnerDateModel());
        datePicker.setEditor(new JSpinner.DateEditor(datePicker, "dd-MM-yyyy")); // Set date format to dd-MM-yyyy
        datePicker.addChangeListener(e -> updateAvailableTimeSlots());
        gbc.gridy = 1;
        centerPanel.add(datePicker, gbc);

        // Time Slot Selection
        JLabel timeSlotLabel = new JLabel("Select Time Slot:");
        timeSlotLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center-align the label
        gbc.gridy = 2;
        centerPanel.add(timeSlotLabel, gbc);

        timeSlotComboBox = new JComboBox<>();
        gbc.gridy = 3;
        centerPanel.add(timeSlotComboBox, gbc);

        // Reason for Appointment
        JLabel reasonLabel = new JLabel("Reason for Visit:");
        reasonLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center-align the label
        gbc.gridy = 4;
        centerPanel.add(reasonLabel, gbc);

        reasonTextArea = new JTextArea(5, 20);
        reasonTextArea.setLineWrap(true);
        reasonTextArea.setWrapStyleWord(true);
        reasonTextArea.setAlignmentX(JTextArea.CENTER_ALIGNMENT); // Center-align the text in the text area
        gbc.gridy = 5;
        centerPanel.add(new JScrollPane(reasonTextArea), gbc);

        // Book Appointment Button
        RoundedButton bookButton = new RoundedButton("Book Appointment");
        
        bookButton.addActionListener(e -> bookAppointment());
        gbc.gridy = 6;
        centerPanel.add(bookButton, gbc);

        // Back to Main Menu Button
        RoundedButton backButton = new RoundedButton("Back to Main Menu");
        
        backButton.addActionListener(e -> goBackToMainMenu());
        gbc.gridy = 7;
        centerPanel.add(backButton, gbc);

        // Add panels to frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);

        // Initialize available time slots on load
        updateAvailableTimeSlots();
    }

    private void updateAvailableTimeSlots() {
        LocalDate selectedDate = ((java.util.Date) datePicker.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // Use 'dd-MM-yyyy'

        try {
            List<String> availableSlots = dentalService.getAvailableTimeSlots(selectedDate.format(formatter)); // Send 'dd-MM-yyyy' to server
            timeSlotComboBox.removeAllItems();
            for (String slot : availableSlots) {
                timeSlotComboBox.addItem(slot);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching available time slots: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bookAppointment() {
        LocalDate selectedDate = ((java.util.Date) datePicker.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        String selectedTimeSlot = (String) timeSlotComboBox.getSelectedItem();
        String reason = reasonTextArea.getText();

        try {
            if (selectedTimeSlot == null || selectedTimeSlot.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a time slot.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(reason.equals("")){
                JOptionPane.showMessageDialog(this, "Please enter your reason.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = dentalService.bookAppointment(email, selectedDate.toString(), selectedTimeSlot, reason);
            if (success) {
                JOptionPane.showMessageDialog(this, "Appointment booked successfully!");
                // Return to the main screen after booking
                new DentalMainScreen(dentalService, email);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "You already have an appointment for this date.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RemoteException e) {
            // Display only the message from the cause
            JOptionPane.showMessageDialog(this, e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error booking appointment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to go back to the main menu
    private void goBackToMainMenu() {
        new DentalMainScreen(dentalService, email);
        dispose(); // Close the current screen
    }

    

   
}
