package Weather;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import AdminHomeScreen.AdminDashBoardActivity;
import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Polygon drawnPolygon;
    private List<LatLng> coordinates = new ArrayList<>();
    private Spinner floodTypeSpinner, currentFloodTypeSpinner;
    private Button drawPolygonButton, clearPolygonButton, savePolygonButton, submitFloodTypeButton, deleteFloodTypeButton;
    private EditText floodTypeInput;

    private TaskApi apiService;  // Retrofit instance
    private Map<Polygon, Integer> polygonIdMap = new HashMap<>();  // To map polygons to their database IDs
    private List<Polygon> currentPolygons = new ArrayList<>();     // Store the polygons currently shown on the map

    private List<PolygonData> allPolygons = new ArrayList<>();  // Store all polygons fetched from the server
    private List<FloodType> floodTypes = new ArrayList<>();  // Store all flood types fetched from the server

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_map);

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        // Initialize views
        floodTypeSpinner = findViewById(R.id.floodTypeSpinner);
        currentFloodTypeSpinner = findViewById(R.id.currentFloodTypeSpinner);
        drawPolygonButton = findViewById(R.id.drawPolygonButton);
        clearPolygonButton = findViewById(R.id.clearPolygonButton);
        savePolygonButton = findViewById(R.id.savePolygonButton);
        floodTypeInput = findViewById(R.id.floodTypeInput);
        submitFloodTypeButton = findViewById(R.id.submitFloodTypeButton);
        deleteFloodTypeButton = findViewById(R.id.deleteFloodTypeButton);

        // Initialize Retrofit API service
        apiService = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        // Fetch flood types from the server
        fetchFloodTypes();

        // Fetch saved polygons
        fetchSavedPolygons();

        // Set up button listeners
        drawPolygonButton.setOnClickListener(v -> drawPolygon());
        clearPolygonButton.setOnClickListener(v -> clearPolygon());
        savePolygonButton.setOnClickListener(v -> savePolygon());

        // Add listener to submit new flood type
        submitFloodTypeButton.setOnClickListener(v -> submitNewFloodType());

        // Add listener to delete the selected flood type
        deleteFloodTypeButton.setOnClickListener(v -> deleteSelectedFloodType());

        // Add listener to spinner to filter polygons based on selected flood type
        floodTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FloodType selectedFloodType = (FloodType) floodTypeSpinner.getSelectedItem();
                if (selectedFloodType != null) {
                    filterPolygonsByFloodType(selectedFloodType.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Add listener to current flood type spinner
        currentFloodTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Display or use the selected flood type
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Custom back button functionality
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            // Finish the current activity and go back to the previous one
            finish();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Focus on Fiji
        LatLng fiji = new LatLng(-17.7134, 178.0650);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fiji, 7));

        // Add click listener for drawing a polygon
        mMap.setOnMapClickListener(latLng -> {
            coordinates.add(latLng);
            if (drawnPolygon != null) {
                drawnPolygon.setPoints(coordinates);
            } else {
                PolygonOptions polygonOptions = new PolygonOptions().addAll(coordinates).clickable(true);
                drawnPolygon = mMap.addPolygon(polygonOptions);
            }
        });

        // Handle polygon deletion
        mMap.setOnPolygonClickListener(polygon -> {
            Integer polygonId = polygonIdMap.get(polygon);

            if (polygonId != null) {
                new AlertDialog.Builder(AdminMapActivity.this)
                        .setTitle("Delete Polygon")
                        .setMessage("Are you sure you want to delete this polygon?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            polygon.remove();
                            Log.d("AdminMapActivity", "Deleting polygon with ID: " + polygonId);
                            deletePolygonFromDatabase(polygonId);  // Pass the correct polygon ID for deletion
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                Log.e("AdminMapActivity", "Invalid Polygon ID: " + polygon.getId());
                Toast.makeText(AdminMapActivity.this, "Invalid Polygon ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawPolygon() {
        coordinates.clear();
        if (drawnPolygon != null) {
            drawnPolygon.remove();
        }
        Toast.makeText(this, "Start drawing on the map", Toast.LENGTH_SHORT).show();
    }

    private void clearPolygon() {
        coordinates.clear();
        if (drawnPolygon != null) {
            drawnPolygon.remove();
            drawnPolygon = null;
        }
        Toast.makeText(this, "Polygon cleared", Toast.LENGTH_SHORT).show();
    }

    private void fetchFloodTypes() {
        // Call API to fetch flood types and set them in both spinners
        Call<List<FloodType>> call = apiService.getFloodTypes();
        call.enqueue(new Callback<List<FloodType>>() {
            @Override
            public void onResponse(Call<List<FloodType>> call, Response<List<FloodType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    floodTypes = response.body();
                    ArrayAdapter<FloodType> adapter = new ArrayAdapter<>(AdminMapActivity.this,
                            android.R.layout.simple_spinner_item, floodTypes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    floodTypeSpinner.setAdapter(adapter);
                    currentFloodTypeSpinner.setAdapter(adapter);  // Set data in the current flood type spinner
                } else {
                    Toast.makeText(AdminMapActivity.this, "Failed to load flood types", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FloodType>> call, Throwable t) {
                Toast.makeText(AdminMapActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSavedPolygons() {
        // Fetch polygons from the server
        Call<List<PolygonData>> call = apiService.getSavedPolygons();
        call.enqueue(new Callback<List<PolygonData>>() {
            @Override
            public void onResponse(Call<List<PolygonData>> call, Response<List<PolygonData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allPolygons = response.body();  // Store all polygons
                    // Initially show polygons for the default flood type
                    FloodType selectedFloodType = (FloodType) floodTypeSpinner.getSelectedItem();
                    if (selectedFloodType != null) {
                        filterPolygonsByFloodType(selectedFloodType.getId());
                    }
                } else {
                    Toast.makeText(AdminMapActivity.this, "Failed to load polygons", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PolygonData>> call, Throwable t) {
                Toast.makeText(AdminMapActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to filter and display polygons by the selected flood type
    private void filterPolygonsByFloodType(int floodTypeId) {
        clearAllPolygons();  // Clear existing polygons from the map
        for (PolygonData polygonData : allPolygons) {
            if (polygonData.getFloodTypeId() == floodTypeId) {
                drawSavedPolygon(polygonData);  // Draw only the polygons for the selected flood type
            }
        }
    }

    // Method to clear all polygons from the map
    private void clearAllPolygons() {
        for (Polygon polygon : currentPolygons) {
            polygon.remove();  // Remove each polygon from the map
        }
        currentPolygons.clear();  // Clear the list to ensure no stale polygons remain
    }

    private void drawSavedPolygon(PolygonData polygonData) {
        List<LatLng> points = parseCoordinates(polygonData.getCoordinates());

        // Use the color from the response
        String colorString = polygonData.getColor();
        int color;
        try {
            color = Color.parseColor(colorString);
        } catch (IllegalArgumentException e) {
            color = Color.GRAY;  // Default color if parsing fails
        }

        Polygon polygon = mMap.addPolygon(new PolygonOptions()
                .addAll(points)
                .strokeColor(color)
                .fillColor(color)
                .clickable(true)
        );

        // Store the polygon and its ID
        polygonIdMap.put(polygon, polygonData.getId());
        currentPolygons.add(polygon);  // Add to the list of currently shown polygons
    }

    private List<LatLng> parseCoordinates(String coordinatesJson) {
        // Parse coordinates JSON into List<LatLng>
        Gson gson = new Gson();
        List<LatLng> points = new ArrayList<>();
        List<Map<String, Double>> rawPoints = gson.fromJson(coordinatesJson, List.class);
        for (Map<String, Double> point : rawPoints) {
            LatLng latLng = new LatLng(point.get("latitude"), point.get("longitude"));
            points.add(latLng);
        }
        return points;
    }

    private void savePolygon() {
        if (coordinates.isEmpty()) {
            Toast.makeText(this, "No polygon drawn", Toast.LENGTH_SHORT).show();
            return;
        }

        String polygonName = "Polygon " + System.currentTimeMillis(); // Example polygon name
        FloodType selectedFloodType = (FloodType) floodTypeSpinner.getSelectedItem();

        // Prepare the polygon coordinates for submission
        String coordinatesJson = new Gson().toJson(coordinates);  // Convert list to JSON string

        // Convert floodType ID to String before passing
        String floodTypeId = String.valueOf(selectedFloodType.getId());

        // Dynamically generate a color for the polygon
        String polygonColor = getRandomColor();

        // Call API to save polygon
        Call<ResponseBody> call = apiService.savePolygon(polygonName, coordinatesJson, Integer.parseInt(floodTypeId), polygonColor);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        Toast.makeText(AdminMapActivity.this, responseBody, Toast.LENGTH_SHORT).show();

                        // Clear the polygon from the drawing view
                        clearPolygon();

                        // Fetch polygons again to ensure the newly saved polygon is included
                        fetchSavedPolygons();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(AdminMapActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorResponse = response.errorBody().string();
                        Log.e("AdminMapActivity", "Error response: " + errorResponse);
                        Toast.makeText(AdminMapActivity.this, "Failed to save polygon. Error: " + errorResponse, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(AdminMapActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AdminMapActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitNewFloodType() {
        String floodTypeName = floodTypeInput.getText().toString().trim();
        if (floodTypeName.isEmpty()) {
            Toast.makeText(this, "Please enter a flood type", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call API to add new flood type
        Call<ResponseBody> call = apiService.addFloodType(floodTypeName);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminMapActivity.this, "New flood type added successfully", Toast.LENGTH_SHORT).show();
                    fetchFloodTypes();  // Refresh flood types in the spinner
                    floodTypeInput.setText("");  // Clear the input field
                } else {
                    Toast.makeText(AdminMapActivity.this, "Failed to add flood type", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AdminMapActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getRandomColor() {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return String.format("#%02x%02x%02x", red, green, blue);  // Return a hex color string
    }

    private void deletePolygonFromDatabase(int polygonId) {
        Log.d("AdminMapActivity", "Deleting polygon with ID: " + polygonId);  // Log the ID being sent

        Call<ResponseBody> call = apiService.deletePolygon(polygonId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        Log.d("AdminMapActivity", "Delete response: " + responseBody);
                        Toast.makeText(AdminMapActivity.this, "Polygon deleted successfully", Toast.LENGTH_SHORT).show();
                        fetchSavedPolygons();  // Reload polygons after deletion
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(AdminMapActivity.this, "Error reading server response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorResponse = response.errorBody().string();
                        Log.e("AdminMapActivity", "Failed response: " + errorResponse);
                        Toast.makeText(AdminMapActivity.this, "Failed to delete polygon from server", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(AdminMapActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("AdminMapActivity", "Failed to delete polygon: " + t.getMessage());
                Toast.makeText(AdminMapActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteSelectedFloodType() {
        FloodType selectedFloodType = (FloodType) currentFloodTypeSpinner.getSelectedItem();
        if (selectedFloodType == null) {
            Toast.makeText(this, "No flood type selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Correcting the Retrofit Call
        Call<Void> call = apiService.deleteFloodType(selectedFloodType.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Successful deletion
                    Toast.makeText(AdminMapActivity.this, "Flood type deleted successfully", Toast.LENGTH_SHORT).show();
                    fetchFloodTypes();  // Refresh the flood types after deletion
                } else {
                    // Handling any non-successful response
                    Toast.makeText(AdminMapActivity.this, "Failed to delete flood type", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle any failure
                Toast.makeText(AdminMapActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Overriding onBackPressed to go to AdminDashBoardActivity
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AdminMapActivity.this, AdminDashBoardActivity.class);
        startActivity(intent);
        finish();  // Optional: to prevent returning to this activity when pressing back again
    }
}
