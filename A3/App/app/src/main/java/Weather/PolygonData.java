package Weather;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class PolygonData {

    @SerializedName("id")
    private int id;

    @SerializedName("polygon_name")
    private String polygonName;

    @SerializedName("coordinates")
    private String coordinates; // Stored as JSON string

    @SerializedName("flood_type_id")
    private int floodTypeId;

    @SerializedName("color")  // Add color field
    private String color;     // Color associated with this polygon

    // No-argument constructor
    public PolygonData() {
    }

    // Constructor with fields
    public PolygonData(String polygonName, String coordinates, int floodTypeId, String color) {
        this.polygonName = polygonName;
        this.coordinates = coordinates;
        this.floodTypeId = floodTypeId;
        this.color = color;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPolygonName() {
        return polygonName;
    }

    public void setPolygonName(String polygonName) {
        this.polygonName = polygonName;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public int getFloodTypeId() {
        return floodTypeId;
    }

    public void setFloodTypeId(int floodTypeId) {
        this.floodTypeId = floodTypeId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    // Helper method to convert coordinates JSON string to List<LatLng>
    public List<LatLng> getCoordinatesAsLatLngList() {
        List<List<Double>> coordinatesList = new Gson().fromJson(coordinates, List.class);
        List<LatLng> latLngList = new ArrayList<>();

        for (List<Double> point : coordinatesList) {
            if (point.size() == 2) { // Ensure each point contains exactly 2 values: latitude and longitude
                latLngList.add(new LatLng(point.get(0), point.get(1)));
            }
        }
        return latLngList;
    }
}
