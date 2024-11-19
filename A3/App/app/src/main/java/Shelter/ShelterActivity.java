package Shelter;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShelterActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RecyclerView shelterRecyclerView;
    private ShelterAdapter shelterAdapter;
    private List<Shelter> shelterList;
    private GoogleMap mMap;
    private ImageView backButton, filterButton;
    private TextView headingTitle;
    private Set<String> availableTowns = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shelter_activity);  // Ensure you have the correct layout file

        // Setup back button
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());  // Return to previous screen

        // Setup heading title
        headingTitle = findViewById(R.id.headingTitle);
        headingTitle.setText("Evacuation Center Locations");

        // Setup filter button
        filterButton = findViewById(R.id.filterButton);
        filterButton.setOnClickListener(v -> showFilterMenu());  // Show filter options

        // Setup RecyclerView
        shelterRecyclerView = findViewById(R.id.shelterList);
        shelterRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Fetch shelter data dynamically via API call
        fetchSheltersFromAPI();
    }

    // Fetch shelters dynamically from the server
    private void fetchSheltersFromAPI() {
        TaskApi apiInterface = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);
        Call<List<Shelter>> call = apiInterface.getShelters();

        call.enqueue(new Callback<List<Shelter>>() {
            @Override
            public void onResponse(Call<List<Shelter>> call, Response<List<Shelter>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    shelterList = response.body();
                    extractAvailableTowns();  // Extract available towns for the filter
                    setupRecyclerView();
                    if (mMap != null) {
                        addMarkersToMap();
                    }
                } else {
                    Log.e("ShelterActivity", "Failed to fetch shelters: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Shelter>> call, Throwable t) {
                Log.e("ShelterActivity", "Error fetching shelters", t);
            }
        });
    }

    // Extract available towns from the shelter list for filtering
    private void extractAvailableTowns() {
        availableTowns.clear();
        for (Shelter shelter : shelterList) {
            availableTowns.add(shelter.getTown());
        }
    }

    // Setup RecyclerView after data is fetched
    private void setupRecyclerView() {
        shelterAdapter = new ShelterAdapter(shelterList);
        shelterRecyclerView.setAdapter(shelterAdapter);
        shelterAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // If shelter list is already loaded, add markers
        if (shelterList != null && !shelterList.isEmpty()) {
            addMarkersToMap();
        }
    }

    // Helper method to add markers to the map based on shelter locations
    private void addMarkersToMap() {
        for (Shelter shelter : shelterList) {
            LatLng location = shelter.getLocation();
            mMap.addMarker(new MarkerOptions().position(location).title(shelter.getName()));
        }

        // Optionally, move the camera to focus on the region of the shelters
        if (!shelterList.isEmpty()) {
            mMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(shelterList.get(0).getLocation(), 12));
        }
    }

    // Show filter menu with available towns
    private void showFilterMenu() {
        PopupMenu popupMenu = new PopupMenu(this, filterButton);
        popupMenu.getMenu().add("All Towns");  // Add "All Towns" option
        for (String town : availableTowns) {
            popupMenu.getMenu().add(town);  // Add available towns
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            filterSheltersByTown(item.getTitle().toString());
            return true;
        });
        popupMenu.show();
    }

    // Filter shelters based on selected town
    private void filterSheltersByTown(String selectedTown) {
        if (selectedTown.equals("All Towns")) {
            setupRecyclerView();  // Show all shelters
        } else {
            List<Shelter> filteredList = new ArrayList<>();
            for (Shelter shelter : shelterList) {
                if (shelter.getTown().equalsIgnoreCase(selectedTown)) {
                    filteredList.add(shelter);
                }
            }
            shelterAdapter = new ShelterAdapter(filteredList);
            shelterRecyclerView.setAdapter(shelterAdapter);
            shelterAdapter.notifyDataSetChanged();
        }
    }
}
