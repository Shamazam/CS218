package NotificationInbox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import HomeScreen.HomeScreen;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationHistoryActivity extends AppCompatActivity {

    private static final String TAG = "NotificationHistory";

    private RecyclerView notificationRecyclerView;
    private NotificationAdapter notificationAdapter;
    private List<NotificationModel> notificationList;
    private List<NotificationModel> filteredNotificationList;
    private TabLayout tabLayout;
    private int userId;  // Declare userId here
    private String firstName;
    private ImageView backButton;  // Declare backButton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_history);

        // Retrieve userId from Intent and log it
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getIntExtra("user_id", -1);  // Fetch user_id from Intent
            firstName = intent.getStringExtra("first_name");
            Log.d(TAG, "Received User ID: " + userId);
        }

        // Check if userId is valid
        if (userId == -1) {
            Toast.makeText(this, "Invalid User ID", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if userId is invalid
            return;
        }

        // Initialize RecyclerView
        notificationRecyclerView = findViewById(R.id.notificationRecyclerView);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize TabLayout
        tabLayout = findViewById(R.id.tabLayout);
        setupTabs();

        // Fetch Notifications for the user
        fetchNotifications(userId);  // Pass the user ID here

        // Initialize back button and set onClick listener
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Navigate back to HomeScreen, pass userId if needed
            Intent homeIntent = new Intent(NotificationHistoryActivity.this, HomeScreen.class);
            homeIntent.putExtra("user_id", userId);  // Pass user_id back to HomeScreen
            homeIntent.putExtra("first_name", firstName);
            startActivity(homeIntent);
            finish();  // Close the NotificationHistoryActivity
        });
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Read"));
        tabLayout.addTab(tabLayout.newTab().setText("Unread"));

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterNotifications(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void filterNotifications(int tabPosition) {
        if (notificationList == null) return;

        filteredNotificationList = new ArrayList<>();
        switch (tabPosition) {
            case 0: // All notifications
                filteredNotificationList.addAll(notificationList);
                break;
            case 1: // Read notifications
                for (NotificationModel notification : notificationList) {
                    if (notification.isRead()) {
                        filteredNotificationList.add(notification);
                    }
                }
                break;
            case 2: // Unread notifications
                for (NotificationModel notification : notificationList) {
                    if (!notification.isRead()) {
                        filteredNotificationList.add(notification);
                    }
                }
                break;
        }
        if (notificationAdapter != null) {
            notificationAdapter.updateList(filteredNotificationList);
        }
    }

    private void fetchNotifications(int userId) {
        TaskApi api = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);
        Call<NotificationResponse> call = api.getNotificationsForUser(userId);  // Expecting NotificationResponse now

        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NotificationResponse notificationResponse = response.body();
                    if (notificationResponse.getData() != null) {
                        notificationList = notificationResponse.getData();
                        filteredNotificationList = new ArrayList<>(notificationList);

                        // Initialize the adapter with the list and the context
                        notificationAdapter = new NotificationAdapter(filteredNotificationList, NotificationHistoryActivity.this, userId);
                        notificationRecyclerView.setAdapter(notificationAdapter);
                    } else {
                        Log.w(TAG, "No notifications found for User ID: " + userId);
                        Toast.makeText(NotificationHistoryActivity.this, "No notifications found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Failed to load notifications. Response: " + response.message());
                    Toast.makeText(NotificationHistoryActivity.this, "Failed to load notifications", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching notifications: " + t.getMessage());
                Toast.makeText(NotificationHistoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
