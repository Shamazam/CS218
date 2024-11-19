package AdminDisasterTips;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

import readyfiji.app.Disaster;
import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import readyfiji.app.TaskItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAddTasksActivity extends AppCompatActivity {

    private EditText disasterName, taskName, newDisasterName, newTaskName;
    private Spinner spinnerDisasters, spinnerDeleteDisasters, spinnerDeleteTaskDisasters, spinnerDeleteTasks;
    private Spinner spinnerEditDisaster, spinnerEditTaskDisasters, spinnerEditTasks;
    private Button btnSubmitDisaster, btnSubmitTask, btnDeleteDisaster, btnDeleteTask;
    private Button btnEditDisaster, btnEditTask;
    private TaskApi apiService;
    private List<Disaster> disasterList;
    private List<TaskItem> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_tasks);

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable back button in the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize fields
        disasterName = findViewById(R.id.disaster_name);
        taskName = findViewById(R.id.task_name);
        newDisasterName = findViewById(R.id.new_disaster_name);
        newTaskName = findViewById(R.id.new_task_name);
        spinnerDisasters = findViewById(R.id.spinner_disasters);
        spinnerDeleteDisasters = findViewById(R.id.spinner_delete_disasters);
        spinnerDeleteTaskDisasters = findViewById(R.id.spinner_delete_task_disasters);
        spinnerDeleteTasks = findViewById(R.id.spinner_delete_tasks);
        spinnerEditDisaster = findViewById(R.id.spinner_edit_disaster);
        spinnerEditTaskDisasters = findViewById(R.id.spinner_edit_task_disasters);
        spinnerEditTasks = findViewById(R.id.spinner_edit_tasks);

        btnSubmitDisaster = findViewById(R.id.btn_submit_disaster);
        btnSubmitTask = findViewById(R.id.btn_submit_task);
        btnDeleteDisaster = findViewById(R.id.btn_delete_disaster);
        btnDeleteTask = findViewById(R.id.btn_delete_task);
        btnEditDisaster = findViewById(R.id.btn_edit_disaster);
        btnEditTask = findViewById(R.id.btn_edit_task);

        // Initialize API service
        apiService = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        // Load disasters from server and populate spinners
        loadDisasters();

        // Handle disaster submission
        btnSubmitDisaster.setOnClickListener(v -> {
            String disaster = disasterName.getText().toString();
            if (!disaster.isEmpty()) {
                submitDisasterToServer(disaster);
            } else {
                Toast.makeText(AdminAddTasksActivity.this, "Please enter a disaster name", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle task submission
        btnSubmitTask.setOnClickListener(v -> {
            String task = taskName.getText().toString();
            Disaster selectedDisaster = (Disaster) spinnerDisasters.getSelectedItem();
            if (!task.isEmpty() && selectedDisaster != null) {
                submitAdminTaskToServer(selectedDisaster.getDisasterId(), task);
            } else {
                Toast.makeText(AdminAddTasksActivity.this, "Please select a disaster and enter a task name", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle delete disaster
        btnDeleteDisaster.setOnClickListener(v -> {
            Disaster selectedDisaster = (Disaster) spinnerDeleteDisasters.getSelectedItem();
            if (selectedDisaster != null) {
                deleteDisasterFromServer(selectedDisaster.getDisasterId());
            } else {
                Toast.makeText(AdminAddTasksActivity.this, "Please select a disaster to delete", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle delete task
        btnDeleteTask.setOnClickListener(v -> {
            TaskItem selectedTask = (TaskItem) spinnerDeleteTasks.getSelectedItem();
            if (selectedTask != null) {
                deleteTaskFromServer(selectedTask.getTaskId());
            } else {
                Toast.makeText(AdminAddTasksActivity.this, "Please select a task to delete", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle edit disaster
        btnEditDisaster.setOnClickListener(v -> {
            Disaster selectedDisaster = (Disaster) spinnerEditDisaster.getSelectedItem();
            String newDisasterNameText = newDisasterName.getText().toString();
            if (selectedDisaster != null && !newDisasterNameText.isEmpty()) {
                editDisasterOnServer(selectedDisaster.getDisasterId(), newDisasterNameText);
            } else {
                Toast.makeText(AdminAddTasksActivity.this, "Please select a disaster and enter a new name", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle edit task
        btnEditTask.setOnClickListener(v -> {
            TaskItem selectedTask = (TaskItem) spinnerEditTasks.getSelectedItem();
            String newTaskNameText = newTaskName.getText().toString();
            if (selectedTask != null && !newTaskNameText.isEmpty()) {
                editTaskOnServer(selectedTask.getTaskId(), newTaskNameText);
            } else {
                Toast.makeText(AdminAddTasksActivity.this, "Please select a task and enter a new name", Toast.LENGTH_SHORT).show();
            }
        });

        // Load tasks when a disaster is selected for task deletion
        spinnerDeleteTaskDisasters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Disaster selectedDisaster = (Disaster) parent.getSelectedItem();
                loadTasksForDisaster(selectedDisaster.getDisasterId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Load tasks for editing when a disaster is selected for task editing
        spinnerEditTaskDisasters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Disaster selectedDisaster = (Disaster) parent.getSelectedItem();
                loadTasksForEditing(selectedDisaster.getDisasterId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    // Load disasters from server
    private void loadDisasters() {
        Call<List<Disaster>> call = apiService.getDisasters();
        call.enqueue(new Callback<List<Disaster>>() {
            @Override
            public void onResponse(Call<List<Disaster>> call, Response<List<Disaster>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    disasterList = response.body();
                    populateDisasterSpinner(disasterList);
                } else {
                    Toast.makeText(AdminAddTasksActivity.this, "Failed to load disasters", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Disaster>> call, Throwable t) {
                Toast.makeText(AdminAddTasksActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Populate disaster spinners
    private void populateDisasterSpinner(List<Disaster> disasterList) {
        ArrayAdapter<Disaster> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, disasterList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDisasters.setAdapter(adapter);
        spinnerDeleteDisasters.setAdapter(adapter);
        spinnerDeleteTaskDisasters.setAdapter(adapter);
        spinnerEditDisaster.setAdapter(adapter);
        spinnerEditTaskDisasters.setAdapter(adapter);
    }

    // Populate task spinner for deletion
    private void populateTaskSpinner(List<TaskItem> taskList) {
        ArrayAdapter<TaskItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, taskList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeleteTasks.setAdapter(adapter);
    }

    // Populate task spinner for editing
    private void populateEditTaskSpinner(List<TaskItem> taskList) {
        ArrayAdapter<TaskItem> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, taskList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEditTasks.setAdapter(adapter);
    }

    // Load tasks for the selected disaster (for deletion)
    private void loadTasksForDisaster(int disasterId) {
        Call<List<TaskItem>> call = apiService.getTasksForDisaster(disasterId, "admin");
        call.enqueue(new Callback<List<TaskItem>>() {
            @Override
            public void onResponse(Call<List<TaskItem>> call, Response<List<TaskItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    taskList = response.body();
                    populateTaskSpinner(taskList);
                } else {
                    Toast.makeText(AdminAddTasksActivity.this, "Failed to load tasks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TaskItem>> call, Throwable t) {
                Toast.makeText(AdminAddTasksActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load tasks for the selected disaster (for editing)
    private void loadTasksForEditing(int disasterId) {
        Call<List<TaskItem>> call = apiService.getTasksForDisaster(disasterId, "admin");
        call.enqueue(new Callback<List<TaskItem>>() {
            @Override
            public void onResponse(Call<List<TaskItem>> call, Response<List<TaskItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    taskList = response.body();
                    populateEditTaskSpinner(taskList);
                } else {
                    Toast.makeText(AdminAddTasksActivity.this, "Failed to load tasks for editing", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TaskItem>> call, Throwable t) {
                Toast.makeText(AdminAddTasksActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Submit disaster to server
    private void submitDisasterToServer(String disasterName) {
        Call<Void> call = apiService.addDisaster(disasterName);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminAddTasksActivity.this, "Disaster added successfully", Toast.LENGTH_SHORT).show();
                    loadDisasters(); // Reload disasters after adding
                } else {
                    Toast.makeText(AdminAddTasksActivity.this, "Failed to add disaster", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminAddTasksActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Submit task to the server
    private void submitAdminTaskToServer(int disasterId, String taskName) {
        Call<TaskItem> call = apiService.addAdminTask(disasterId, taskName);
        call.enqueue(new Callback<TaskItem>() {
            @Override
            public void onResponse(Call<TaskItem> call, Response<TaskItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AdminAddTasksActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminAddTasksActivity.this, "Failed to add task", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TaskItem> call, Throwable t) {
                Toast.makeText(AdminAddTasksActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Edit disaster on server
    private void editDisasterOnServer(int disasterId, String newDisasterName) {
        Call<Void> call = apiService.editDisaster(disasterId, newDisasterName);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminAddTasksActivity.this, "Disaster edited successfully", Toast.LENGTH_SHORT).show();
                    loadDisasters(); // Reload disasters after editing
                } else {
                    Toast.makeText(AdminAddTasksActivity.this, "Failed to edit disaster", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminAddTasksActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Edit task on server
    private void editTaskOnServer(int taskId, String newTaskName) {
        Call<Void> call = apiService.editTask(taskId, newTaskName);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminAddTasksActivity.this, "Task edited successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminAddTasksActivity.this, "Failed to edit task", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminAddTasksActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Delete disaster from server
    private void deleteDisasterFromServer(int disasterId) {
        Call<Void> call = apiService.deleteDisaster(disasterId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminAddTasksActivity.this, "Disaster deleted successfully", Toast.LENGTH_SHORT).show();
                    loadDisasters(); // Reload disasters after deletion
                } else {
                    Toast.makeText(AdminAddTasksActivity.this, "Failed to delete disaster", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminAddTasksActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Delete task from server
    private void deleteTaskFromServer(int taskId) {
        Call<Void> call = apiService.deleteTask(taskId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminAddTasksActivity.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                    Disaster selectedDisaster = (Disaster) spinnerDeleteTaskDisasters.getSelectedItem();
                    if (selectedDisaster != null) {
                        loadTasksForDisaster(selectedDisaster.getDisasterId());
                    }
                } else {
                    Toast.makeText(AdminAddTasksActivity.this, "Failed to delete task", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminAddTasksActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle back button press in the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Handle toolbar back button press
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // Handle system back button press
        finish(); // Close activity
    }
}
