package LoginRegister;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import readyfiji.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    EditText firstName, lastName, emailAddress, password, confirmPassword;
    Button registerButton;
    TextView loginLink;
    String deviceToken;  // To store the Android ID as the device token

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register); // Ensure this matches your layout file name

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        emailAddress = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);

        // Retrieve the Android ID as the device token
        deviceToken = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("Android ID", "Device Token: " + deviceToken);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Set onClick listener for the login link to navigate to the Login activity
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the Login Activity
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish(); // Optional: Finish Register activity if you don't want the user to come back here by pressing back
            }
        });
    }

    private void registerUser() {
        String firstNameInput = firstName.getText().toString().trim();
        String lastNameInput = lastName.getText().toString().trim();
        String emailInput = emailAddress.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();
        String confirmPasswordInput = confirmPassword.getText().toString().trim();

        // Check if all fields are filled
        if (TextUtils.isEmpty(firstNameInput) || TextUtils.isEmpty(lastNameInput)
                || TextUtils.isEmpty(emailInput) || TextUtils.isEmpty(passwordInput)
                || TextUtils.isEmpty(confirmPasswordInput)) {
            Toast.makeText(Register.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Email format validation
        if (!isValidEmail(emailInput)) {
            Toast.makeText(Register.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Password and Confirm Password match validation
        if (!passwordInput.equals(confirmPasswordInput)) {
            Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check password length (optional)
        if (passwordInput.length() < 6) {
            Toast.makeText(Register.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use Retrofit to register the user with the device token (Android ID)
        TaskApi apiInterface = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);
        Call<ResponseBody> call = apiInterface.registerUser(firstNameInput, lastNameInput, emailInput, passwordInput, deviceToken); // Include device token

        // Handle the API response
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Parse the response body
                        String responseBody = response.body().string();
                        Log.d("ServerResponse", "Response Body: " + responseBody); // Logging the server response for debugging

                        // Parse the JSON response to check if the operation was successful
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equals("success")) {
                            // Registration was successful
                            Toast.makeText(Register.this, "User registered successfully", Toast.LENGTH_SHORT).show();

                            // Redirect to the Login screen after successful registration
                            Intent intent = new Intent(Register.this, Login.class);
                            startActivity(intent);
                            finish(); // Optional: Finish the Register activity
                        } else if (status.equals("error")) {
                            // Registration failed, e.g., user is already registered
                            Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Register.this, "Error parsing server response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // The request failed (not in the 200–299 range)
                    Toast.makeText(Register.this, "Request failed with status code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API Error", "Error: " + t.getMessage()); // Log the error for debugging
                Toast.makeText(Register.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to check if email is valid
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
