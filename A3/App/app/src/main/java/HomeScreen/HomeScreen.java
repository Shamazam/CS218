package HomeScreen;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import AboutUs.AboutUsActivity;
import ChangePassword.ChangePasswordActivity;
import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import readyfiji.app.ToDoListActivity;
import DisasterMap.DisasterMapActivity;
import EditProfile.EditProfileActivity;
import EditProfile.UserProfile;
import EmergencyContacts.EmergencyContactsActivity;
import LoginRegister.Login;

import NotificationInbox.NotificationHistoryActivity;
import QuickLinks.QuickLinksActivity;
import Shelter.ShelterActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView latestUpdateImage;
    private TextView latestUpdateTitle, latestUpdateDescription;
    private List<NewsItem> newsItems;
    private int currentItem = 0;
    private Handler handler;
    private Runnable updateRunnable;
    private int userId;
    private String firstName;

    // Drawer-related
    private DrawerLayout drawerLayout;
    private TextView profileName;
    private ImageView profileIcon, navProfileIcon; // For both header and navigation drawer

    // Unread messages
    private TextView unreadBadge;  // Badge for unread messages

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Get the logged-in user's first_name and userId from the Intent passed by Login activity
        Intent intent = getIntent();
        firstName = intent.getStringExtra("first_name");
        userId = intent.getIntExtra("user_id", -1);

        Log.d("HomeScreen", "Received User ID: " + userId);
        Log.d("HomeScreen", "Received User First Name: " + firstName);

        // Setup latest updates section
        latestUpdateTitle = findViewById(R.id.latestUpdateTitle);
        latestUpdateDescription = findViewById(R.id.latestUpdateDescription);
        latestUpdateImage = findViewById(R.id.latestUpdateImage);

        // Setup navigation drawer
        drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set an empty title for the toolbar to avoid showing the app name on the home screen
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        // Set profile picture and name in the drawer header
        View headerView = navigationView.getHeaderView(0);
        navProfileIcon = headerView.findViewById(R.id.profilePicture); // Profile icon in navigation drawer
        profileName = headerView.findViewById(R.id.profileName);

        // Set the profile name dynamically
        profileName.setText("Welcome, " + firstName);

        // Profile icon click listener to open the drawer
        profileIcon = findViewById(R.id.profileIcon); // Top-right profile icon
        profileIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Fetch user profile data and load the profile image (both header and drawer icon)
        fetchUserProfileData(userId);

        // Bell icon and unread badge setup
        ImageView bellIcon = findViewById(R.id.notificationBellIcon);
        unreadBadge = findViewById(R.id.badgeCount);  // Initialize the unread badge

        // Fetch unread messages count dynamically
        fetchUnreadMessages(userId);

        // Bell icon click listener to open notification screen
        bellIcon.setOnClickListener(v -> {
            Log.d("HomeScreen", "Navigating to NotificationHistoryActivity with User ID: " + userId);
            Intent intent1 = new Intent(HomeScreen.this, NotificationHistoryActivity.class);
            intent1.putExtra("user_id", userId);
            intent1.putExtra("first_name", firstName);
            startActivity(intent1);
        });

        // Load news updates from API
        fetchNewsData();

        // Underline the News Alerts Title
        TextView newsAlertsTitle = findViewById(R.id.newsAlertsTitle);
        newsAlertsTitle.setPaintFlags(newsAlertsTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Setup icons for each screen
        ImageView todoListIcon = findViewById(R.id.todoListIcon);
        ImageView shelterLocationsIcon = findViewById(R.id.shelterLocationsIcon);
        ImageView liveTrackingIcon = findViewById(R.id.liveTrackingIcon);
        ImageView emergencyContactsIcon = findViewById(R.id.emergencyContactsIcon);
        ImageView quickLinksIcon = findViewById(R.id.quickLinksIcon);
        ImageView aboutUsIcon = findViewById(R.id.aboutUsIcon);

        // Set listeners for each icon to navigate to the respective screens
        todoListIcon.setOnClickListener(v -> {
            Intent act = new Intent(HomeScreen.this, ToDoListActivity.class);
            act.putExtra("userId", userId);
            startActivity(act);
        });

        shelterLocationsIcon.setOnClickListener(v -> {
            Intent act = new Intent(HomeScreen.this, ShelterActivity.class);
            startActivity(act);
        });

        liveTrackingIcon.setOnClickListener(v -> {
            Intent act = new Intent(HomeScreen.this, DisasterMapActivity.class);
            startActivity(act);
        });

        emergencyContactsIcon.setOnClickListener(v -> {
            Intent act = new Intent(HomeScreen.this, EmergencyContactsActivity.class);
            startActivity(act);
        });

        quickLinksIcon.setOnClickListener(v -> {
            Intent act = new Intent(HomeScreen.this, QuickLinksActivity.class);
            startActivity(act);
        });

        aboutUsIcon.setOnClickListener(v -> {
            Intent act = new Intent(HomeScreen.this, AboutUsActivity.class);
            startActivity(act);
        });
    }


    // Method to fetch the user's profile data
    private void fetchUserProfileData(int userId) {
        TaskApi taskApi = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        // Pass the userId dynamically in the API call using @Query
        Call<UserProfile> call = taskApi.getUserProfile(userId);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile userProfile = response.body();

                    // Load the profile image if available for both header and navigation drawer
                    if (userProfile.getProfileImage() != null && !userProfile.getProfileImage().isEmpty()) {
                        new ImageLoaderTask(profileIcon).execute(userProfile.getProfileImage()); // For top-right icon
                        new ImageLoaderTask(navProfileIcon).execute(userProfile.getProfileImage()); // For drawer icon
                    }
                } else {
                    Log.e("HomeScreen", "Failed to fetch profile data.");
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.e("HomeScreen", "Error fetching profile data: " + t.getMessage());
            }
        });
    }

    // Method to set the unread message count
    private void setUnreadCount(int count) {
        if (count > 0) {
            unreadBadge.setVisibility(View.VISIBLE);
            unreadBadge.setText(String.valueOf(count));
        } else {
            unreadBadge.setVisibility(View.GONE);
        }
    }

    // Fetch unread message count from the server
    private void fetchUnreadMessages(int userId) {
        TaskApi taskApi = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        Call<UnreadNotificationResponse> call = taskApi.getUnreadNotifications(userId);
        call.enqueue(new Callback<UnreadNotificationResponse>() {
            @Override
            public void onResponse(Call<UnreadNotificationResponse> call, Response<UnreadNotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int unreadCount = response.body().getUnreadCount();  // Parse unread_count from response
                    setUnreadCount(unreadCount);  // Update the unread badge count
                } else {
                    Log.e("HomeScreen", "Failed to fetch unread messages.");
                }
            }

            @Override
            public void onFailure(Call<UnreadNotificationResponse> call, Throwable t) {
                Log.e("HomeScreen", "Error fetching unread messages: " + t.getMessage());
            }
        });
    }

    // Drawer menu item click listener
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_edit_profile) {
            Intent intent = new Intent(HomeScreen.this, EditProfileActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(HomeScreen.this, ChangePasswordActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("first_name", firstName);
            startActivity(intent);
        } else if (id == R.id.menu_delete_account) {
            showDeleteAccountConfirmation();
        } else if (id == R.id.menu_logout) {
            Intent intent = new Intent(HomeScreen.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Method to show confirmation dialog for deleting account
    private void showDeleteAccountConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes", (dialog, which) -> deleteUserAccount(userId))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // Method to fetch news data using Retrofit
    private void fetchNewsData() {
        TaskApi taskApi = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);
        Call<List<NewsItem>> call = taskApi.getNewsItems();

        call.enqueue(new Callback<List<NewsItem>>() {
            @Override
            public void onResponse(Call<List<NewsItem>> call, Response<List<NewsItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    newsItems = response.body();
                    if (!newsItems.isEmpty()) {
                        startSlideshow();
                    }
                } else {
                    Log.e("HomeScreen", "No news items available or failed to fetch data.");
                }
            }

            @Override
            public void onFailure(Call<List<NewsItem>> call, Throwable t) {
                Log.e("HomeScreen", "Error fetching news data: " + t.getMessage());
            }
        });
    }

    private void startSlideshow() {
        handler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentItem >= newsItems.size()) {
                    currentItem = 0;
                }
                updateNewsItem(newsItems.get(currentItem));
                currentItem++;
                // Post the handler with a delay after the image has been successfully loaded
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(updateRunnable, 5000);
    }

    private void updateNewsItem(NewsItem newsItem) {
        // Set the news title and ensure both image and description are hidden initially
        latestUpdateTitle.setText(newsItem.getTitle());
        latestUpdateDescription.setVisibility(View.GONE);
        latestUpdateImage.setVisibility(View.GONE);

        if (newsItem.getImageUrl() != null && !newsItem.getImageUrl().isEmpty()) {
            // Load the image and handle visibility in ImageLoaderTask
            new ImageLoaderTask(latestUpdateImage, latestUpdateDescription, newsItem.getDescription()).execute(newsItem.getImageUrl());
        } else {
            // No image available, display the description instead
            latestUpdateDescription.setText(newsItem.getDescription());
            latestUpdateDescription.setVisibility(View.VISIBLE);
        }
    }

    private static class ImageLoaderTask extends AsyncTask<String, Void, Bitmap> {
        private final ImageView imageView;
        private final TextView descriptionView;
        private final String description;

        // Overloaded constructor for simple image loading without description
        public ImageLoaderTask(ImageView imageView) {
            this.imageView = imageView;
            this.descriptionView = null; // No description view in this context
            this.description = null; // No description to display
        }

        // Constructor for loading an image and handling description
        public ImageLoaderTask(ImageView imageView, TextView descriptionView, String description) {
            this.imageView = imageView;
            this.descriptionView = descriptionView;
            this.description = description;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
                imageView.setVisibility(View.VISIBLE);
                if (descriptionView != null) {
                    descriptionView.setVisibility(View.GONE); // Hide description if image is present
                }
            } else if (descriptionView != null && description != null) {
                imageView.setVisibility(View.GONE);
                descriptionView.setText(description);
                descriptionView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }

    private void deleteUserAccount(int userId) {
        TaskApi service = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);
        Call<Void> call = service.deleteUser(userId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(HomeScreen.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                    // Log out the user after successful deletion
                    Intent intent = new Intent(HomeScreen.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Log the response code and body for debugging
                    Log.e("DeleteAccount", "Failed to delete account. Response code: " + response.code());
                    Log.e("DeleteAccount", "Error body: " + response.errorBody());
                    Toast.makeText(HomeScreen.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Log the failure message
                Log.e("DeleteAccount", "Error: " + t.getMessage());
                Toast.makeText(HomeScreen.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
