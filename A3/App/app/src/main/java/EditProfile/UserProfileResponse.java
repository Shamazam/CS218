package EditProfile;

import com.google.gson.annotations.SerializedName;

public class UserProfileResponse {
    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("username")
    private String username;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("street_address")
    private String streetAddress;

    @SerializedName("location_type")
    private String locationType;

    @SerializedName("location_value_id")
    private String locationValueId;

    @SerializedName("location_name")
    private String locationName;

    @SerializedName("sub_location_name")
    private String subLocationName;

    @SerializedName("profile_image")  // Add the profile image field here
    private String profileImage;

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getLocationType() {
        return locationType;
    }

    public String getLocationValueId() {
        return locationValueId;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getSubLocationName() {
        return subLocationName;
    }

    public String getProfileImage() {  // Getter for profile image
        return profileImage;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "UserProfileResponse{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", streetAddress='" + streetAddress + '\'' +
                ", locationType='" + locationType + '\'' +
                ", locationValueId='" + locationValueId + '\'' +
                ", locationName='" + locationName + '\'' +
                ", subLocationName='" + subLocationName + '\'' +
                ", profileImage='" + profileImage + '\'' +  // Add this to the toString method
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
