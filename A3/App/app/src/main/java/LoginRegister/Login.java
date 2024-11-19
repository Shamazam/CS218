package LoginRegister;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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

import ForgotPassword.ForgotPasswordActivity;
import HomeScreen.HomeScreen;
import AdminHomeScreen.AdminDashBoardActivity;
import SuperAdmin.SuperAdminDashboardActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginButton;
    TextView forgotPassword, registerHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        DeveloperModeCheck.preventAppIfDeveloperModeEnabled(this);

        // Find the views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        registerHere = findViewById(R.id.registerHere);

        // Handle login button click
        loginButton.setOnClickListener(v -> loginUser());

        // Handle "Forgot password?" click
        forgotPassword.setOnClickListener(v -> {
            Toast.makeText(Login.this, "Forgot password clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Login.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Handle "Register here" click
        registerHere.setOnClickListener(v -> {
            // Navigate to the Registration screen
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String emailOrUsername = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(emailOrUsername)) {
            emailInput.setError("Please enter your email or username");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Please enter your password");
            return;
        }

        // We no longer force only valid emails for admins who use usernames
        boolean isEmail = Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches();

        // Make the Retrofit call to check credentials
        TaskApi apiInterface = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);
        Call<ResponseBody> call = apiInterface.loginUser(emailOrUsername, password);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Parse the response body
                        String responseBody = response.body().string();
                        Log.d("ServerResponse", "Response Body: " + responseBody);

                        JSONObject jsonObject = new JSONObject(responseBody);
                        String status = jsonObject.getString("status");

                        if (status.equals("success")) {
                            // Extract user/admin details and role from the response
                            JSONObject userData = jsonObject.getJSONObject("data");
                            String role = jsonObject.getString("role");
                            String firstName = userData.getString("first_name");
                            int id;

                            if (role.equals("superadmin")) {
                                // Handle SuperAdmin login
                                id = userData.getInt("admin_id"); // assuming superadmin's primary key is also admin_id
                                Intent intent = new Intent(Login.this, SuperAdminDashboardActivity.class);
                                intent.putExtra("first_name", firstName);
                                intent.putExtra("admin_id", id);
                                startActivity(intent);
                            } else if (role.equals("admin")) {
                                // Handle Admin login
                                id = userData.getInt("admin_id");
                                Intent intent = new Intent(Login.this, AdminDashBoardActivity.class);
                                intent.putExtra("first_name", firstName);
                                intent.putExtra("admin_id", id);
                                startActivity(intent);
                            } else {
                                // Handle User login
                                id = userData.getInt("user_id");
                                Intent intent = new Intent(Login.this, HomeScreen.class);
                                intent.putExtra("first_name", firstName);
                                intent.putExtra("user_id", id);
                                startActivity(intent);
                            }

                            finish(); // Close the LoginActivity

                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Login.this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "Login failed with status code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API Error", "Error: " + t.getMessage());
                Toast.makeText(Login.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
