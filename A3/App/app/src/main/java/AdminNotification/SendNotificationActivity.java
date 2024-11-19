package AdminNotification;

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

import java.util.ArrayList;
import java.util.List;

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotificationActivity extends AppCompatActivity {

    private EditText notificationTitle, notificationMessage;
    private Button btnSendNotification, btnDeleteNotification;
    private Spinner notificationSpinner;
    private TaskApi apiService;
    private List<String> notificationTitles = new ArrayList<>();  // List to hold notification titles
    private List<Integer> notificationIds = new ArrayList<>();    // List to hold notification IDs
    private int selectedNotificationId = -1;  // To keep track of the selected notification's ID for deletion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show the back arrow
            getSupportActionBar().setTitle("Send Notification");   // Set toolbar title
        }

        // Initialize Views
        notificationTitle = findViewById(R.id.notification_title);
        notificationMessage = findViewById(R.id.notification_message);
        btnSendNotification = findViewById(R.id.btn_send_notification);
        btnDeleteNotification = findViewById(R.id.btn_delete_notification);
        notificationSpinner = findViewById(R.id.notification_spinner);

        // Initialize API service
        apiService = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        // Handle Send Notification button click
        btnSendNotification.setOnClickListener(v -> {
            String title = notificationTitle.getText().toString();
            String message = notificationMessage.getText().toString();

            if (!title.isEmpty() && !message.isEmpty()) {
                sendNotification(title, message);
            } else {
                Toast.makeText(SendNotificationActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Delete Notification button click
        btnDeleteNotification.setOnClickListener(v -> {
            if (selectedNotificationId != -1) {
                deleteNotification(selectedNotificationId);
            } else {
                Toast.makeText(SendNotificationActivity.this, "Please select a notification to delete", Toast.LENGTH_SHORT).show();
            }
        });

        // Load notifications into spinner
        loadNotificationTitles();

        // Handle spinner selection
        notificationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the corresponding ID for the selected title
                selectedNotificationId = notificationIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedNotificationId = -1;  // No selection
            }
        });
    }

    // Method to send notification using the API
    private void sendNotification(String title, String message) {
        Call<Void> call = apiService.sendNotification(title, message);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SendNotificationActivity.this, "Notification sent successfully", Toast.LENGTH_SHORT).show();
                    // Clear input fields after success
                    notificationTitle.setText("");
                    notificationMessage.setText("");
                    loadNotificationTitles();  // Refresh notification list after adding new notification
                } else {
                    Toast.makeText(SendNotificationActivity.this, "Failed to send notification", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SendNotificationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to load notification titles into the spinner
    private void loadNotificationTitles() {
        Call<List<NotificationData>> call = apiService.getNotificationTitles();  // API call to get titles and IDs
        call.enqueue(new Callback<List<NotificationData>>() {
            @Override
            public void onResponse(Call<List<NotificationData>> call, Response<List<NotificationData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NotificationData> notifications = response.body();
                    notificationTitles.clear();
                    notificationIds.clear();
                    for (NotificationData notification : notifications) {
                        notificationTitles.add(notification.getTitle());
                        notificationIds.add(notification.getId());  // Store the ID for deletion
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(SendNotificationActivity.this,
                            android.R.layout.simple_spinner_item, notificationTitles);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    notificationSpinner.setAdapter(adapter);  // Set adapter for spinner
                } else {
                    Toast.makeText(SendNotificationActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NotificationData>> call, Throwable t) {
                Toast.makeText(SendNotificationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to delete the selected notification using the API
    private void deleteNotification(int notificationId) {
        Call<ResponseBody> call = apiService.deleteNotification(notificationId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SendNotificationActivity.this, "Notification deleted successfully", Toast.LENGTH_SHORT).show();
                    loadNotificationTitles();  // Refresh notification list after deletion
                } else {
                    Toast.makeText(SendNotificationActivity.this, "Failed to delete notification", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SendNotificationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle back button press on toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the current activity and go back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Handle back button on device
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Close the activity
    }
}
