package AdminAddShelter;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditShelterActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Spinner shelterSpinner;
    private EditText shelterName, personInCharge, primaryContact, secondaryContact, streetAddress, town, city, region, capacity;
    private Button submitShelter, deleteShelter;
    private TaskApi taskApi;
    private GoogleMap mMap;
    private LatLng selectedLatLng;  // To store the selected LatLng from the map
    private List<AdminShelter> shelterList;
    private int shelterId;  // Dynamically updated shelter ID
    private boolean isMapReady = false;  // Track if the map is ready
    private AdminShelter pendingShelter = null; // Store shelter for later map update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shelter);

        // Initialize Retrofit API instance
        taskApi = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Shelter Location");

        // Initializing fields
        shelterSpinner = findViewById(R.id.shelter_spinner);
        shelterName = findViewById(R.id.shelter_name);
        personInCharge = findViewById(R.id.person_in_charge);
        primaryContact = findViewById(R.id.primary_contact);
        secondaryContact = findViewById(R.id.secondary_contact);
        streetAddress = findViewById(R.id.street_address);
        town = findViewById(R.id.town);
        city = findViewById(R.id.city);
        region = findViewById(R.id.region);
        capacity = findViewById(R.id.capacity);
        submitShelter = findViewById(R.id.submit_button);
        deleteShelter = findViewById(R.id.delete_button);

        // Initialize Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Load shelter data from API and populate spinner
        loadShelters();

        // Set listener for shelter spinner selection
        shelterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (shelterList != null && shelterList.size() > 0) {
                    AdminShelter selectedShelter = shelterList.get(position);
                    shelterId = selectedShelter.getId();  // Update the shelter ID

                    // Populate shelter fields immediately
                    populateShelterDetails(selectedShelter);

                    // Store selected shelter for map update
                    pendingShelter = selectedShelter;

                    // If map is ready, update the map immediately
                    if (isMapReady) {
                        updateMapLocation(selectedShelter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                shelterId = -1;  // No shelter selected
            }
        });

        // Set OnClickListener for submit button
        submitShelter.setOnClickListener(v -> {
            AdminShelter selectedShelter = (AdminShelter) shelterSpinner.getSelectedItem();
            if (selectedShelter != null) {
                updateShelterDetails(selectedShelter);
            }
        });

        // Set OnClickListener for delete button
        deleteShelter.setOnClickListener(v -> {
            AdminShelter selectedShelter = (AdminShelter) shelterSpinner.getSelectedItem();
            if (selectedShelter != null) {
                deleteShelter(shelterId);  // Use the shelter ID here
            }
        });
    }

    // Method to load shelters from the API
    private void loadShelters() {
        Call<List<AdminShelter>> call = taskApi.getadminShelters();
        call.enqueue(new Callback<List<AdminShelter>>() {
            @Override
            public void onResponse(Call<List<AdminShelter>> call, Response<List<AdminShelter>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    shelterList = response.body();
                    populateShelterSpinner();
                } else {
                    Toast.makeText(EditShelterActivity.this, "Failed to load shelters", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AdminShelter>> call, Throwable t) {
                Toast.makeText(EditShelterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Populate spinner with shelter names
    private void populateShelterSpinner() {
        if (shelterList != null && !shelterList.isEmpty()) {
            ArrayAdapter<AdminShelter> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, shelterList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            shelterSpinner.setAdapter(adapter);
        }
    }

    // Populate the fields with the selected shelter's details
    private void populateShelterDetails(AdminShelter shelter) {
        shelterName.setText(shelter.getName());
        personInCharge.setText(shelter.getPersonInCharge());
        primaryContact.setText(shelter.getContactNumber());
        secondaryContact.setText(shelter.getSecondaryContact());
        streetAddress.setText(shelter.getStreetAddress());
        town.setText(shelter.getTown());
        city.setText(shelter.getCity());
        region.setText(shelter.getRegion());
        capacity.setText(String.valueOf(shelter.getCapacity()));

        selectedLatLng = new LatLng(shelter.getLatitude(), shelter.getLongitude());
    }

    // Update the map location with the selected shelter's coordinates
    private void updateMapLocation(AdminShelter shelter) {
        if (shelter != null && mMap != null) {
            LatLng shelterLocation = new LatLng(shelter.getLatitude(), shelter.getLongitude());

            selectedLatLng = shelterLocation;  // Set initial position

            // Clear old markers and add a new marker (without draggable option)
            mMap.clear();  // Clear any old markers
            mMap.addMarker(new MarkerOptions()
                    .position(selectedLatLng)
                    .title(shelter.getName()));  // Marker should not be draggable

            // Move the camera to the shelter location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15));
        }
    }

    // Method to update shelter details
    private void updateShelterDetails(AdminShelter shelter) {
        String name = shelterName.getText().toString();
        String person = personInCharge.getText().toString();
        String primary = primaryContact.getText().toString();
        String secondary = secondaryContact.getText().toString();
        String address = streetAddress.getText().toString();
        String townText = town.getText().toString();
        String cityText = city.getText().toString();
        String regionText = region.getText().toString();
        String capacityText = capacity.getText().toString();

        if (!name.isEmpty() && !person.isEmpty() && !primary.isEmpty() && !address.isEmpty() && !capacityText.isEmpty()) {
            // Send updated shelter data to API
            Call<Void> call = taskApi.updateShelter(
                    shelter.getId(),  // Pass the shelter ID here
                    name,
                    person,
                    primary,
                    secondary,
                    address,
                    townText,
                    cityText,
                    regionText,
                    selectedLatLng.latitude,
                    selectedLatLng.longitude,
                    Integer.parseInt(capacityText)
            );
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EditShelterActivity.this, "Shelter updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditShelterActivity.this, "Failed to update shelter", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(EditShelterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to delete a shelter
    private void deleteShelter(int shelterId) {
        Call<Void> call = taskApi.deleteShelter(shelterId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditShelterActivity.this, "Shelter deleted successfully", Toast.LENGTH_SHORT).show();
                    loadShelters();  // Reload shelters after deletion
                } else {
                    Toast.makeText(EditShelterActivity.this, "Failed to delete shelter", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditShelterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        isMapReady = true;

        // Enable map gestures if necessary
        mMap.getUiSettings().setAllGesturesEnabled(true);

        // Set the OnMapClickListener to listen for map click events
        mMap.setOnMapClickListener(latLng -> {
            // Move the marker to the clicked position
            selectedLatLng = latLng;
            updateMarkerPosition(latLng); // Update the marker position
        });

        // Log to confirm map readiness
        if (pendingShelter != null) { // In case the shelter is loaded before map is ready
            updateMapLocation(pendingShelter);  // Update the map when it's ready
        }
    }

    private void updateMarkerPosition(LatLng latLng) {
        if (mMap != null) {
            mMap.clear(); // Clear any existing markers
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Selected Location"));  // Add a marker at the clicked location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));  // Adjust the camera to the new position
        }
    }

    // Handle back button press
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();  // Handle toolbar back button press
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Handle system back button press if necessary
        super.onBackPressed();
        finish();  // Close activity when back is pressed
    }
}
