package EditProfile;

public class District {
    private String district_name;  // Assuming API sends this as district_name
    private int district_id;  // Assuming the ID is district_id

    // Constructor, getters, and setters
    public District(String district_name, int district_id) {
        this.district_name = district_name;
        this.district_id = district_id;
    }

    public String getDistrictName() {
        return district_name;
    }

    public void setDistrictName(String district_name) {
        this.district_name = district_name;
    }

    public int getDistrictId() {
        return district_id;
    }

    public void setDistrictId(int district_id) {
        this.district_id = district_id;
    }

    @Override
    public String toString() {
        return district_name;  // Return the district name for display in the Spinner
    }
}
