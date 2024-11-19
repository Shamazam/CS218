package EmergencyContacts;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ContactResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<Contact> data;

    // Getter for the list of contacts (data)
    public List<Contact> getData() {
        return data;
    }

    // Setter if needed
    public void setData(List<Contact> data) {
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
