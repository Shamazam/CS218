package EditProfile;

public class Suburb {
    private int suburb_id;
    private String suburb_name;

    // Constructor, getters, and setters
    public Suburb(int suburb_id, String suburb_name) {
        this.suburb_id = suburb_id;
        this.suburb_name = suburb_name;
    }

    public int getSuburbId() {
        return suburb_id;
    }

    public String getSuburbName() {
        return suburb_name;
    }

    // Override the toString method to display the suburb name in the spinner
    @Override
    public String toString() {
        return suburb_name;  // This will be shown in the spinner
    }
}
