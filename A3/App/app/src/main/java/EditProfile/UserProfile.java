package EditProfile;

public class UserProfile {
    private String user_id;
    private String first_name;
    private String last_name;
    private String username;
    private String phone_number;
    private String street_address;
    private String location_type;
    private String location_value_id;
    private String profile_image;

    // Getters
    public String getUserId() {
        return user_id;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public String getStreetAddress() {
        return street_address;
    }

    public String getLocationType() {
        return location_type;
    }

    public String getLocationValueId() {
        return location_value_id;
    }

    // Setters
    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setStreetAddress(String street_address) {
        this.street_address = street_address;
    }

    public void setLocationType(String location_type) {
        this.location_type = location_type;
    }

    public void setLocationValueId(String location_value_id) {
        this.location_value_id = location_value_id;
    }

    // toString method
    @Override
    public String toString() {
        return "UserProfile{" +
                "user_id='" + user_id + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", username='" + username + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", street_address='" + street_address + '\'' +
                ", location_type='" + location_type + '\'' +
                ", location_value_id='" + location_value_id + '\'' +
                '}';
    }

    public String getProfileImage() {
        return profile_image;
    }

    public void setProfileImage(String profile_image) {
        this.profile_image = profile_image;
    }
}
