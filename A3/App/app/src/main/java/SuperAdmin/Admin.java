package SuperAdmin;

public class Admin {

    private int admin_id;  // Corresponds to the 'admin_id' in your table
    private String first_name;
    private String last_name;
    private String username;
    private String password;  // Assuming this field is also used if needed for password updates

    // Default constructor
    public Admin() {
    }

    // Constructor to initialize fields
    public Admin(int admin_id, String first_name, String last_name, String username, String password) {
        this.admin_id = admin_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public int getAdminId() {
        return admin_id;
    }

    public void setAdminId(int admin_id) {
        this.admin_id = admin_id;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return username;  // The spinner will display the username
    }
}
