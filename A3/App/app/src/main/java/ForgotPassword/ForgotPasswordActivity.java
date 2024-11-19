package ForgotPassword;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private LinearLayout containerEmail, containerCode, containerPassword;
    private EditText inputEmail, inputCode, inputNewPassword, inputConfirmPassword;
    private Button btnSubmitEmail, btnSubmitCode, btnSubmitPassword;
    private TaskApi apiService; // Retrofit API service instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Forgot Password");
        }

        // Handle back button in the toolbar
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize containers and form fields
        containerEmail = findViewById(R.id.container_email);
        containerCode = findViewById(R.id.container_code);
        containerPassword = findViewById(R.id.container_password);

        inputEmail = findViewById(R.id.input_email);
        inputCode = findViewById(R.id.input_code);
        inputNewPassword = findViewById(R.id.input_new_password);
        inputConfirmPassword = findViewById(R.id.input_confirm_password);

        btnSubmitEmail = findViewById(R.id.btn_submit_email);
        btnSubmitCode = findViewById(R.id.btn_submit_code);
        btnSubmitPassword = findViewById(R.id.btn_submit_password);

        // Initialize Retrofit API service
        apiService = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        // Set initial states - email container active, others disabled
        enableContainer(containerEmail, true);
        enableContainer(containerCode, false);
        enableContainer(containerPassword, false);

        // Handle email submission
        btnSubmitEmail.setOnClickListener(v -> {
            String email = inputEmail.getText().toString();
            if (!email.isEmpty()) {
                // Make API call to request password reset code
                requestPasswordReset(email);
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle code submission
        btnSubmitCode.setOnClickListener(v -> {
            String codeStr = inputCode.getText().toString();
            if (!codeStr.isEmpty()) {
                try {
                    int code = Integer.parseInt(codeStr);
                    // Make API call to verify code
                    verifyCode(inputEmail.getText().toString(), code);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid code format", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter the 6-digit code", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle password reset
        btnSubmitPassword.setOnClickListener(v -> {
            String newPassword = inputNewPassword.getText().toString();
            String confirmPassword = inputConfirmPassword.getText().toString();

            if (!newPassword.isEmpty() && !confirmPassword.isEmpty()) {
                if (newPassword.equals(confirmPassword)) {
                    // Make API call to reset password
                    resetPassword(inputEmail.getText().toString(), newPassword);
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter and confirm your new password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // API call to request password reset code
    private void requestPasswordReset(String email) {
        apiService.requestPasswordReset(email).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Code sent to your email", Toast.LENGTH_SHORT).show();
                    // Disable email container and enable code container
                    enableContainer(containerEmail, false);
                    enableContainer(containerCode, true);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Error: Unable to send reset code", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // API call to verify code
    private void verifyCode(String email, int code) {
        apiService.verifyCode(email, code).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Code verified successfully", Toast.LENGTH_SHORT).show();
                    // Disable code container and enable password container
                    enableContainer(containerCode, false);
                    enableContainer(containerPassword, true);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Invalid code", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // API call to reset password
    private void resetPassword(String email, String newPassword) {
        apiService.resetPassword(email, newPassword).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after resetting the password
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Error: Unable to reset password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Enable or disable a container (makes input fields grayed out and disabled)
    private void enableContainer(LinearLayout container, boolean enabled) {
        container.setAlpha(enabled ? 1f : 0.5f); // Grayed out if disabled
        setViewAndChildrenEnabled(container, enabled); // Enable/disable all children views
    }

    // Recursive function to enable/disable all views within a container
    private void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled); // Enable/disable the view
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled); // Recursive call to child views
            }
        }
    }
}
