package AboutUs;

public class ContactUs {
    private String email;
    private String phone;

    // Constructor
    public ContactUs(String email, String phone) {
        this.email = email;
        this.phone = phone;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
