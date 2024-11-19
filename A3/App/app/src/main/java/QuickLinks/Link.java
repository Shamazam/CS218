package QuickLinks;

public class Link {

    private String title;  // The title of the link
    private String url;    // The actual URL for the link

    // Default constructor
    public Link() {
    }

    // Constructor with parameters
    public Link(String title, String url) {
        this.title = title;
        this.url = url;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Setter for title
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for URL
    public String getUrl() {
        return url;
    }

    // Setter for URL
    public void setUrl(String url) {
        this.url = url;
    }
}
