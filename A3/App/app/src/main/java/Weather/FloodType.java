package Weather;

public class FloodType {

    private int id;            // Flood type ID (from the database)
    private String flood_type;  // Flood type name (e.g., "River Flooding", "Flash Flooding")
    private String color;       // Hex color code (e.g., "#FF0000")

    // Constructor
    public FloodType(int id, String flood_type, String color) {
        this.id = id;
        this.flood_type = flood_type;
        this.color = color;
    }

    // Getter for ID
    public int getId() {
        return id;
    }

    // Setter for ID
    public void setId(int id) {
        this.id = id;
    }

    // Getter for flood type
    public String getFloodType() {
        return flood_type;
    }

    // Setter for flood type
    public void setFloodType(String flood_type) {
        this.flood_type = flood_type;
    }

    // Getter for color
    public String getColor() {
        return color;
    }

    // Setter for color
    public void setColor(String color) {
        this.color = color;
    }

    // Override toString to show the flood type name in the Spinner
    @Override
    public String toString() {
        return flood_type;  // This will be displayed in the Spinner dropdown
    }
}
