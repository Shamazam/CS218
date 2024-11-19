package AboutUs;

public class AboutUsContent {
    private int id;
    private int sectionId;
    private String content;
    private int order;

    // Constructor
    public AboutUsContent(int id, int sectionId, String content, int order) {
        this.id = id;
        this.sectionId = sectionId;
        this.content = content;
        this.order = order;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}