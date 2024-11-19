package AdminAddShelter;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminShelterCapacityActivity extends AppCompatActivity {

    private Spinner shelterSpinner;
    private TextView currentCapacityTextView;
    private EditText newCapacityEditText;
    private Button submitCapacityButton;
    private ImageView backArrow; // Reference for back button

    private TaskApi apiService;
    private List<AdminShelter> shelterList = new ArrayList<>();
    private int selectedShelterId = -1;
    private int selectedShelterCapacity = 0;  // This will hold the max capacity of the selected shelter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_shelter_capacity);

        // Initialize UI elements
        backArrow = findViewById(R.id.backArrow); // Find the back arrow view
        shelterSpinner = findViewById(R.id.shelterSpinner);
        currentCapacityTextView = findViewById(R.id.currentCapacityField);
        newCapacityEditText = findViewById(R.id.newCapacityField);
        submitCapacityButton = findViewById(R.id.submitCapacityBtn);

        // Initialize API service
        apiService = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        // Set up back arrow click listener
        backArrow.setOnClickListener(v -> onBackPressed()); // Go back when back arrow is pressed

        // Load shelters for the spinner
        loadShelters();

        // Spinner item selection handler
        shelterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AdminShelter selectedShelter = shelterList.get(position);
                selectedShelterId = selectedShelter.getId();
                selectedShelterCapacity = selectedShelter.getCapacity();  // Store the maximum capacity
                String currentCapacity = String.valueOf(selectedShelter.getCurrentCapacity());
                currentCapacityTextView.setText(currentCapacity);  // Show current capacity
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedShelterId = -1;
                selectedShelterCapacity = 0;
            }
        });

        // Submit button click handler
        submitCapacityButton.setOnClickListener(v -> {
            String newCapacityStr = newCapacityEditText.getText().toString().trim();
            if (selectedShelterId != -1 && !newCapacityStr.isEmpty()) {
                int newCapacity = Integer.parseInt(newCapacityStr);

                // Check if new capacity exceeds the maximum capacity
                if (newCapacity <= selectedShelterCapacity) {
                    updateShelterCapacity(selectedShelterId, newCapacity);
                } else {
                    Toast.makeText(AdminShelterCapacityActivity.this, "New capacity exceeds maximum shelter capacity", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AdminShelterCapacityActivity.this, "Please select a shelter and enter the new capacity", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load shelters for spinner
    private void loadShelters() {
        Call<List<AdminShelter>> call = apiService.getadminShelters();
        call.enqueue(new Callback<List<AdminShelter>>() {
            @Override
            public void onResponse(Call<List<AdminShelter>> call, Response<List<AdminShelter>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    shelterList = response.body();
                    ArrayAdapter<AdminShelter> adapter = new ArrayAdapter<>(AdminShelterCapacityActivity.this,
                            android.R.layout.simple_spinner_item, shelterList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    shelterSpinner.setAdapter(adapter);
                } else {
                    Toast.makeText(AdminShelterCapacityActivity.this, "Failed to load shelters", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AdminShelter>> call, Throwable t) {
                Toast.makeText(AdminShelterCapacityActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Update shelter capacity
    private void updateShelterCapacity(int shelterId, int newCapacity) {
        Call<ResponseBody> call = apiService.updateShelterCapacity(shelterId, String.valueOf(newCapacity));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminShelterCapacityActivity.this, "Capacity updated successfully", Toast.LENGTH_SHORT).show();
                    newCapacityEditText.setText(""); // Clear input field

                    // Update current capacity on the screen
                    currentCapacityTextView.setText(String.valueOf(newCapacity));
                } else {
                    Toast.makeText(AdminShelterCapacityActivity.this, "Failed to update capacity", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AdminShelterCapacityActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
