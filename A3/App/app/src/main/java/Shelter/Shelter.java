package Shelter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class Shelter {

    @SerializedName("shelter_name")
    private String name;

    @SerializedName("person_in_charge")
    private String personInCharge;

    @SerializedName("primary_contact")
    private String contactNumber;

    @SerializedName("secondary_contact")
    private String secondaryContact;

    @SerializedName("street_address")
    private String streetAddress;

    @SerializedName("town")
    private String town;

    @SerializedName("city")
    private String city;

    @SerializedName("region")
    private String region;

    @SerializedName("capacity")
    private int capacity;

    @SerializedName("current_capacity")
    private int currentOccupancy;

    @SerializedName("is_full")
    private boolean isFull;

    @SerializedName("latitude")
    private double latitude;  // Store latitude separately for JSON deserialization

    @SerializedName("longitude")
    private double longitude; // Store longitude separately for JSON deserialization

    private boolean isExpanded;

    // Constructor
    public Shelter(String name, String personInCharge, String contactNumber, String secondaryContact,
                   String streetAddress, String town, String city, String region,
                   int capacity, int currentOccupancy, boolean isFull, LatLng location) {
        this.name = name;
        this.personInCharge = personInCharge;
        this.contactNumber = contactNumber;
        this.secondaryContact = secondaryContact;
        this.streetAddress = streetAddress;
        this.town = town;
        this.city = city;
        this.region = region;
        this.capacity = capacity;
        this.currentOccupancy = currentOccupancy;
        this.isFull = isFull;
        this.latitude = location.latitude;
        this.longitude = location.longitude;
        this.isExpanded = false;  // Default to not expanded
    }

    // Getter and setter for location
    public LatLng getLocation() {
        return new LatLng(latitude, longitude);
    }

    public void setLocation(LatLng location) {
        this.latitude = location.latitude;
        this.longitude = location.longitude;
    }

    // Getters and setters for all other fields
    public String getName() {
        return name;
    }

    public String getPersonInCharge() {
        return personInCharge;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getSecondaryContact() {
        return secondaryContact;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getTown() {
        return town;
    }

    public String getCity() {
        return city;
    }

    public String getRegion() {
        return region;
    }

    public String getFullAddress() {
        return streetAddress + ", " + town + ", " + city + ", " + region;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public boolean isFull() {
        return isFull;
    }

    // Expanded state methods
    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
