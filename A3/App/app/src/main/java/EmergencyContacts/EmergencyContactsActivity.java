package EmergencyContacts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmergencyContactsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LinearLayout departmentsLayout;
    private TaskApi taskApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        // Set up the toolbar with back and filter buttons
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());  // Close the activity when back is clicked


        // Set up Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize Retrofit API
        taskApi = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        // Set up collapsible departments and contacts
        departmentsLayout = findViewById(R.id.departmentsLayout);

        // Fetch departments and display them
        fetchDepartments();
    }

    // Fetch department data from API
    private void fetchDepartments() {
        Call<DepartmentResponse> call = taskApi.getDepartments();
        call.enqueue(new Callback<DepartmentResponse>() {
            @Override
            public void onResponse(Call<DepartmentResponse> call, Response<DepartmentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Department> departments = response.body().getData();
                    if (departments == null || departments.isEmpty()) {
                        Toast.makeText(EmergencyContactsActivity.this, "No departments found", Toast.LENGTH_SHORT).show();
                    } else {
                        setupDepartments(departments);
                    }
                } else {
                    Toast.makeText(EmergencyContactsActivity.this, "Failed to load departments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DepartmentResponse> call, Throwable t) {
                Toast.makeText(EmergencyContactsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Set up department collapsibles and fetch contacts on click
    private void setupDepartments(List<Department> departments) {
        for (Department department : departments) {
            View departmentView = getLayoutInflater().inflate(R.layout.department_collapsible, departmentsLayout, false);

            TextView departmentName = departmentView.findViewById(R.id.departmentName);
            ImageView arrowIcon = departmentView.findViewById(R.id.arrowIcon); // Arrow for expand/collapse
            LinearLayout contactsContainer = departmentView.findViewById(R.id.contactsContainer);

            departmentName.setText(department.getDepartmentName());
            contactsContainer.setVisibility(View.GONE); // Initially collapsed

            // Create a common method for handling expand/collapse
            View.OnClickListener toggleExpandCollapse = v -> {
                if (contactsContainer.getVisibility() == View.GONE) {
                    contactsContainer.setVisibility(View.VISIBLE);
                    arrowIcon.setImageResource(R.drawable.ic_arrow_up); // Change arrow to up when expanded
                    fetchContactsForDepartment(department.getDepartmentId(), contactsContainer);
                } else {
                    contactsContainer.setVisibility(View.GONE);
                    arrowIcon.setImageResource(R.drawable.ic_arrow_down); // Change arrow to down when collapsed
                }
            };

            // Set the same listener for both the name and the arrow
            departmentName.setOnClickListener(toggleExpandCollapse);
            arrowIcon.setOnClickListener(toggleExpandCollapse);

            departmentsLayout.addView(departmentView);
        }
    }

    // Fetch contact data for a specific department
    private void fetchContactsForDepartment(int departmentId, LinearLayout contactsContainer) {
        // Clear existing contacts to avoid duplication
        contactsContainer.removeAllViews();

        Call<ContactResponse> call = taskApi.getContactsForDepartment(departmentId);
        call.enqueue(new Callback<ContactResponse>() {
            @Override
            public void onResponse(Call<ContactResponse> call, Response<ContactResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Contact> contacts = response.body().getData();
                    setupContacts(contacts, contactsContainer);
                } else {
                    Toast.makeText(EmergencyContactsActivity.this, "Failed to load contacts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ContactResponse> call, Throwable t) {
                Toast.makeText(EmergencyContactsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupContacts(List<Contact> contacts, LinearLayout contactsContainer) {
        for (Contact contact : contacts) {
            View contactView = getLayoutInflater().inflate(R.layout.contact_collapsible, contactsContainer, false);

            TextView contactName = contactView.findViewById(R.id.contactName);
            ImageView arrowIcon = contactView.findViewById(R.id.arrowIcon); // Arrow for expand/collapse
            LinearLayout contactDetails = contactView.findViewById(R.id.contactDetails);

            contactName.setText(contact.getBuildingName());
            contactDetails.setVisibility(View.GONE); // Initially collapsed

            // Landline and mobile TextViews
            TextView landline1 = contactView.findViewById(R.id.landline1);
            TextView landline2 = contactView.findViewById(R.id.landline2);
            TextView mobile1 = contactView.findViewById(R.id.mobile1);
            TextView mobile2 = contactView.findViewById(R.id.mobile2);
            TextView mobile3 = contactView.findViewById(R.id.mobile3);
            TextView mobile4 = contactView.findViewById(R.id.mobile4);

            // Set the landline and mobile numbers
            landline1.setText(contact.getLandline1());
            landline2.setText(contact.getLandline2());
            mobile1.setText(contact.getMobile1());
            mobile2.setText(contact.getMobile2());
            mobile3.setText(contact.getMobile3());
            mobile4.setText(contact.getMobile4());

            // Make the phone numbers clickable
            setPhoneNumberClick(landline1, contact.getLandline1());
            setPhoneNumberClick(landline2, contact.getLandline2());
            setPhoneNumberClick(mobile1, contact.getMobile1());
            setPhoneNumberClick(mobile2, contact.getMobile2());
            setPhoneNumberClick(mobile3, contact.getMobile3());
            setPhoneNumberClick(mobile4, contact.getMobile4());

            // Set the rest of the contact details
            TextView contactDetailsText = contactView.findViewById(R.id.contactDetailsText);
            String details = "Address: " + contact.getStreetAddress() + "\n" +
                    "Town: " + contact.getTown() + "\n" +
                    "City: " + contact.getCity() + "\n" +
                    "Region: " + contact.getRegion();
            contactDetailsText.setText(details);

            // Create a common method for handling expand/collapse
            View.OnClickListener toggleExpandCollapse = v -> {
                if (contactDetails.getVisibility() == View.GONE) {
                    contactDetails.setVisibility(View.VISIBLE);
                    arrowIcon.setImageResource(R.drawable.ic_arrow_up); // Change arrow to up when expanded
                } else {
                    contactDetails.setVisibility(View.GONE);
                    arrowIcon.setImageResource(R.drawable.ic_arrow_down); // Change arrow to down when collapsed
                }
            };

            // Set the same listener for both the name and the arrow
            contactName.setOnClickListener(toggleExpandCollapse);
            arrowIcon.setOnClickListener(toggleExpandCollapse);

            LatLng contactLocation = new LatLng(contact.getLatitude(), contact.getLongitude());
            mMap.addMarker(new MarkerOptions().position(contactLocation).title(contact.getBuildingName()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(contactLocation, 10));

            contactsContainer.addView(contactView);
        }
    }

    private void setPhoneNumberClick(TextView textView, String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            textView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            });
        } else {
            textView.setVisibility(View.GONE); // Hide if there's no number
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
