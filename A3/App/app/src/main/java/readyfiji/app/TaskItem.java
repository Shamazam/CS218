package readyfiji.app;

import com.google.gson.annotations.SerializedName;

public class TaskItem {

    @SerializedName("task_id")
    private int taskId;

    @SerializedName("task_name")
    private String taskName;

    @SerializedName("completed")
    private int completed; // 0 = not completed, 1 = completed

    @SerializedName("task_type")
    private String taskType; // 'user' or 'admin'

    @SerializedName("user_id")
    private int userId;  // Track the user who created the task

    // Default constructor for Gson or other serialization frameworks (optional)
    public TaskItem() {}

    // Constructor with parameters
    public TaskItem(int taskId, String taskName, int completed, String taskType, int userId) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.completed = completed;
        this.taskType = taskType;
        this.userId = userId;
    }

    // Getters and setters
    public int getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    // Returns true if the task is completed (boolean logic)
    public boolean isCompleted() {
        return completed == 1;
    }

    // Sets the completed status using a boolean
    public void setCompleted(boolean completed) {
        this.completed = completed ? 1 : 0;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return this.getTaskName(); // Ensure that this returns the task name
    }
}
