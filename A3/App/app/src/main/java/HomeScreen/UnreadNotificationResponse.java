package HomeScreen;

public class UnreadNotificationResponse {
    private String status;
    private int unread_count;

    public String getStatus() {
        return status;
    }

    public int getUnreadCount() {
        return unread_count;  // Ensure this matches the API response key
    }
}


