package AdminQuickLinks;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminQuickLinksActivity extends AppCompatActivity {

    // Declare UI components
    private EditText addCategoryInput, editCategoryInput, linkTitleInput, linkUrlInput;
    private Spinner categorySpinner, addLinkCategorySpinner, deleteLinkCategorySpinner, deleteLinkSpinner;
    private Button submitAddCategory, submitEditCategory, deleteCategoryButton, submitAddLink, deleteLinkButton;
    private TaskApi apiService;

    private List<CategoryData> categoriesList = new ArrayList<>();
    private List<LinkData> linksList = new ArrayList<>();

    private int selectedCategoryId = -1;
    private int selectedLinkId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_admin_quick_links);

        // Initialize custom back arrow
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> onBackPressed()); // Handle back arrow click

        // Initialize UI elements
        addCategoryInput = findViewById(R.id.categoryField);
        editCategoryInput = findViewById(R.id.editCategoryField);
        linkTitleInput = findViewById(R.id.linkTitleField);
        linkUrlInput = findViewById(R.id.linkUrlField);

        categorySpinner = findViewById(R.id.categorySpinner);
        addLinkCategorySpinner = findViewById(R.id.linkCategorySpinner);
        deleteLinkCategorySpinner = findViewById(R.id.deleteLinkCategorySpinner);
        deleteLinkSpinner = findViewById(R.id.linkSpinner);

        submitAddCategory = findViewById(R.id.submitCategoryBtn);
        submitEditCategory = findViewById(R.id.submitEditCategoryBtn);
        deleteCategoryButton = findViewById(R.id.deleteCategoryBtn);
        submitAddLink = findViewById(R.id.submitLinkBtn);
        deleteLinkButton = findViewById(R.id.deleteLinkBtn);

        // Initialize API service
        apiService = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        // Load categories to populate spinners
        loadCategories();

        // Handle adding a new category
        submitAddCategory.setOnClickListener(v -> {
            String categoryName = addCategoryInput.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                addCategory(categoryName);
            } else {
                Toast.makeText(AdminQuickLinksActivity.this, "Please enter a category name", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle editing a category
        submitEditCategory.setOnClickListener(v -> {
            String newCategoryName = editCategoryInput.getText().toString().trim();
            if (selectedCategoryId != -1 && !newCategoryName.isEmpty()) {
                editCategory(selectedCategoryId, newCategoryName);
            } else {
                Toast.makeText(AdminQuickLinksActivity.this, "Please select and edit a category", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle deleting a category
        deleteCategoryButton.setOnClickListener(v -> {
            if (selectedCategoryId != -1) {
                deleteCategory(selectedCategoryId);
            } else {
                Toast.makeText(AdminQuickLinksActivity.this, "Please select a category to delete", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle adding a new link
        submitAddLink.setOnClickListener(v -> {
            String linkTitle = linkTitleInput.getText().toString().trim();
            String linkUrl = linkUrlInput.getText().toString().trim();

            // Get the selected category from linkCategorySpinner
            CategoryData selectedCategory = (CategoryData) addLinkCategorySpinner.getSelectedItem();

            if (selectedCategory != null && !linkTitle.isEmpty() && !linkUrl.isEmpty()) {
                int categoryIdForLink = selectedCategory.getCategoryId(); // Use the correct category ID for the link
                manageLink("add", categoryIdForLink, null, linkTitle, linkUrl); // Pass the correct category ID for adding a link
            } else {
                Toast.makeText(AdminQuickLinksActivity.this, "Please fill in all fields for the link", Toast.LENGTH_SHORT).show();
            }
        });


        // Handle deleting a link
        deleteLinkButton.setOnClickListener(v -> {
            if (selectedLinkId != -1) {
                manageLink("delete", null, selectedLinkId, null, null);  // Call manageLink with "delete" action
            } else {
                Toast.makeText(AdminQuickLinksActivity.this, "Please select a link to delete", Toast.LENGTH_SHORT).show();
            }
        });

        // Spinner for selecting category to delete links
        deleteLinkCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategoryId = categoriesList.get(position).getCategoryId();
                Log.d("AdminQuickLinksActivity", "Selected category ID for loading links: " + selectedCategoryId);
                loadLinks(selectedCategoryId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategoryId = -1;
            }
        });

        // Spinner for deleting links
        deleteLinkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("AdminQuickLinksActivity", "Item selected in deleteLinkSpinner: position " + position);
                if (position >= 0 && position < linksList.size()) {
                    selectedLinkId = linksList.get(position).getLinkId();
                    Log.d("AdminQuickLinksActivity", "Selected link ID: " + selectedLinkId);
                } else {
                    selectedLinkId = -1;  // Reset if no valid selection
                    Log.d("AdminQuickLinksActivity", "Invalid position selected for link");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedLinkId = -1;
                Log.d("AdminQuickLinksActivity", "Nothing selected in deleteLinkSpinner");
            }
        });
    }

    // Load categories for spinners
    private void loadCategories() {
        Call<List<CategoryData>> call = apiService.getCategories();
        call.enqueue(new Callback<List<CategoryData>>() {
            @Override
            public void onResponse(Call<List<CategoryData>> call, Response<List<CategoryData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoriesList = response.body();
                    ArrayAdapter<CategoryData> adapter = new ArrayAdapter<>(AdminQuickLinksActivity.this,
                            android.R.layout.simple_spinner_item, categoriesList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(adapter);
                    addLinkCategorySpinner.setAdapter(adapter);
                    deleteLinkCategorySpinner.setAdapter(adapter);
                } else {
                    Toast.makeText(AdminQuickLinksActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CategoryData>> call, Throwable t) {
                Toast.makeText(AdminQuickLinksActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load links for selected category
    private void loadLinks(int categoryId) {
        Log.d("AdminQuickLinksActivity", "Start loading links for category ID: " + categoryId);

        Call<List<LinkData>> call = apiService.getLinksForCategory(categoryId);
        call.enqueue(new Callback<List<LinkData>>() {
            @Override
            public void onResponse(Call<List<LinkData>> call, Response<List<LinkData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    linksList = response.body(); // Set the fetched links
                    Log.d("AdminQuickLinksActivity", "Successfully loaded links for category ID: " + categoryId);
                    Log.d("AdminQuickLinksActivity", "Number of links loaded: " + linksList.size());

                    // Bind the links to the spinner
                    ArrayAdapter<LinkData> adapter = new ArrayAdapter<>(AdminQuickLinksActivity.this,
                            android.R.layout.simple_spinner_item, linksList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // Check if there are any links and bind to the spinner
                    if (linksList.isEmpty()) {
                        Log.d("AdminQuickLinksActivity", "No links found for category ID: " + categoryId);
                        Toast.makeText(AdminQuickLinksActivity.this, "No links available for this category", Toast.LENGTH_SHORT).show();
                    }

                    // Set adapter to spinner
                    deleteLinkSpinner.setAdapter(adapter);
                } else {
                    Log.d("AdminQuickLinksActivity", "Failed to get links from API: " + response.errorBody());
                    Toast.makeText(AdminQuickLinksActivity.this, "Failed to load links", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LinkData>> call, Throwable t) {
                Log.e("AdminQuickLinksActivity", "Error fetching links: " + t.getMessage());
                Toast.makeText(AdminQuickLinksActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Manage (Add/Delete) a link
    private void manageLink(String action, Integer categoryId, Integer linkId, String linkTitle, String linkUrl) {
        Call<ResponseBody> call = apiService.manageLink(action, categoryId, linkId, linkTitle, linkUrl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String message = action.equals("add") ? "Link added successfully" : "Link deleted successfully";
                    Toast.makeText(AdminQuickLinksActivity.this, message, Toast.LENGTH_SHORT).show();

                    if (action.equals("add")) {
                        // Clear input fields after adding a link
                        linkTitleInput.setText("");
                        linkUrlInput.setText("");
                    }

                    loadLinks(selectedCategoryId); // Reload links after add/delete
                } else {
                    Toast.makeText(AdminQuickLinksActivity.this, "Failed to " + action + " link", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AdminQuickLinksActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Add a new category
    private void addCategory(String categoryName) {
        Call<ResponseBody> call = apiService.addCategory(categoryName);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminQuickLinksActivity.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                    addCategoryInput.setText(""); // Clear input field
                    loadCategories(); // Reload categories
                } else {
                    Toast.makeText(AdminQuickLinksActivity.this, "Failed to add category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AdminQuickLinksActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Edit a category
    private void editCategory(int categoryId, String newCategoryName) {
        Call<ResponseBody> call = apiService.editCategory(categoryId, newCategoryName);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminQuickLinksActivity.this, "Category updated successfully", Toast.LENGTH_SHORT).show();
                    editCategoryInput.setText(""); // Clear input field
                    loadCategories(); // Reload categories
                } else {
                    Toast.makeText(AdminQuickLinksActivity.this, "Failed to update category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AdminQuickLinksActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Delete a category
    private void deleteCategory(int categoryId) {
        Call<ResponseBody> call = apiService.deleteCategory(categoryId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminQuickLinksActivity.this, "Category deleted successfully", Toast.LENGTH_SHORT).show();
                    loadCategories(); // Reload categories
                } else {
                    Toast.makeText(AdminQuickLinksActivity.this, "Failed to delete category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AdminQuickLinksActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
