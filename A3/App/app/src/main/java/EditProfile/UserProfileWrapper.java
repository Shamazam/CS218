package EditProfile;

import com.google.gson.annotations.SerializedName;

public class UserProfileWrapper {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private UserProfileResponse data;  // Expect `data` as a JSON object now

    // Getters
    public String getStatus() {
        return status;
    }

    public UserProfileResponse getData() {
        return data;
    }
}

