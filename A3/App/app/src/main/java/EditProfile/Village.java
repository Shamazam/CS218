package EditProfile;

public class Village {
    private int village_id;
    private String village_name;

    // Constructor, getters, and setters
    public Village(int village_id, String village_name) {
        this.village_id = village_id;
        this.village_name = village_name;
    }

    public int getVillageId() {
        return village_id;
    }

    public String getVillageName() {
        return village_name;
    }

    // Override the toString method to display the village name in the spinner
    @Override
    public String toString() {
        return village_name;  // This will be shown in the spinner
    }
}
