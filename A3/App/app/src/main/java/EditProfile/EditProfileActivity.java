package EditProfile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.io.File;

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private EditText usernameInput, firstNameInput, lastNameInput, phoneNumberInput, streetAddressInput;
    private Button saveButton, uploadPhotoButton;
    private ImageView profileImage;
    private Uri selectedImageUri;

    private TaskApi taskApi; // Retrofit API interface

    private String userId = "";

    // Launcher for opening the gallery to select a profile photo
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    profileImage.setImageURI(selectedImageUri); // Set the selected image to the ImageView
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Profile");
        }

        // Handle back press with toolbar navigation
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        userId = String.valueOf(intent.getIntExtra("user_id", -1));

        // Initializing views
        profileImage = findViewById(R.id.profileImage);
        uploadPhotoButton = findViewById(R.id.uploadPhotoButton);
        usernameInput = findViewById(R.id.usernameInput);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        streetAddressInput = findViewById(R.id.streetAddressInput);
        saveButton = findViewById(R.id.saveButton);

        // Initialize Retrofit API interface
        taskApi = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        // Fetch user data to populate fields
        fetchInitialUserData(userId);

        // Handling the photo upload
        uploadPhotoButton.setOnClickListener(view -> {
            if (checkGalleryPermission()) {
                openGallery();
            }
        });

        // Implement the save button functionality
        saveButton.setOnClickListener(v -> {
            // Save profile information when save button is clicked
            saveProfile();
        });
    }

    private void fetchInitialUserData(String userId) {
        taskApi.getUserProfile(userId).enqueue(new Callback<UserProfileWrapper>() {
            @Override
            public void onResponse(Call<UserProfileWrapper> call, Response<UserProfileWrapper> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Get the `data` object from the response
                    UserProfileResponse profile = response.body().getData();

                    // Log the raw response
                    Log.d("API Data", "First Name: " + profile.getFirstName() + ", Last Name: " + profile.getLastName());

                    // Populate the fields
                    firstNameInput.setText(profile.getFirstName());
                    lastNameInput.setText(profile.getLastName());

                    if (profile.getUsername() != null && !profile.getUsername().isEmpty()) {
                        usernameInput.setText(profile.getUsername());
                    }
                    if (profile.getPhoneNumber() != null && !profile.getPhoneNumber().isEmpty()) {
                        phoneNumberInput.setText(profile.getPhoneNumber());
                    }
                    if (profile.getStreetAddress() != null && !profile.getStreetAddress().isEmpty()) {
                        streetAddressInput.setText(profile.getStreetAddress());
                    }

                    // Load profile image or default user icon
                    if (profile.getProfileImage() != null && !profile.getProfileImage().isEmpty()) {
                        // If the profile image exists, load it using Glide
                        Glide.with(EditProfileActivity.this)
                                .load(profile.getProfileImage())
                                .circleCrop()  // This makes the image circular
                                .into(profileImage);
                    } else {
                        // If no image is provided, set the default profile icon
                        profileImage.setImageResource(R.drawable.ic_profile);
                    }

                } else {
                    Log.d("API Response Error", response.message());
                    Toast.makeText(EditProfileActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileWrapper> call, Throwable t) {
                Log.d("API Failure", t.getMessage());
                Toast.makeText(EditProfileActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Open the gallery for profile picture selection
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    // Check if the user has granted permission to access the gallery
    private boolean checkGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 2000);
                return false;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);
                return false;
            }
        }
        return true;
    }

    // Save the profile information including the profile image
    private void saveProfile() {
        // Gather inputs from the user interface
        String username = usernameInput.getText().toString().trim();
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        String streetAddress = streetAddressInput.getText().toString().trim();

        // Convert the selected image Uri to a file path
        MultipartBody.Part imagePart = null;
        if (selectedImageUri != null) {
            String imagePath = FileUtil.getPath(selectedImageUri, this);
            if (imagePath != null) {
                File imageFile = new File(imagePath);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
                imagePart = MultipartBody.Part.createFormData("profile_image", imageFile.getName(), requestFile);
            }
        }

        // Prepare the other fields as RequestBody for Retrofit
        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), userId);
        RequestBody firstNameBody = RequestBody.create(MediaType.parse("text/plain"), firstName);
        RequestBody lastNameBody = RequestBody.create(MediaType.parse("text/plain"), lastName);
        RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), username);
        RequestBody phoneNumberBody = RequestBody.create(MediaType.parse("text/plain"), phoneNumber);
        RequestBody streetAddressBody = RequestBody.create(MediaType.parse("text/plain"), streetAddress);

        // Send null for location type and value
        RequestBody locationTypeBody = null;
        RequestBody locationValueIdBody = null;

        // Make the API call to save the profile with image
        taskApi.updateUserProfileWithImage(
                userIdBody, firstNameBody, lastNameBody, usernameBody, phoneNumberBody, streetAddressBody,
                locationTypeBody, locationValueIdBody, imagePart
        ).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("Save API Response", "Profile updated successfully. Response: " + response.body().toString());
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("Save API Error", "Error response: " + errorBody);
                    } catch (Exception e) {
                        Log.e("Save API Error", "Error parsing error body", e);
                    }
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("Save API Failure", "Error calling API: " + t.getMessage(), t);
                Toast.makeText(EditProfileActivity.this, "Error updating profile: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
