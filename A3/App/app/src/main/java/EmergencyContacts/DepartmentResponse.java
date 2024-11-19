package EmergencyContacts;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DepartmentResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<Department> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Department> getData() {
        return data;
    }

    public void setData(List<Department> data) {
        this.data = data;
    }
}
