package Weather;

public class ApiResponse {

    private String status;   // Status of the API response (e.g., "success" or "error")
    private String message;  // Message from the server (e.g., "Polygon saved successfully")

    // Constructor
    public ApiResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getter for status
    public String getStatus() {
        return status;
    }

    // Setter for status
    public void setStatus(String status) {
        this.status = status;
    }

    // Getter for message
    public String getMessage() {
        return message;
    }

    // Setter for message
    public void setMessage(String message) {
        this.message = message;
    }
}
