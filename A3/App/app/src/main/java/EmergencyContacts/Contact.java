package EmergencyContacts;

import com.google.gson.annotations.SerializedName;

public class Contact {

    @SerializedName("id")
    private int id;

    @SerializedName("building_name")
    private String buildingName;

    @SerializedName("landline1")
    private String landline1;

    @SerializedName("landline2")
    private String landline2;

    @SerializedName("mobile1")
    private String mobile1;

    @SerializedName("mobile2")
    private String mobile2;

    @SerializedName("mobile3")
    private String mobile3;

    @SerializedName("mobile4")
    private String mobile4;

    @SerializedName("street_address")
    private String streetAddress;

    @SerializedName("town")
    private String town;

    @SerializedName("city")
    private String city;

    @SerializedName("region")
    private String region;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("department_id")
    private int departmentId;  // This is the missing field

    // Getters and setters for the fields

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String building_name) {
        this.buildingName = building_name;
    }

    public String getLandline1() {
        return landline1;
    }

    public void setLandline1(String landline1) {
        this.landline1 = landline1;
    }

    public String getLandline2() {
        return landline2;
    }

    public void setLandline2(String landline2) {
        this.landline2 = landline2;
    }

    public String getMobile1() {
        return mobile1;
    }

    public void setMobile1(String mobile1) {
        this.mobile1 = mobile1;
    }

    public String getMobile2() {
        return mobile2;
    }

    public void setMobile2(String mobile2) {
        this.mobile2 = mobile2;
    }

    public String getMobile3() {
        return mobile3;
    }

    public void setMobile3(String mobile3) {
        this.mobile3 = mobile3;
    }

    public String getMobile4() {
        return mobile4;
    }

    public void setMobile4(String mobile4) {
        this.mobile4 = mobile4;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String street_address) {
        this.streetAddress = street_address;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }
}
