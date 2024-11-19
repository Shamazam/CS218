package readyfiji.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import readyfiji.app.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ToDoListActivity extends AppCompatActivity {

    private TaskApi taskApi;
    private LinearLayout disasterLayout;
    private ExpandableTaskAdapter expandableTaskAdapter;
    private int userId;  // Assuming you have a userId field to pass

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the title inline with the back arrow in the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Disasters To Do List"); // Set title inline with the arrow
        }

        // Retrieve userId from Intent inside onCreate
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getIntExtra("userId", -1);  // Fetch user_id from Intent
        }

        // Check if userId is valid
        if (userId == -1) {
            Toast.makeText(this, "Invalid User ID", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if userId is invalid
            return;
        }

        // Initialize Retrofit and the layout
        taskApi = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);
        disasterLayout = findViewById(R.id.disasterLayout);

        // Initialize the expandable adapter with userId
        expandableTaskAdapter = new ExpandableTaskAdapter(this, new ArrayList<>(), taskApi, userId);

        // RecyclerView setup
        RecyclerView expandableRecyclerView = new RecyclerView(this);
        expandableRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        disasterLayout.addView(expandableRecyclerView);
        expandableRecyclerView.setAdapter(expandableTaskAdapter);

        // Fetch disasters and their associated tasks
        fetchDisasters();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();  // Close the current activity and go back
        return true;
    }

    // Fetch all disasters from the server
    private void fetchDisasters() {
        taskApi.getDisasters().enqueue(new Callback<List<Disaster>>() {
            @Override
            public void onResponse(Call<List<Disaster>> call, Response<List<Disaster>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayDisasters(response.body());  // Display each disaster
                } else {
                    Toast.makeText(ToDoListActivity.this, "Failed to fetch disasters", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Disaster>> call, Throwable t) {
                Toast.makeText(ToDoListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Display each disaster and its tasks in collapsible format
    private void displayDisasters(List<Disaster> disasters) {
        expandableTaskAdapter.updateDisasters(disasters);  // Update the disaster list in the adapter

        // Fetch tasks for each disaster
        for (Disaster disaster : disasters) {
            fetchTasksForDisaster(disaster.getDisasterId(), disaster);
        }
    }

    // Fetch tasks for a specific disaster
    private void fetchTasksForDisaster(int disasterId, Disaster disaster) {
        // Fetch tasks from the server
        taskApi.getTasksForDisaster(disasterId, userId).enqueue(new Callback<List<TaskItem>>() {
            @Override
            public void onResponse(Call<List<TaskItem>> call, Response<List<TaskItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TaskItem> tasks = response.body();  // This should be a list of TaskItem
                    expandableTaskAdapter.updateTasksForDisaster(disaster, tasks);  // Update adapter with tasks
                } else {
                    Toast.makeText(ToDoListActivity.this, "Failed to fetch tasks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TaskItem>> call, Throwable t) {
                Toast.makeText(ToDoListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
