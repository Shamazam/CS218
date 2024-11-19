package QuickLinks;

import java.util.List;

public class Category {

    // Fields should match the exact JSON keys from the API response
    private String category_name;  // Name of the category
    private List<Link> links;      // List of Link objects within the category

    // Default constructor for serialization/deserialization (e.g., for Gson)
    public Category() {
    }

    // Constructor with parameters
    public Category(String category_name, List<Link> links) {
        this.category_name = category_name;
        this.links = links;
    }

    // Getter for category_name
    public String getCategoryName() {
        return category_name;
    }

    // Setter for category_name
    public void setCategoryName(String category_name) {
        this.category_name = category_name;
    }

    // Getter for links
    public List<Link> getLinks() {
        return links;
    }

    // Setter for links
    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
