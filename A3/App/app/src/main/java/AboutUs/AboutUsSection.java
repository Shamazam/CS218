package AboutUs;

// About Us Section Model
import java.util.List;

public class AboutUsSection {
    private int id;
    private String title;
    private List<AboutUsContent> content; // Nested content for each section

    // Constructor
    public AboutUsSection(int id, String title, List<AboutUsContent> content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<AboutUsContent> getContent() {
        return content;
    }

    public void setContent(List<AboutUsContent> content) {
        this.content = content;
    }
}
