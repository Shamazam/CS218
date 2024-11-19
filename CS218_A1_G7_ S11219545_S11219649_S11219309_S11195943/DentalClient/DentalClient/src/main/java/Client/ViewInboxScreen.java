package Client;

import API.DentalService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ViewInboxScreen extends JFrame {

    private DentalService dentalService;
    private String email;
    private JTable inboxTable;
    private DefaultTableModel tableModel;

    public ViewInboxScreen(DentalService dentalService, String email) {
        this.dentalService = dentalService;
        this.email = email;

        // Set up the JFrame
        setTitle("Inbox - Delta Dentistry");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Top panel for title
        JPanel topPanel = new JPanel();
        JLabel titleLabel = new JLabel("Inbox");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 200, 0)); // Set text color to green
        topPanel.add(titleLabel);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Center panel for inbox messages
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Set up the table model and table
        tableModel = new DefaultTableModel(new Object[]{"Date", "Subject", "Message"}, 0);
        inboxTable = new JTable(tableModel) {
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                return new MultiLineCellRenderer();
            }
        };
        inboxTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(inboxTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for the back button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        RoundedButton backButton = new RoundedButton("Back to Main Menu");
        
        backButton.addActionListener(e -> backToMainMenu());
        bottomPanel.add(backButton);

        // Add panels to the frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);

        // Load messages into the table
        loadInboxMessages();
    }

    // Method to load inbox messages
    private void loadInboxMessages() {
        try {
            List<String> messages = dentalService.getInboxMessages(email);
            tableModel.setRowCount(0); // Clear existing rows

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            for (String message : messages) {
                String[] parts = message.split("\n");
                String rawDate = parts[0].replace("Date: ", "").trim();

                // Directly format the date string to "dd-MM-yyyy" if already in correct format
                String formattedDate = "";
                if (rawDate != null && !rawDate.trim().isEmpty()) {
                    LocalDate date = LocalDate.parse(rawDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    formattedDate = date.format(outputFormatter);
                }

                String subject = parts[1].replace("Subject: ", "").trim();
                String msg = parts[2].replace("Message: ", "").trim();

                tableModel.addRow(new Object[]{formattedDate, subject, msg});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching messages: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Custom renderer for wrapping text in cells
    private static class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {
        public MultiLineCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "");
            setFont(table.getFont());

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
            if (table.getRowHeight(row) != getPreferredSize().height) {
                table.setRowHeight(row, getPreferredSize().height);
            }

            return this;
        }
    }

    

    // Method to return to the main menu
    private void backToMainMenu() {
        new DentalMainScreen(dentalService, email);
        dispose(); // Close the current inbox screen
    }
}
