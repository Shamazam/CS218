package QuickLinks;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuickLinksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QuickLinksAdapter adapter;
    private List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_links);

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.quickLinksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Toolbar and back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove the default app title from the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Handle the back button click
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        // Fetch categories and links from the server
        fetchQuickLinks();
    }

    private void fetchQuickLinks() {
        TaskApi apiInterface = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);
        Call<QuickLinksResponse> call = apiInterface.getQuickLinks();

        call.enqueue(new Callback<QuickLinksResponse>() {
            @Override
            public void onResponse(Call<QuickLinksResponse> call, Response<QuickLinksResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    QuickLinksResponse quickLinksResponse = response.body();

                    // Get the list of categories from the 'data' field
                    categoryList = quickLinksResponse.getData();

                    // Set up the adapter with the category list
                    adapter = new QuickLinksAdapter(categoryList);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(QuickLinksActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<QuickLinksResponse> call, Throwable t) {
                Toast.makeText(QuickLinksActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
