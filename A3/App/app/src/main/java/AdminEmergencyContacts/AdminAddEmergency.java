package AdminEmergencyContacts;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import EmergencyContacts.Department;
import EmergencyContacts.DepartmentResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAddEmergency extends AppCompatActivity implements OnMapReadyCallback {

    private EditText departmentName, landline1, landline2, mobile1, mobile2, mobile3, mobile4, streetAddress, town, city, region, buildingName;
    private Spinner spinnerDepartments;
    private Button btnSubmitDepartment, btnSubmitContact;
    private TaskApi apiService;
    private List<Department> departmentList; // List to hold departments
    private GoogleMap mMap;
    private LatLng selectedLatLng;  // To store the selected LatLng from the map
    private MapTouchWrapper mapTouchWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_emergency_contacts);

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Emergency Contacts");
        }

        // Initialize fields
        departmentName = findViewById(R.id.department_name);
        landline1 = findViewById(R.id.landline_number_1);
        landline2 = findViewById(R.id.landline_number_2);
        mobile1 = findViewById(R.id.mobile_number_1);
        mobile2 = findViewById(R.id.mobile_number_2);
        mobile3 = findViewById(R.id.mobile_number_3);
        mobile4 = findViewById(R.id.mobile_number_4);
        streetAddress = findViewById(R.id.street_address);
        town = findViewById(R.id.town);
        city = findViewById(R.id.city);
        region = findViewById(R.id.region);
        buildingName = findViewById(R.id.building_name);
        spinnerDepartments = findViewById(R.id.spinner_departments);
        btnSubmitDepartment = findViewById(R.id.btn_submit_department);
        btnSubmitContact = findViewById(R.id.btn_submit_contact);

        // Initialize API service
        apiService = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        // Initialize Google Map and MapTouchWrapper for smoother touch interaction
        mapTouchWrapper = findViewById(R.id.map_container);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);  // Load the map asynchronously
        }

        // Load departments into spinner
        loadDepartments();

        // Handle department submission
        btnSubmitDepartment.setOnClickListener(v -> {
            String department = departmentName.getText().toString();
            if (!department.isEmpty()) {
                submitDepartmentToServer(department);
            } else {
                Toast.makeText(AdminAddEmergency.this, "Please enter a department name", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle contact submission
        btnSubmitContact.setOnClickListener(v -> {
            String landlineOne = landline1.getText().toString();
            String landlineTwo = landline2.getText().toString();
            String mobileOne = mobile1.getText().toString();
            String mobileTwo = mobile2.getText().toString();
            String mobileThree = mobile3.getText().toString();
            String mobileFour = mobile4.getText().toString();
            String address = streetAddress.getText().toString();
            String townName = town.getText().toString();
            String cityName = city.getText().toString();
            String regionName = region.getText().toString();
            String building = buildingName.getText().toString();
            Department selectedDepartment = (Department) spinnerDepartments.getSelectedItem();

            if (!landlineOne.isEmpty() && !mobileOne.isEmpty() && !address.isEmpty() && !townName.isEmpty() && !cityName.isEmpty() && !building.isEmpty() && selectedDepartment != null && selectedLatLng != null) {
                submitContactToServer(selectedDepartment.getDepartmentId(), landlineOne, landlineTwo, mobileOne, mobileTwo, mobileThree, mobileFour, address, townName, cityName, regionName, selectedLatLng.latitude, selectedLatLng.longitude, building);
            } else {
                Toast.makeText(AdminAddEmergency.this, "Please fill all required fields and select a location on the map", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to load departments into spinner
    private void loadDepartments() {
        Call<DepartmentResponse> call = apiService.getDepartments();
        call.enqueue(new Callback<DepartmentResponse>() {
            @Override
            public void onResponse(Call<DepartmentResponse> call, Response<DepartmentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    departmentList = response.body().getData();
                    populateDepartmentSpinner(departmentList);
                } else {
                    Toast.makeText(AdminAddEmergency.this, "Failed to load departments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DepartmentResponse> call, Throwable t) {
                Toast.makeText(AdminAddEmergency.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Populate spinner with department names
    private void populateDepartmentSpinner(List<Department> departmentList) {
        ArrayAdapter<Department> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departmentList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartments.setAdapter(adapter);
    }

    // Submit department to server
    private void submitDepartmentToServer(String departmentName) {
        Call<Void> call = apiService.addDepartment(departmentName);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminAddEmergency.this, "Department added successfully", Toast.LENGTH_SHORT).show();
                    loadDepartments(); // Refresh spinner after adding new department
                } else {
                    Toast.makeText(AdminAddEmergency.this, "Failed to add department", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminAddEmergency.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Submit emergency contact to server
    private void submitContactToServer(int departmentId, String landlineOne, String landlineTwo, String mobileOne, String mobileTwo, String mobileThree, String mobileFour,
                                       String address, String townName, String cityName, String regionName, double latitude, double longitude, String building) {
        Call<Void> call = apiService.addEmergencyContact(departmentId, landlineOne, landlineTwo, mobileOne, mobileTwo, mobileThree, mobileFour, address, townName, cityName, regionName, String.valueOf(longitude), String.valueOf(latitude), building);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminAddEmergency.this, "Emergency contact added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminAddEmergency.this, "Failed to add contact", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminAddEmergency.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set a default location or zoom level
        LatLng defaultLocation = new LatLng(-17.7134, 178.0650);  // Fiji's default location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));

        // Add marker click listener to get latitude and longitude
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();  // Clear previous marker
            selectedLatLng = latLng;  // Store selected location
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            if (marker != null) {
                marker.showInfoWindow();
            }
        });

        // Ensure the map interacts smoothly with scrolling
        mapTouchWrapper.setGoogleMap(mMap);
    }

    // Handle back button press to return to the dashboard
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();  // Close the current activity and go back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();  // Close the current activity and return to the previous screen
    }
}
