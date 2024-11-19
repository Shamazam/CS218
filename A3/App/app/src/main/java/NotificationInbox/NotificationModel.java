package NotificationInbox;

import com.google.gson.annotations.SerializedName;

public class NotificationModel {
    private int id;
    private String title;
    private String message;

    @SerializedName("created_at")
    private String createdAt; // Make sure this matches the JSON key if needed

    @SerializedName("is_read")
    private int isRead;

    public NotificationModel(int id, String title, String message, String createdAt, int isRead) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return isRead == 1;
    }

    public void setRead(int isRead) {
        this.isRead = isRead;
    }
}
