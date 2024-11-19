package API;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DentalService extends Remote {
    
    boolean registerUser(String email, String firstName, String lastName, String dob, String password) throws RemoteException;//stores new user

    boolean loginUser(String email, String password) throws RemoteException;//check user login detail

    boolean changePassword(String email, String currentPassword, String newPassword) throws RemoteException;//change user password
    
    boolean bookAppointment(String email, String date, String timeSlot, String reason) throws RemoteException;//saves user appointment request
    
    List<String> getAvailableTimeSlots(String appointmentDate) throws RemoteException;//retrieves available time slot for user
    
    List<String> getAvailableAppointmentDates(String email) throws RemoteException; // Method to fetch available appointment dates
    
    List<String> getAppointmentHistory(String email, String date) throws RemoteException; // Method to fetch history for a specific date
    
    List<String> getInboxMessages(String email) throws RemoteException;//shows information about a request
    
    boolean isDentistUser(String username) throws RemoteException;//check if user is dentist
    
    public boolean isAdminUser(String username) throws RemoteException;
    
    public boolean registerDentist(String email, String firstName, String lastName, String password) throws RemoteException;
    
    public boolean updateDentist(String email, String currentPassword, String newPassword) throws RemoteException;
            
    public boolean deleteDentist(String email, String password) throws RemoteException;
    
    public List<String> getPendingAppointments() throws RemoteException;//get pending appointment request
    
    public boolean approveAppointment(int appointmentId) throws RemoteException; // Updated method to approve appointments

    public boolean rejectAppointment(int appointmentId, String reason) throws RemoteException; // Method to reject appointments
    
    List<String> getApprovedRejectedAppointments() throws RemoteException;// returns list of all request either accepted or rejected to admin
    
}
