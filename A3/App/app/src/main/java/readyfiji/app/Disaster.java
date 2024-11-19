package readyfiji.app;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;


public class Disaster {

    @SerializedName("disaster_id")  // JSON key for disasterId
    private int disasterId;

    @SerializedName("disaster_name")  // JSON key for disasterName
    private String disasterName;

    @SerializedName("tasks")  // JSON key for the list of tasks
    private List<TaskItem> tasks;  // List to store tasks

    private boolean isExpanded;  // Field to track expanded state (no need for serialization)

    // Default no-arg constructor for deserialization
    public Disaster() {
        this.isExpanded = false;  // Default to collapsed
        this.tasks = new ArrayList<>();  // Initialize tasks list to avoid null
    }

    // Constructor with parameters
    public Disaster(int disasterId, String disasterName) {
        this.disasterId = disasterId;
        this.disasterName = disasterName;
        this.isExpanded = false;  // Default to collapsed
        this.tasks = new ArrayList<>();  // Initialize tasks list
    }

    // Getter and setter for disasterId
    public int getDisasterId() {
        return disasterId;
    }

    public void setDisasterId(int disasterId) {
        this.disasterId = disasterId;
    }

    // Getter and setter for disasterName
    public String getDisasterName() {
        return disasterName;
    }

    public void setDisasterName(String disasterName) {
        this.disasterName = disasterName;
    }

    // Getter and setter for tasks
    public List<TaskItem> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskItem> tasks) {
        this.tasks = tasks;
    }

    // Getter and setter for expanded state
    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    @Override
    public String toString() {
        return this.getDisasterName(); // Assuming getDisasterName() returns the name of the disaster
    }

}
