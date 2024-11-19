package DisasterMap;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import Weather.FloodType;
import Weather.PolygonData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisasterMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private WebView windyWebView;
    private TextView collapseCycloneTracing, collapseFloodMap;
    private LinearLayout cycloneTracingContent, floodMapContent;
    private ImageView cycloneArrow, floodArrow;
    private GoogleMap mMap;
    private Spinner floodTypeSpinner;
    private List<Polygon> currentPolygons = new ArrayList<>();  // Store the polygons currently shown on the map
    private List<PolygonData> allPolygons = new ArrayList<>();  // Store all polygons fetched from the server

    private TaskApi apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disaster_map);

        // Initialize API Service
        apiService = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        // Initialize views
        collapseCycloneTracing = findViewById(R.id.collapseCycloneTracing);
        cycloneTracingContent = findViewById(R.id.cycloneTracingContent);
        cycloneArrow = findViewById(R.id.cycloneArrow);
        collapseFloodMap = findViewById(R.id.collapseFloodMap);
        floodMapContent = findViewById(R.id.floodMapContent);
        floodArrow = findViewById(R.id.floodArrow);
        windyWebView = findViewById(R.id.windyWebView);
        floodTypeSpinner = findViewById(R.id.floodTypeSpinner);

        // Set up Windy WebView
        setupWindyWebView();

        // Set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Handle the back button click (back arrow)
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> onBackPressed());

        // Set up toggle for cyclone tracing section
        findViewById(R.id.collapseCycloneTracingHeader).setOnClickListener(v -> toggleVisibility(cycloneTracingContent, cycloneArrow));

        // Set up toggle for flood map section
        findViewById(R.id.collapseFloodMapHeader).setOnClickListener(v -> toggleVisibility(floodMapContent, floodArrow));

        // Initialize Google Maps Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Fetch flood types and polygons
        fetchFloodTypes();
        fetchSavedPolygons();

        // Spinner item selection listener
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
    }

    // Windy WebView setup
    private void setupWindyWebView() {
        windyWebView.getSettings().setJavaScriptEnabled(true);
        windyWebView.loadUrl("https://embed.windy.com/embed2.html?lat=-17.713&lon=178.065&detailLat=-17.713&detailLon=178.065&width=650&height=450&zoom=7&level=surface&overlay=wind&product=ecmwf&menu=&message=true&marker=&calendar=&pressure=true&wind=undefined&windGust=undefined&temp=undefined&dew=undefined&precip=undefined&type=map&location=coordinates&detail=true&metricWind=default&metricTemp=default&radarRange=-1");
    }

    // Toggle visibility of sections and adjust arrow direction
    private void toggleVisibility(LinearLayout sectionContent, ImageView arrowIcon) {
        if (sectionContent.getVisibility() == View.GONE) {
            sectionContent.setVisibility(View.VISIBLE);
            arrowIcon.setImageResource(R.drawable.ic_arrow_up);
        } else {
            sectionContent.setVisibility(View.GONE);
            arrowIcon.setImageResource(R.drawable.ic_arrow_down);
        }
    }

    // Google Maps setup
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Focus on Fiji
        LatLng fiji = new LatLng(-17.7134, 178.0650);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fiji, 7));
    }

    // Fetch flood types to populate the spinner
    private void fetchFloodTypes() {
        Call<List<FloodType>> call = apiService.getFloodTypes();
        call.enqueue(new Callback<List<FloodType>>() {
            @Override
            public void onResponse(Call<List<FloodType>> call, Response<List<FloodType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FloodType> floodTypes = response.body();
                    ArrayAdapter<FloodType> adapter = new ArrayAdapter<>(DisasterMapActivity.this,
                            android.R.layout.simple_spinner_item, floodTypes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    floodTypeSpinner.setAdapter(adapter);
                } else {
                    Toast.makeText(DisasterMapActivity.this, "Failed to load flood types", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FloodType>> call, Throwable t) {
                Toast.makeText(DisasterMapActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fetch saved polygons to display on the map
    private void fetchSavedPolygons() {
        Call<List<PolygonData>> call = apiService.getSavedPolygons();
        call.enqueue(new Callback<List<PolygonData>>() {
            @Override
            public void onResponse(Call<List<PolygonData>> call, Response<List<PolygonData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allPolygons = response.body();  // Store all polygons
                } else {
                    Toast.makeText(DisasterMapActivity.this, "Failed to load polygons", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PolygonData>> call, Throwable t) {
                Toast.makeText(DisasterMapActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Filter polygons by flood type
    private void filterPolygonsByFloodType(int floodTypeId) {
        clearAllPolygons();  // Clear existing polygons from the map
        for (PolygonData polygonData : allPolygons) {
            if (polygonData.getFloodTypeId() == floodTypeId) {
                drawSavedPolygon(polygonData);  // Draw only the polygons for the selected flood type
            }
        }
    }

    // Clear all polygons from the map
    private void clearAllPolygons() {
        for (Polygon polygon : currentPolygons) {
            polygon.remove();
        }
        currentPolygons.clear();
    }

    // Draw a saved polygon on the map
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

        currentPolygons.add(polygon);  // Add to the list of currently shown polygons
    }

    // Helper to parse coordinates JSON
    private List<LatLng> parseCoordinates(String coordinatesJson) {
        Gson gson = new Gson();
        List<LatLng> points = new ArrayList<>();
        List<Map<String, Double>> rawPoints = gson.fromJson(coordinatesJson, List.class);
        for (Map<String, Double> point : rawPoints) {
            LatLng latLng = new LatLng(point.get("latitude"), point.get("longitude"));
            points.add(latLng);
        }
        return points;
    }
}
