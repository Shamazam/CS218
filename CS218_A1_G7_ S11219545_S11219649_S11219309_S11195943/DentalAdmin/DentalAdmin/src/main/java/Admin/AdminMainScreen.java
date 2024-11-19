/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Admin;

import API.DentalService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class AdminMainScreen extends JFrame {
    
    private DentalService dentalService;
    private String email;
    
    public AdminMainScreen(DentalService dentalService, String email) {
        
        this.dentalService = dentalService;
        this.email = email;

        // Set up the JFrame
        setTitle("Delta Dentistry");
        setSize(400, 400);
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
        
        
        JLabel dashboardLable = new JLabel("Admin Dashbaord");
        dashboardLable.setFont(new Font("Arial", Font.BOLD, 18));
        dashboardLable.setForeground(new Color(0, 200, 0)); // Set text color to green
        topPanel.add(dashboardLable);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Center panel for buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        // Set a fixed button size for consistency
        Dimension buttonSize = new Dimension(250, 40);
        
        // Create the buttons
        RoundedButton createdentistAcc = new RoundedButton("Create Dentist Account");
        styleButton(createdentistAcc, buttonSize);//----
        createdentistAcc.addActionListener(e -> openDoctorAccCreationScreen());
        
        //another function here
        RoundedButton updateDentistAcc = new RoundedButton("Update Dentist Account");
        styleButton(updateDentistAcc, buttonSize);//----
        updateDentistAcc.addActionListener(e -> openDentistUpdateScreen());
        
        RoundedButton deleteDentistAcc = new RoundedButton("Delete Dentist Account");
        styleButton(deleteDentistAcc, buttonSize);//----
        deleteDentistAcc.addActionListener(e -> openDentistDeleteScreen());
        
        
        
        RoundedButton logoutButton = new RoundedButton("Logout");
        styleButton(logoutButton, buttonSize);
        logoutButton.addActionListener(e -> logout());
        
        centerPanel.add(createdentistAcc);
        centerPanel.add(Box.createVerticalStrut(20));
        
        centerPanel.add(updateDentistAcc);
        centerPanel.add(Box.createVerticalStrut(20));
        
        centerPanel.add(deleteDentistAcc);
        centerPanel.add(Box.createVerticalStrut(20));
        
        centerPanel.add(logoutButton);
        centerPanel.add(Box.createVerticalStrut(20));
        
        
        // Add panels to the frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
    
    }
    
    private void openDoctorAccCreationScreen(){
        new DoctorAccCreationScreen(dentalService,email);
        dispose();
    }
    
    private void openDentistUpdateScreen(){
        new openDentistUpdateScreen(dentalService,email);
        dispose();
    }
    
    private void openDentistDeleteScreen(){
        new openDentistDeleteScreen(dentalService,email);
        dispose();
    }
       

    // Method to handle logout
    private void logout() {
        new Adminlogin(dentalService);
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
    
    
    
    
}
