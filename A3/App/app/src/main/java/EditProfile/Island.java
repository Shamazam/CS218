package EditProfile;

public class Island {
    private String island_name;
    private int island_id;

    public Island(String island_name, int island_id) {
        this.island_name = island_name;
        this.island_id = island_id;
    }

    public String getIslandName() {
        return island_name;
    }

    public void setIslandName(String island_name) {
        this.island_name = island_name;
    }

    public int getIslandId() {
        return island_id;
    }

    public void setIslandId(int island_id) {
        this.island_id = island_id;
    }

    @Override
    public String toString() {
        return island_name;  // For Spinner display
    }
}
