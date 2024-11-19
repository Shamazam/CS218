package SuperAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import LoginRegister.Login;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuperAdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private ImageView profileIcon;
    private TextView userCountTextView, adminCountTextView, profileNameTextView;
    private Button addAdminButton, editAdminButton;
    private TaskApi taskApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_dashboard);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        profileIcon = findViewById(R.id.profileIcon);
        userCountTextView = findViewById(R.id.userCountTextView);
        adminCountTextView = findViewById(R.id.adminCountTextView);
        addAdminButton = findViewById(R.id.addAdminButton);
        editAdminButton = findViewById(R.id.editAdminButton);

        // Retrieve first name from intent
        Intent intent = getIntent();
        String firstName = intent.getStringExtra("first_name");

        // Initialize the TextViews for user and admin counts
        profileNameTextView = navigationView.getHeaderView(0).findViewById(R.id.profileName);
        profileNameTextView.setText("Welcome " + firstName);

        // Initialize Retrofit API
        taskApi = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        // Fetch the counts and update the UI
        fetchCounts();

        // Set up the drawer toggle
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set profile icon to open the drawer
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        // Set navigation listener
        navigationView.setNavigationItemSelectedListener(this);

        // Set up click listeners for buttons
        addAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Add Admin screen
                Intent intent = new Intent(SuperAdminDashboardActivity.this, AddAdminActivity.class);
                startActivity(intent);
            }
        });

        editAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Edit Admin screen
                Intent intent = new Intent(SuperAdminDashboardActivity.this, EditAdminActivity.class);
                startActivity(intent);
            }
        });
    }

    // Fetch the user and admin counts from the API
    private void fetchCounts() {
        Call<CountsResponse> call = taskApi.getCounts();
        call.enqueue(new Callback<CountsResponse>() {
            @Override
            public void onResponse(Call<CountsResponse> call, Response<CountsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CountsResponse countsResponse = response.body();
                    userCountTextView.setText(String.valueOf(countsResponse.getUserCount()));
                    adminCountTextView.setText(String.valueOf(countsResponse.getAdminCount()));
                } else {
                    Toast.makeText(SuperAdminDashboardActivity.this, "Failed to load counts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CountsResponse> call, Throwable t) {
                Toast.makeText(SuperAdminDashboardActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle navigation item clicks
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            // Handle Logout - redirect to Login screen and clear the activity stack
            Intent intent = new Intent(SuperAdminDashboardActivity.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear backstack
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
