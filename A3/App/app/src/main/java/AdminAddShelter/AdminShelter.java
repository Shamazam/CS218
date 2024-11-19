package AdminAddShelter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class AdminShelter {

    @SerializedName("id")
    private int id;

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

    @SerializedName("latitude")
    private double latitude;  // Latitude from the API

    @SerializedName("longitude")
    private double longitude; // Longitude from the API

    @SerializedName("capacity")
    private int capacity;

    @SerializedName("current_capacity")
    private int currentCapacity;

    @SerializedName("last_updated")
    private String lastUpdated;

    // Default constructor
    public AdminShelter() {}

    // Getters for all fields
    public int getId() {
        return id;
    }

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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    // Ensure the shelter name is displayed in the Spinner
    @Override
    public String toString() {
        return name;
    }
}
