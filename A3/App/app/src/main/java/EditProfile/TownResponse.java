package EditProfile;

import java.util.List;

public class TownResponse {
    private String status;
    private List<Location> towns;

    public String getStatus() {
        return status;
    }

    public List<Location> getTowns() {
        return towns;
    }
}

