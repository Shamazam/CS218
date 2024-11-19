package AdminQuickLinks;

public class LinkData {
    private int link_id;
    private String link_title;

    // Constructor
    public LinkData(int link_id, String link_title) {
        this.link_id = link_id;
        this.link_title = link_title;
    }

    // Getter and Setter methods
    public int getLinkId() {
        return link_id;
    }

    public void setLinkId(int link_id) {
        this.link_id = link_id;
    }

    public String getLinkTitle() {
        return link_title;
    }

    public void setLinkTitle(String link_title) {
        this.link_title = link_title;
    }

    @Override
    public String toString() {
        return link_title;  // To show link titles in the Spinner
    }
}
