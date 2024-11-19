package AdminHomeScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import AdminAddShelter.AddShelterActivity;
import AdminAddShelter.AdminShelterCapacityActivity;
import AdminAddShelter.EditShelterActivity;
import AdminDisasterTips.AdminAddTasksActivity;
import AdminEmergencyContacts.AdminAddEmergency;
import AdminEmergencyContacts.AdminEditEmergencyActivity;
import AdminNotification.SendNotificationActivity;
import AdminQuickLinks.AdminQuickLinksActivity;
import AdminSendAlert.SendAlertActivity;
import readyfiji.app.R;
import LoginRegister.Login;
import Weather.AdminMapActivity;

public class AdminDashBoardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button addDisasterTipsButton, addShelterLocationsButton, addEmergencyContactsButton, sendNotificationButton, sendAlertButton, changeShelterCapacityButton;
    private Button manageSheltersButton, manageEmergencyContactsButton, manageQuickLinksButton, manageFloodingZonesButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private ImageView userIcon;
    private TextView profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String firstName = intent.getStringExtra("first_name");

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Set Navigation Item listener
        navigationView.setNavigationItemSelectedListener(this);

        // Setup ActionBarDrawerToggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Initialize buttons
        addDisasterTipsButton = findViewById(R.id.addTipsButton);
        addShelterLocationsButton = findViewById(R.id.addShelterButton);
        addEmergencyContactsButton = findViewById(R.id.addContactsButton);
        sendNotificationButton = findViewById(R.id.sendNotificationButton);
        sendAlertButton = findViewById(R.id.sendAlertButton);
        changeShelterCapacityButton = findViewById(R.id.changeShelterCapacityButton);

        // Initialize new buttons
        manageSheltersButton = findViewById(R.id.manageSheltersButton);
        manageEmergencyContactsButton = findViewById(R.id.manageEmergencyContactsButton);
        manageQuickLinksButton = findViewById(R.id.manageQuickLinksButton);
        manageFloodingZonesButton = findViewById(R.id.manageFloodingZonesButton);

        // Initialize user icon to open the drawer
        userIcon = findViewById(R.id.userIcon);
        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        // Set the profile name dynamically inside the drawer header
        View headerView = navigationView.getHeaderView(0);
        profileName = headerView.findViewById(R.id.profileName);
        profileName.setText("Welcome " + firstName);

        // Button listeners
        addDisasterTipsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashBoardActivity.this, AdminAddTasksActivity.class);
                startActivity(intent);
            }
        });

        addShelterLocationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashBoardActivity.this, AddShelterActivity.class);
                startActivity(intent);
            }
        });

        addEmergencyContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashBoardActivity.this, AdminAddEmergency.class);
                startActivity(intent);
            }
        });

        sendNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashBoardActivity.this, SendNotificationActivity.class);
                startActivity(intent);
            }
        });

        sendAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashBoardActivity.this, SendAlertActivity.class);
                startActivity(intent);
            }
        });

        changeShelterCapacityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashBoardActivity.this, AdminShelterCapacityActivity.class);
                startActivity(intent);
            }
        });

        manageSheltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashBoardActivity.this, EditShelterActivity.class); // Replace with actual activity
                startActivity(intent);
            }
        });

        manageEmergencyContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashBoardActivity.this, AdminEditEmergencyActivity.class); // Replace with actual activity
                startActivity(intent);
            }
        });

        manageQuickLinksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashBoardActivity.this, AdminQuickLinksActivity.class);
                startActivity(intent);
            }
        });

        manageFloodingZonesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashBoardActivity.this, AdminMapActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            // Handle Logout
            Intent intent = new Intent(AdminDashBoardActivity.this, Login.class);
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
