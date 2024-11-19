package NotificationInbox;

import java.util.List;

public class NotificationResponse {
    private String status;
    private List<NotificationModel> data; // Data should be treated as a List

    public String getStatus() {
        return status;
    }

    public List<NotificationModel> getData() {
        return data;
    }
}
