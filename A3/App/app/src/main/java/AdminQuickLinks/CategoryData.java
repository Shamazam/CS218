package AdminQuickLinks;

public class CategoryData {
    private int category_id;
    private String category_name;

    // Constructor
    public CategoryData(int category_id, String category_name) {
        this.category_id = category_id;
        this.category_name = category_name;
    }

    // Getter and Setter methods
    public int getCategoryId() {
        return category_id;
    }

    public void setCategoryId(int category_id) {
        this.category_id = category_id;
    }

    public String getCategoryName() {
        return category_name;
    }

    public void setCategoryName(String category_name) {
        this.category_name = category_name;
    }

    @Override
    public String toString() {
        return category_name;  // To show category names in the Spinner
    }
}
