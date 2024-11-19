package SuperAdmin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAdminActivity extends AppCompatActivity {

    private Spinner adminSpinner, adminPasswordSpinner;
    private EditText firstNameField, lastNameField, usernameField;
    private EditText newPasswordField, confirmNewPasswordField;
    private Button submitEditButton, submitPasswordButton;

    private TaskApi adminApi;
    private List<Admin> adminDetails;  // Holds the details of admins

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_admin);

        // Initialize the fields
        adminSpinner = findViewById(R.id.adminSpinner);
        adminPasswordSpinner = findViewById(R.id.adminPasswordSpinner);
        firstNameField = findViewById(R.id.firstNameField);
        lastNameField = findViewById(R.id.lastNameField);
        usernameField = findViewById(R.id.usernameField);
        newPasswordField = findViewById(R.id.newPasswordField);
        confirmNewPasswordField = findViewById(R.id.confirmNewPasswordField);
        submitEditButton = findViewById(R.id.submitEditButton);
        submitPasswordButton = findViewById(R.id.submitPasswordButton);

        // Initialize Retrofit API
        adminApi = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        // Set up back button functionality
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> finish());  // Finish the activity and go back

        // Load Admins into the Spinners
        loadAdminsIntoSpinners();

        // Set listener for admin selection in the spinner to populate admin details
        adminSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadAdminDetails(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set up submit button for editing admin details
        submitEditButton.setOnClickListener(v -> {
            String firstName = firstNameField.getText().toString().trim();
            String lastName = lastNameField.getText().toString().trim();
            String username = usernameField.getText().toString().trim();

            if (validateEditFields(firstName, lastName, username)) {
                editAdminDetails(firstName, lastName, username);
            }
        });

        // Set up submit button for changing admin password
        submitPasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordField.getText().toString().trim();
            String confirmPassword = confirmNewPasswordField.getText().toString().trim();

            if (validatePasswordFields(newPassword, confirmPassword)) {
                changeAdminPassword(newPassword);
            }
        });
    }

    // Load Admins from the API and populate the spinners
    private void loadAdminsIntoSpinners() {
        Call<List<Admin>> call = adminApi.getAdmins();  // Assuming the API returns a list of admins
        call.enqueue(new Callback<List<Admin>>() {
            @Override
            public void onResponse(Call<List<Admin>> call, Response<List<Admin>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adminDetails = response.body();
                    List<String> adminUsernames = extractUsernames(adminDetails);
                    populateSpinners(adminUsernames);
                } else {
                    Toast.makeText(EditAdminActivity.this, "Failed to load admins", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Admin>> call, Throwable t) {
                Toast.makeText(EditAdminActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Populate both spinners with admin usernames
    private void populateSpinners(List<String> usernames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, usernames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adminSpinner.setAdapter(adapter);
        adminPasswordSpinner.setAdapter(adapter);
    }

    // Extract usernames from the admin details
    private List<String> extractUsernames(List<Admin> admins) {
        List<String> usernames = new ArrayList<>();
        for (Admin admin : admins) {
            usernames.add(admin.getUsername());
        }
        return usernames;
    }

    // Load the selected admin's details into the fields
    private void loadAdminDetails(int position) {
        Admin selectedAdmin = adminDetails.get(position);
        firstNameField.setText(selectedAdmin.getFirstName());
        lastNameField.setText(selectedAdmin.getLastName());
        usernameField.setText(selectedAdmin.getUsername());
    }

    // Validate the edit fields
    private boolean validateEditFields(String firstName, String lastName, String username) {
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

        return true;
    }

    // Validate the password fields
    private boolean validatePasswordFields(String newPassword, String confirmPassword) {
        if (TextUtils.isEmpty(newPassword)) {
            newPasswordField.setError("New password is required");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmNewPasswordField.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    // Method to handle editing admin details
    private void editAdminDetails(String firstName, String lastName, String username) {
        Admin selectedAdmin = adminDetails.get(adminSpinner.getSelectedItemPosition());
        Call<AdminResponse> call = adminApi.editAdminDetails(selectedAdmin.getAdminId(), firstName, lastName, username);

        call.enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditAdminActivity.this, "Admin details updated successfully!", Toast.LENGTH_SHORT).show();
                    // Reload the spinner data to reflect changes
                    loadAdminsIntoSpinners();
                } else {
                    Toast.makeText(EditAdminActivity.this, "Failed to update admin details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                Toast.makeText(EditAdminActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to handle changing admin password
    private void changeAdminPassword(String newPassword) {
        Admin selectedAdmin = adminDetails.get(adminPasswordSpinner.getSelectedItemPosition());
        Call<AdminResponse> call = adminApi.changeAdminPassword(selectedAdmin.getAdminId(), newPassword);

        call.enqueue(new Callback<AdminResponse>() {
            @Override
            public void onResponse(Call<AdminResponse> call, Response<AdminResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditAdminActivity.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                    // Reload the spinner data to reflect changes
                    loadAdminsIntoSpinners();
                } else {
                    Toast.makeText(EditAdminActivity.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminResponse> call, Throwable t) {
                Toast.makeText(EditAdminActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
