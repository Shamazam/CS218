package HomeScreen;

import com.google.gson.annotations.SerializedName;

public class NewsItem {

    @SerializedName("title")  // Maps the JSON key "title" to this variable
    private String title;

    @SerializedName("description")  // Maps the JSON key "description" to this variable
    private String description;

    @SerializedName("image_url")  // Maps the JSON key "image_url" to this variable
    private String image_url;

    // Constructor that accepts title, description, and imageUrl
    public NewsItem(String title, String description, String image_url) {
        this.title = title;
        this.description = description;
        this.image_url = image_url;
    }

    // Getter methods for the variables
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return image_url;
    }
}

