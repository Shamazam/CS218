package AdminAddShelter;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
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

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddShelterActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText shelterName, personInCharge, primaryContact, secondaryContact, streetAddress, town, city, region, capacity;
    private Button submitShelter;
    private TaskApi taskApi;
    private GoogleMap mMap;
    private LatLng selectedLatLng;  // To store the selected LatLng from the map
    private ScrollView scrollView;  // Reference to the scroll view for disabling/enabling
    private ImageView backButton;   // Back button reference

    // For logging
    private static final String TAG = "AddShelterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shelter);

        // Initialize Retrofit API instance
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        taskApi = retrofit.create(TaskApi.class);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        // Initializing fields
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
        scrollView = findViewById(R.id.scrollView);

        // Initialize Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);  // Use the fragment ID for the map
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);  // Load the map asynchronously
        }

        // Set OnClickListener for submit button
        submitShelter.setOnClickListener(v -> {
            String name = shelterName.getText().toString();
            String person = personInCharge.getText().toString();
            String primary = primaryContact.getText().toString();
            String secondary = secondaryContact.getText().toString();
            String address = streetAddress.getText().toString();
            String townText = town.getText().toString();
            String cityText = city.getText().toString();
            String regionText = region.getText().toString();
            String capacityText = capacity.getText().toString();

            // Validate required fields
            if (name.isEmpty() || person.isEmpty() || primary.isEmpty() || address.isEmpty() || capacityText.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            } else if (selectedLatLng == null) {
                Toast.makeText(this, "Please select a location on the map", Toast.LENGTH_SHORT).show();
            } else {
                // Send the selected latitude and longitude from the map
                sendShelterData(name, person, primary, secondary, address, townText, cityText, regionText, selectedLatLng.latitude, selectedLatLng.longitude, capacityText);
            }
        });
    }

    // Method to send shelter data to the server
    private void sendShelterData(String name, String person, String primary, String secondary,
                                 String address, String town, String city, String region,
                                 double latitude, double longitude, String capacity) {

        Log.d(TAG, "Calling API with Latitude: " + latitude + " and Longitude: " + longitude);

        Call<ResponseBody> call = taskApi.addShelter(
                name,
                person,
                primary,
                secondary,
                address,
                town,
                city,
                region,
                String.valueOf(latitude),   // Convert latitude to String
                String.valueOf(longitude),  // Convert longitude to String
                Integer.parseInt(capacity)
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddShelterActivity.this, "Shelter added successfully!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Shelter added successfully");
                } else {
                    Toast.makeText(AddShelterActivity.this, "Failed to add shelter", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Failed to add shelter. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AddShelterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error sending data to the server: " + t.getMessage());
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

        // Disable the ScrollView when interacting with the map
        mMap.setOnCameraMoveStartedListener(i -> scrollView.requestDisallowInterceptTouchEvent(true));
        mMap.setOnCameraIdleListener(() -> scrollView.requestDisallowInterceptTouchEvent(false));
    }
}
