package ChangePassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import LoginRegister.Login;
import HomeScreen.HomeScreen;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText emailInput, currentPasswordInput, newPasswordInput, confirmPasswordInput;
    private Button submitButton;
    private ImageView backButton;
    private int userId;
    private String firstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Get the user ID from the intent passed from the previous activity
        userId = getIntent().getIntExtra("user_id", -1);
        firstName = getIntent().getStringExtra("first_name");// Get the user ID passed from HomeScreen

        // Initialize the views (ensure these IDs match the ones in your XML layout)
        emailInput = findViewById(R.id.emailInput);
        currentPasswordInput = findViewById(R.id.currentPasswordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        submitButton = findViewById(R.id.submitChangePassword);
        backButton = findViewById(R.id.backButton);

        // Back button functionality
        backButton.setOnClickListener(v -> goToHomeScreen()); // Go back to the home screen

        // Submit button functionality
        submitButton.setOnClickListener(v -> handleChangePassword());
    }

    private void handleChangePassword() {
        String email = emailInput.getText().toString().trim();
        String currentPassword = currentPasswordInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Please enter your email");
            return;
        }

        if (TextUtils.isEmpty(currentPassword)) {
            currentPasswordInput.setError("Please enter your current password");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            newPasswordInput.setError("Please enter a new password");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return;
        }

        // Call the API to change the password
        TaskApi apiInterface = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);
        Call<ResponseBody> call = apiInterface.changePassword(userId, email, currentPassword, newPassword);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                    logOutUser(); // Log out and go back to the login screen after password change
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logOutUser() {
        // Log the user out and return to the Login screen
        Intent intent = new Intent(ChangePasswordActivity.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the backstack
        startActivity(intent);
        finish(); // Close the ChangePasswordActivity
    }

    private void goToHomeScreen() {
        // Go back to the Home screen
        Intent intent = new Intent(ChangePasswordActivity.this, HomeScreen.class);
        intent.putExtra("user_id", userId); // Pass the user_id back to the HomeScreen
        intent.putExtra("first_name", firstName);
        startActivity(intent);
        finish(); // Close the ChangePasswordActivity
    }

    @Override
    public void onBackPressed() {
        // Override the back button to go to the home screen instead of finishing the activity
        super.onBackPressed();
        goToHomeScreen();
    }
}
