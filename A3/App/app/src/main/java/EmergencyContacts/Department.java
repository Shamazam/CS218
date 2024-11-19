package EmergencyContacts;

import com.google.gson.annotations.SerializedName;

public class Department {

    @SerializedName("id")
    private int departmentId;

    @SerializedName("name")
    private String departmentName;

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public String toString() {
        return departmentName; // Return the department name for display in the Spinner
    }
}
