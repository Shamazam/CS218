package Server;

import API.DentalService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DentalServiceImpl extends UnicastRemoteObject implements DentalService {

     private static final String DATABASE_LOCATION = "jdbc:ucanaccess://Users.accdb";//declares database location
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");//declares formate of date

    public DentalServiceImpl() throws RemoteException {
        super();
        //try and catch block ensures that the java program can actually connect and communicate with Microsoft Access Database
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RemoteException("Database driver not found.", e);
        }
    }
    @Override
    public boolean registerUser(String email, String firstName, String lastName, String dob, String password) throws RemoteException {
        String insertSQL = "INSERT INTO [User Credentials] (Username, FName, Lname, DOB, UserPassword) VALUES (?, ?, ?, ?, ?)";
        //takes in information that the user has provided and inserts it into the user credidential table
        try (Connection conn = DriverManager.getConnection(DATABASE_LOCATION);
            //add data into the database table    
            PreparedStatement insertionStatement = conn.prepareStatement(insertSQL)) {

            insertionStatement.setString(1, email);  // Username is the email
            insertionStatement.setString(2, firstName);
            insertionStatement.setString(3, lastName);

            // Check and correct date format
            if (!dob.matches("\\d{2}-\\d{2}-\\d{4}")) {
                throw new RemoteException("Invalid date format. Please use dd-MM-yyyy.");
            }

            // Correct date parsing without direct casting
            java.util.Date parsedDate = DATE_FORMAT.parse(dob);
            java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());
            insertionStatement.setDate(4, sqlDate);

            insertionStatement.setString(5, password);

            int rowsInserted = insertionStatement.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error registering user.", e);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RemoteException("Invalid date format. Please use dd-MM-yyyy.", e);
        }
    }
    @Override
    public boolean updateDentist(String email, String currentPassword, String newPassword) throws RemoteException{
        
        String querySQL = "SELECT * FROM [Doctor Credentials] WHERE username = ? AND dentist_password = ?";//checks username and password
        String updateSQL = "UPDATE [Doctor Credentials] SET dentist_password = ? WHERE username = ?";//set new password
        
         try (Connection conn = DriverManager.getConnection(DATABASE_LOCATION);
             PreparedStatement queryStatement = conn.prepareStatement(querySQL);
             PreparedStatement updateStatement = conn.prepareStatement(updateSQL)) {

            // Check if current password matches
            queryStatement.setString(1, email);
            queryStatement.setString(2, currentPassword);
            ResultSet resultSet = queryStatement.executeQuery();

            if (resultSet.next()) {
                // If it matches, update the password
                updateStatement.setString(1, newPassword);
                updateStatement.setString(2, email);
                int rowsUpdated = updateStatement.executeUpdate();
                return rowsUpdated > 0;
            } else {
                return false; // Password does not match
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error changing password.", e);
        }
        
        
        
    }
    
    @Override
    public boolean deleteDentist(String email, String password) throws RemoteException{
        
        String querySQL = "SELECT * FROM [Doctor Credentials] WHERE username = ? AND dentist_password = ?";//checks username and password
        String deleteSQL = "DELETE FROM [Doctor Credentials] WHERE Username = ?";
        
        try (Connection conn = DriverManager.getConnection(DATABASE_LOCATION);
         PreparedStatement queryStatement = conn.prepareStatement(querySQL);
         PreparedStatement deleteStatement = conn.prepareStatement(deleteSQL)) {

        // Check if the username and password match
        queryStatement.setString(1, email);
        queryStatement.setString(2, password);
        ResultSet resultSet = queryStatement.executeQuery();

        if (resultSet.next()) {
            // If match is found, delete the user record
            deleteStatement.setString(1, email);
            int rowsDeleted = deleteStatement.executeUpdate();
            return rowsDeleted > 0; // Return true if the deletion was successful
        } else {
            return false; // No matching record found, so deletion does not occur
        }

    } catch (SQLException e) {
        e.printStackTrace();
        throw new RemoteException("Error deleting user record.", e);
    }
        
        
    }
    
    @Override
    public boolean registerDentist(String email, String firstName, String lastName, String password) throws RemoteException {
        String insertSQL = "INSERT INTO [Doctor Credentials] (username, dentist_password, f_name, l_name, role) VALUES (?, ?, ?, ?, ?)";
        //takes in information that the user has provided and inserts it into the user credidential table
        try (Connection conn = DriverManager.getConnection(DATABASE_LOCATION);
            //add data into the database table    
            PreparedStatement insertionStatement = conn.prepareStatement(insertSQL)) {

            insertionStatement.setString(1, email);  // Username is the email 
            insertionStatement.setString(2, password);
            insertionStatement.setString(3, firstName);
            insertionStatement.setString(4, lastName);
            insertionStatement.setString(5, "Dentist");

            

            int rowsInserted = insertionStatement.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error registering user.", e);
        } 
    }



    @Override
    public boolean loginUser(String email, String password) throws RemoteException {
        String userQuerySQL = "SELECT * FROM [User Credentials] WHERE Username = ? AND UserPassword = ?"; //sql statement to check for a username and its acompanying password
        String dentistQuerySQL = "SELECT * FROM [Doctor Credentials] WHERE username = ? AND dentist_password = ?"; // Corrected column name
        String adminQuerySQL = "SELECT * FROM [Admin Credentials] WHERE Username = ? AND Password = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_LOCATION);
             PreparedStatement userStatement = conn.prepareStatement(userQuerySQL); //prepares to excute the sql command
             PreparedStatement dentistStatement = conn.prepareStatement(dentistQuerySQL);
             PreparedStatement adminStatement = conn.prepareStatement(adminQuerySQL)) {

            // Checking in User Credentials
            userStatement.setString(1, email);
            userStatement.setString(2, password);
            ResultSet userResultSet = userStatement.executeQuery();//excutes sql command
            //first checks if user is signing first
            if (userResultSet.next()) {
                return true; // User found in User Credentials
            }
            
            // Checking in dentist Credentials (Admins) if login details not of users
            dentistStatement.setString(1, email);
            dentistStatement.setString(2, password);
            ResultSet dentistResultSet = dentistStatement.executeQuery();
            if(dentistResultSet.next()){
                return true; // Return true if dentist is found
            }
            
            adminStatement.setString(1, email);
            adminStatement.setString(2, password);
            ResultSet adminResultSet = adminStatement.executeQuery();
            
             return adminResultSet.next();// Return true if admin is found
                 
            
            

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error logging in user.", e);
        }
    }
    
    @Override
    public boolean isAdminUser(String username) throws RemoteException{
        String querySQL = "SELECT * FROM [Admin Credentials] WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DATABASE_LOCATION);
             PreparedStatement searchStatement = conn.prepareStatement(querySQL)) {

            searchStatement.setString(1, username);
            ResultSet resultSet = searchStatement.executeQuery();

            return resultSet.next(); // Returns true if a match is found

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error checking admin user credentials.", e);
        }
    }
    

    @Override
    public boolean isDentistUser(String username) throws RemoteException {
        String querySQL = "SELECT * FROM [Doctor Credentials] WHERE username = ?";//checks if user is a admin
        try (Connection conn = DriverManager.getConnection(DATABASE_LOCATION);
             PreparedStatement searchStatement = conn.prepareStatement(querySQL)) {

            searchStatement.setString(1, username);
            ResultSet resultSet = searchStatement.executeQuery();

            return resultSet.next(); // Returns true if a match is found

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error checking admin user credentials.", e);
        }
    }

    @Override
    public boolean bookAppointment(String email, String appointmentDate, String timeSlot, String reason) throws RemoteException {
        String checkSQL = "SELECT COUNT(*) FROM Appointments WHERE UserEmail = ? AND appointment_date = ?";//check if user has a appointment on that day(one user can only have one appointment per day)
        String insertSQL = "INSERT INTO Appointments (UserEmail, appointment_date, time_slot, reason, status, date_created) VALUES (?, ?, ?, ?, 'Pending', ?)";//inserts data into database table

        try (Connection conn = DriverManager.getConnection(DATABASE_LOCATION);
             PreparedStatement checkStatement = conn.prepareStatement(checkSQL);
             PreparedStatement insertStatement = conn.prepareStatement(insertSQL)) {

            // Check if there is already an appointment for the user on the specified date
            java.sql.Date sqlAppointmentDate = java.sql.Date.valueOf(appointmentDate);
            checkStatement.setString(1, email);
            checkStatement.setDate(2, sqlAppointmentDate);
            ResultSet checkResult = checkStatement.executeQuery();
            checkResult.next();
            int count = checkResult.getInt(1);

            if (count > 0) {
                // User already has an appointment on this date
                return false;
            }

            // Check if the appointment date is at least 7 days in the future
            LocalDate currentDate = LocalDate.now();
            LocalDate appointmentLocalDate = sqlAppointmentDate.toLocalDate();
            if (appointmentLocalDate.isBefore(currentDate.plusDays(7))) {
                throw new RemoteException("Appointments can only be booked at least 7 days in advance.");
            }

            // Insert the appointment if no conflicts exist
            insertStatement.setString(1, email);
            insertStatement.setDate(2, sqlAppointmentDate);
            insertStatement.setString(3, timeSlot);
            insertStatement.setString(4, reason);

            // Set the current date as the date_created
            LocalDate currentDateLocal = LocalDate.now();
            java.sql.Date sqlCurrentDate = java.sql.Date.valueOf(currentDateLocal);
            insertStatement.setDate(5, sqlCurrentDate);  // Adds the current date as 'date_created'

            int rowsInserted = insertStatement.executeUpdate();
            return rowsInserted > 0;//return true if 1 or more rows saved

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error booking appointment.", e);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new RemoteException("Invalid date format.", e);
        }
    }

    @Override
    public List<String> getAvailableTimeSlots(String appointmentDate) throws RemoteException {
        List<String> allTimeSlots = Arrays.asList("09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00");//adds appointment times
        List<String> availableTimeSlots = new ArrayList<>(allTimeSlots);//copies all appointment times to a new arraylist

        String querySQL = "SELECT time_slot FROM Appointments WHERE appointment_date = ?";//checks all time slots taken on a particular day and removes it from arraylist

        try (Connection conn = DriverManager.getConnection(DATABASE_LOCATION);
             PreparedStatement checkStatement = conn.prepareStatement(querySQL)) {

            // Parse date using 'dd-MM-yyyy' since this is the storage format
            java.util.Date parsedDate = DATE_FORMAT.parse(appointmentDate);
            java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());
            checkStatement.setDate(1, sqlDate);

            ResultSet resultSet = checkStatement.executeQuery();
            while (resultSet.next()) {
                availableTimeSlots.remove(resultSet.getString("time_slot"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error fetching available time slots.", e);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RemoteException("Invalid date format provided.", e);
        }

        return availableTimeSlots;
    }
    

    @Override
    public boolean changePassword(String email, String currentPassword, String newPassword) throws RemoteException {
        String querySQL = "SELECT * FROM [User Credentials] WHERE Username = ? AND UserPassword = ?";//checks username and password
        String updateSQL = "UPDATE [User Credentials] SET UserPassword = ? WHERE Username = ?";//set new password

        try (Connection conn = DriverManager.getConnection(DATABASE_LOCATION);
             PreparedStatement queryStatement = conn.prepareStatement(querySQL);
             PreparedStatement updateStatement = conn.prepareStatement(updateSQL)) {

            // Check if current password matches
            queryStatement.setString(1, email);
            queryStatement.setString(2, currentPassword);
            ResultSet resultSet = queryStatement.executeQuery();

            if (resultSet.next()) {
                // If it matches, update the password
                updateStatement.setString(1, newPassword);
                updateStatement.setString(2, email);
                int rowsUpdated = updateStatement.executeUpdate();
                return rowsUpdated > 0;
            } else {
                return false; // Password does not match
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error changing password.", e);
        }
    }

    @Override
    public List<String> getAppointmentHistory(String email, String date) throws RemoteException {
        List<String> appointmentHistory = new ArrayList<>();
        String querySQL = "SELECT appointment_date, time_slot, reason, status FROM Appointments WHERE UserEmail = ? AND appointment_date = ? ORDER BY appointment_date DESC";//returns all approved apointments a user has had

        try (Connection conn = DriverManager.getConnection(DATABASE_LOCATION);
             PreparedStatement SearchStatement = conn.prepareStatement(querySQL)) {

            SearchStatement.setString(1, email);
            // Adjust to match the format stored in your database
            java.util.Date parsedDate = DATE_FORMAT.parse(date); 
            java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());
            SearchStatement.setDate(2, sqlDate);

            ResultSet resultSet = SearchStatement.executeQuery();

            while (resultSet.next()) {
                String appointmentDate = resultSet.getString("appointment_date");
                String timeSlot = resultSet.getString("time_slot");
                String reason = resultSet.getString("reason");
                String status = resultSet.getString("status");

                if (appointmentDate != null && timeSlot != null && reason != null && status != null) {
                    String appointmentDetails = String.format("Date: %s, Time: %s, Reason: %s, Status: %s", appointmentDate, timeSlot, reason, status);//saves data into a string in a particular format
                    appointmentHistory.add(appointmentDetails);//append appointmentDetails into a list
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error fetching appointment history.", e);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RemoteException("Error parsing date format.", e);
        }

        return appointmentHistory;
    }

    @Override
    public List<String> getAvailableAppointmentDates(String email) throws RemoteException {
        List<String> availableDates = new ArrayList<>();
        String querySQL = "SELECT DISTINCT appointment_date FROM Appointments WHERE UserEmail = ? ORDER BY appointment_date DESC";//check for available dates 

        try (Connection conn = DriverManager.getConnection(DATABASE_LOCATION);
             PreparedStatement stmt = conn.prepareStatement(querySQL)) {

            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                Date appointmentDate = resultSet.getDate("appointment_date"); // Assuming SQL date is correct
                // Convert SQL Date to String in 'dd-MM-yyyy' format
                String formattedDate = DATE_FORMAT.format(appointmentDate);
                availableDates.add(formattedDate);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error fetching available dates from database: " + e.getMessage(), e);
        }

        return availableDates;
    }
    
    @Override
public List<String> getInboxMessages(String email) throws RemoteException {
    List<String> messages = new ArrayList<>();
    String querySQL = "SELECT appointment_id, appointment_date, time_slot, reason, status, date_created, date_approved, admin_reason FROM Appointments WHERE UserEmail = ? ORDER BY date_created DESC";//returns all request that has been either accepted, rejected or pending

    try (Connection conn = DriverManager.getConnection(DATABASE_LOCATION);
         PreparedStatement stmt = conn.prepareStatement(querySQL)) {

        stmt.setString(1, email);
        ResultSet resultSet = stmt.executeQuery();

        while (resultSet.next()) {
            int appointmentId = resultSet.getInt("appointment_id");
            Date appointmentDate = resultSet.getDate("appointment_date");
            String timeSlot = resultSet.getString("time_slot");
            String reason = resultSet.getString("reason");
            String status = resultSet.getString("status");
            Date dateCreated = resultSet.getDate("date_created");
            Date dateApproved = resultSet.getDate("date_approved");
            String adminReason = resultSet.getString("admin_reason");

            // Format the date to 'dd-MM-yyyy'
            String formattedAppointmentDate = new SimpleDateFormat("dd-MM-yyyy").format(appointmentDate);
            String formattedDateCreated = new SimpleDateFormat("dd-MM-yyyy").format(dateCreated);
            String formattedDateApproved = dateApproved != null ? new SimpleDateFormat("dd-MM-yyyy").format(dateApproved) : "";

            String subject;
            String message;
            String date;

            // Construct messages based on the appointment status
            if (status.equalsIgnoreCase("pending")) {
                subject = "Awaiting Approval";
                message = String.format("Appointment %d is waiting for approval.", appointmentId);
                date = formattedDateCreated;
            } else if (status.equalsIgnoreCase("approved")) {
                subject = "Appointment Approved";
                message = String.format("Your appointment %d has been approved for %s at %s.", appointmentId, formattedAppointmentDate, timeSlot);
                date = formattedDateApproved;
            } else if (status.equalsIgnoreCase("rejected")) {
                subject = "Appointment Rejected";
                message = String.format("Your appointment %d was rejected due to the following reason: %s", appointmentId, adminReason);
                date = formattedDateApproved;
            } else {
                subject = "Unknown Status";
                message = "There was an issue retrieving the status of your appointment.";
                date = formattedDateCreated;
            }

            // Constructing the full message for the inbox
            String fullMessage = String.format("Date: %s\nSubject: %s\nMessage: %s", date, subject, message);
            messages.add(fullMessage);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        throw new RemoteException("Error fetching messages.", e);
    }

    return messages;
}


@Override
public boolean approveAppointment(int appointmentId) throws RemoteException {
    String checkSQL = "SELECT COUNT(*) FROM Appointments WHERE appointment_date = (SELECT appointment_date FROM Appointments WHERE appointment_id = ?) AND time_slot = (SELECT time_slot FROM Appointments WHERE appointment_id = ?) AND status = 'Approved'";//check previously accepted request
    String updateSQL = "UPDATE Appointments SET status = 'Approved', date_approved = ? WHERE appointment_id = ?";//approves request

    try (Connection conn = DriverManager.getConnection(DATABASE_LOCATION);
         PreparedStatement checkStatement = conn.prepareStatement(checkSQL);
         PreparedStatement updateStatement = conn.prepareStatement(updateSQL)) {

        // Set parameters for checking existing approved appointment
        checkStatement.setInt(1, appointmentId);
        checkStatement.setInt(2, appointmentId);

        ResultSet rs = checkStatement.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            // If an approved appointment already exists for the same date and time
            return false;
        }

        // Set the approval date and appointment ID for update
        updateStatement.setDate(1, new java.sql.Date(System.currentTimeMillis()));
        updateStatement.setInt(2, appointmentId);

        int rowsUpdated = updateStatement.executeUpdate();
        return rowsUpdated > 0; // Returns true if the update was successful

    } catch (SQLException e) {
        e.printStackTrace();
        throw new RemoteException("Error approving appointment.", e);
    }
}





@Override
public boolean rejectAppointment(int appointmentId, String reason) throws RemoteException {
    String updateSQL = "UPDATE Appointments SET status = 'Rejected', date_approved = ?, admin_reason = ? WHERE appointment_id = ?";//updates request and rejects it

    try (Connection conn = DriverManager.getConnection(DATABASE_LOCATION);
         PreparedStatement stmt = conn.prepareStatement(updateSQL)) {

        // Set the date_appro to the current date
        stmt.setDate(1, new java.sql.Date(System.currentTimeMillis()));
        stmt.setString(2, reason);
        stmt.setInt(3, appointmentId);

        int rowsUpdated = stmt.executeUpdate();
        return rowsUpdated > 0; // Returns true if the update was successful

    } catch (SQLException e) {
        e.printStackTrace();
        throw new RemoteException("Error rejecting appointment.", e);
    }
}




    @Override
    public List<String> getPendingAppointments() throws RemoteException {
        List<String> pendingAppointments = new ArrayList<>();
        String querySQL = "SELECT appointment_id, UserEmail, appointment_date, time_slot, reason FROM Appointments WHERE status = 'Pending' ORDER BY appointment_date DESC";//retrieves all requests that are currently alive

        try (Connection conn = DriverManager.getConnection(DATABASE_LOCATION);
             PreparedStatement stmt = conn.prepareStatement(querySQL)) {

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("appointment_id");
                String userEmail = resultSet.getString("UserEmail");
                Date appointmentDate = resultSet.getDate("appointment_date");
                String timeSlot = resultSet.getString("time_slot");
                String reason = resultSet.getString("reason");

                // Format date to 'dd-MM-yyyy'
                String formattedDate = DATE_FORMAT.format(appointmentDate);

                // Construct the appointment string in the required format
                String appointmentDetails = String.format("%d,%s,%s,%s,%s",
                        appointmentId, userEmail, formattedDate, timeSlot, reason);
                pendingAppointments.add(appointmentDetails);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Error fetching pending appointments.", e);
        }

        return pendingAppointments;
    }

@Override
public List<String> getApprovedRejectedAppointments() throws RemoteException {
    List<String> appointments = new ArrayList<>();
    String querySQL = "SELECT appointment_id, UserEmail, appointment_date, time_slot, status, admin_reason FROM Appointments WHERE status IN ('Approved', 'Rejected')";//returns list of requests either rejected or accepted

    try (Connection conn = DriverManager.getConnection(DATABASE_LOCATION);
         PreparedStatement stmt = conn.prepareStatement(querySQL);
         ResultSet resultSet = stmt.executeQuery()) {

        while (resultSet.next()) {
            int appointmentId = resultSet.getInt("appointment_id");
            String userEmail = resultSet.getString("UserEmail");
            Date appointmentDate = resultSet.getDate("appointment_date");
            String timeSlot = resultSet.getString("time_slot");
            String status = resultSet.getString("status");
            String adminReason = resultSet.getString("admin_reason");

            // Format the date in 'dd-MM-yyyy'
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = appointmentDate != null ? dateFormat.format(appointmentDate) : "";

            // Construct the formatted appointment string without a trailing comma
            StringBuilder appointmentDetails = new StringBuilder();
            appointmentDetails.append(appointmentId).append(",");
            appointmentDetails.append(userEmail).append(",");
            appointmentDetails.append(formattedDate).append(",");
            appointmentDetails.append(timeSlot).append(",");
            appointmentDetails.append(status);

            // Add admin reason only if it is not null or empty
            if (adminReason != null && !adminReason.trim().isEmpty()) {
                appointmentDetails.append(",").append(adminReason);
            }

            appointments.add(appointmentDetails.toString());
        }

    } catch (SQLException e) {
        e.printStackTrace();
        throw new RemoteException("Error fetching approved/rejected appointments.", e);
    }

    return appointments;
}


}
