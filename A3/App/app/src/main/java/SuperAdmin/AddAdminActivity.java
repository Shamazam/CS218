package SuperAdmin;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAdminActivity extends AppCompatActivity {

    private EditText firstNameField, lastNameField, usernameField, passwordField, confirmPasswordField;
    private Button submitButton;

    // Retrofit API instance
    private TaskApi adminApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin);

        // Initialize the fields
        firstNameField = findViewById(R.id.firstNameField);
        lastNameField = findViewById(R.id.lastNameField);
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);
        submitButton = findViewById(R.id.submitButton);

        // Initialize Retrofit API
        adminApi = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        // Set up back button functionality
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> finish());  // Finish the activity and go back

        // Set up submit button click listener
        submitButton.setOnClickListener(v -> {
            String firstName = firstNameField.getText().toString().trim();
            String lastName = lastNameField.getText().toString().trim();
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String confirmPassword = confirmPasswordField.getText().toString().trim();

            if (validateFields(firstName, lastName, username, password, confirmPassword)) {
                // Proceed with adding the admin (make API call)
                addAdmin(firstName, lastName, username, password);
            }
        });
    }

    // Validate fields before sending the request
    private boolean validateFields(String firstName, String lastName, String username, String password, String confirmPassword) {
        if (TextUtils.isEmpty(firstName)) {
            firstNameField.setError("First Name is required");
            return false;
        }

        if (TextUtils.isEmpty(lastName)) {
            lastNameField.setError("Last Name is required");
            return false;
        }

        if (TextUtils.isEmpty(username)) {
            usernameField.setError("Username is required");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Password is required");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordField.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    // Method to handle the API call for adding the admin
    private void addAdmin(String firstName, String lastName, String username, String password) {

        // Call the Retrofit API
        Call<AdminResponse> call = adminApi.addAdmin(firstName, lastName, username, password);
        call.enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                if (response.isSuccessful()) {
                    AdminResponse adminResponse = response.body();
                    if (adminResponse != null && "success".equals(adminResponse.getStatus())) {
                        Toast.makeText(AddAdminActivity.this, "Admin added successfully!", Toast.LENGTH_SHORT).show();
                        finish();  // Close the activity after success
                    } else {
                        Log.e("API Response", "Failed: " + (adminResponse != null ? adminResponse.getMessage() : "No response body"));
                        Toast.makeText(AddAdminActivity.this, adminResponse != null ? adminResponse.getMessage() : "Failed to add admin", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        Log.e("API Response", "Error response: " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(AddAdminActivity.this, "Failed to add admin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                Log.e("API Error", "Failed to make API call", t);
                Toast.makeText(AddAdminActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
