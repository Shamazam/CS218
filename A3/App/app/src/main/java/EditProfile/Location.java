package EditProfile;

import androidx.annotation.NonNull;

public class Location {
    private String name;
    private int id;  // Assuming you have an ID field as well

    // Constructor, getters, and setters
    public Location(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    // Override the toString method to return the name
    @NonNull
    @Override
    public String toString() {
        return name;  // This will make the spinner show the name of the location
    }
}


