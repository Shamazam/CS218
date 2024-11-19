package AdminSendAlert;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendAlertActivity extends AppCompatActivity {

    private EditText alertTitle, messageInput;
    private RadioGroup optionsGroup;
    private RadioButton uploadImage, writeMessage;
    private Button btnSendAlert, btnDeleteAlert;
    private Spinner spinnerAlerts;
    private Uri selectedImageUri;
    private String selectedAlertId = null;

    private TaskApi taskApi;
    private List<Alert> alertList = new ArrayList<>();
    private List<String> alertTitles = new ArrayList<>();

    // Gallery launcher to select images
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Toast.makeText(SendAlertActivity.this, "Image selected: " + selectedImageUri, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SendAlertActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_alert);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Send/Delete Alert");
        }

        // Initialize UI elements
        alertTitle = findViewById(R.id.alertTitle);
        messageInput = findViewById(R.id.messageInput);
        optionsGroup = findViewById(R.id.optionsGroup);
        uploadImage = findViewById(R.id.uploadImage);
        writeMessage = findViewById(R.id.writeMessage);
        btnSendAlert = findViewById(R.id.btn_send_alert);
        btnDeleteAlert = findViewById(R.id.btn_delete_alert);
        spinnerAlerts = findViewById(R.id.spinner_alerts);

        messageInput.setVisibility(View.GONE); // Initially hide message input

        // Initialize Retrofit API
        taskApi = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        loadAlertsIntoSpinner();  // Load alerts into the spinner

        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.uploadImage) {
                messageInput.setVisibility(View.GONE);
                checkAndRequestPermissions(); // Request permissions to open gallery
            } else if (checkedId == R.id.writeMessage) {
                messageInput.setVisibility(View.VISIBLE);
            }
        });

        btnSendAlert.setOnClickListener(view -> {
            String title = alertTitle.getText().toString().trim();
            String message = messageInput.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(SendAlertActivity.this, "Title is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (uploadImage.isChecked() && selectedImageUri != null) {
                uploadToServer(title, selectedImageUri);
            } else if (writeMessage.isChecked() && !message.isEmpty()) {
                sendTextAlert(title, message);
            } else {
                Toast.makeText(this, "Please fill the required fields", Toast.LENGTH_SHORT).show();
            }
        });

        btnDeleteAlert.setOnClickListener(view -> {
            if (selectedAlertId != null) {
                deleteAlert(selectedAlertId);
            } else {
                Toast.makeText(SendAlertActivity.this, "No alert selected for deletion.", Toast.LENGTH_SHORT).show();
            }
        });

        spinnerAlerts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAlertId = alertList.get(position).getId();
                Log.d("SelectedAlert", "Selected alert ID: " + selectedAlertId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedAlertId = null;
            }
        });
    }

    private void loadAlertsIntoSpinner() {
        Call<List<Alert>> call = taskApi.getAlerts();
        call.enqueue(new Callback<List<Alert>>() {
            @Override
            public void onResponse(Call<List<Alert>> call, Response<List<Alert>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    alertList = response.body();
                    alertTitles.clear();

                    for (Alert alert : alertList) {
                        alertTitles.add(alert.getTitle());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(SendAlertActivity.this,
                            android.R.layout.simple_spinner_item, alertTitles);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerAlerts.setAdapter(adapter);
                } else {
                    Toast.makeText(SendAlertActivity.this, "Failed to load alerts", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Alert>> call, Throwable t) {
                Toast.makeText(SendAlertActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 100);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            } else {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void sendTextAlert(String title, String message) {
        RequestBody titlePart = RequestBody.create(title, MultipartBody.FORM);
        RequestBody descriptionPart = RequestBody.create(message, MultipartBody.FORM);

        Call<ResponseBody> call = taskApi.sendAlert(titlePart, descriptionPart, null);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SendAlertActivity.this, "Message alert sent successfully", Toast.LENGTH_SHORT).show();
                    loadAlertsIntoSpinner();
                } else {
                    Toast.makeText(SendAlertActivity.this, "Failed to send message", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SendAlertActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadToServer(String title, Uri imageUri) {
        try {
            String filePath = FileUtil.getPath(imageUri, this);
            File imageFile;

            if (filePath != null) {
                imageFile = new File(filePath);
            } else {
                imageFile = getFileFromUri(imageUri); // Use the method to create a temporary file
            }

            if (!imageFile.exists()) {
                Toast.makeText(this, "Error: File does not exist", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestBody titlePart = RequestBody.create(title, MultipartBody.FORM);
            RequestBody requestFile = RequestBody.create(imageFile, MediaType.parse("image/*"));
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

            Call<ResponseBody> call = taskApi.sendAlert(titlePart, null, imagePart);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(SendAlertActivity.this, "Image alert sent successfully", Toast.LENGTH_SHORT).show();
                        loadAlertsIntoSpinner();
                    } else {
                        Toast.makeText(SendAlertActivity.this, "Failed to send image alert", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(SendAlertActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error preparing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Convert the selected image URI into a File
    private File getFileFromUri(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = new File(getCacheDir(), "uploaded_image.jpg");

        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            inputStream.close();
        }
        return tempFile;
    }

    // Delete the selected alert from the server
    private void deleteAlert(String alertId) {
        Call<ResponseBody> call = taskApi.deleteAlert(alertId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SendAlertActivity.this, "Alert deleted successfully", Toast.LENGTH_SHORT).show();
                    loadAlertsIntoSpinner(); // Refresh spinner after deletion
                } else {
                    Toast.makeText(SendAlertActivity.this, "Failed to delete alert", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SendAlertActivity.this, "Delete API call failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Handle permission results for accessing storage
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Handle back arrow press on toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity and return to the previous screen
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
