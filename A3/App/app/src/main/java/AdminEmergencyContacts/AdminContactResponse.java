package AdminEmergencyContacts;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import EmergencyContacts.Contact;

public class AdminContactResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<AdminContact> data;

    // Getter for the list of contacts (data)
    public List<AdminContact> getData() {
        return data;
    }

    // Setter if needed
    public void setData(List<AdminContact> data) {
        this.data = data;
    }

    // Optional: getter for status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
