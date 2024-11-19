package AdminEmergencyContacts;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AdminDepartmentResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<AdminDepartment> data;  // Updated to AdminDepartment

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<AdminDepartment> getData() {
        return data;
    }

    public void setData(List<AdminDepartment> data) {
        this.data = data;
    }
}
