package AdminEmergencyContacts;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class AdminEditEmergencyActivity extends AppCompatActivity implements OnMapReadyCallback {

    // UI Elements
    private Spinner departmentSpinnerEdit, departmentSpinnerContact, contactSpinner;
    private EditText editDepartmentName, buildingName, landlineNumber1, landlineNumber2, mobileNumber1, mobileNumber2, mobileNumber3, mobileNumber4, streetAddress, town, city, region;
    private Button btnSubmitDepartment, btnDeleteDepartment, btnSubmitContact, btnDeleteContact;

    private GoogleMap mMap;
    private LatLng selectedLatLng;
    private List<AdminDepartment> departmentList;
    private List<AdminContact> contactList;
    private TaskApi taskApi;
    private boolean isMapReady = false;
    private AdminContact selectedContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_emergency_contacts);

        // Initialize Retrofit API
        taskApi = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Emergency Contacts");

        // Initialize UI elements
        departmentSpinnerEdit = findViewById(R.id.spinner_departments_edit);
        departmentSpinnerContact = findViewById(R.id.spinner_departments_contact);
        contactSpinner = findViewById(R.id.spinner_contacts);

        editDepartmentName = findViewById(R.id.edit_department_name);
        buildingName = findViewById(R.id.building_name);
        landlineNumber1 = findViewById(R.id.landline_number_1);
        landlineNumber2 = findViewById(R.id.landline_number_2);
        mobileNumber1 = findViewById(R.id.mobile_number_1);
        mobileNumber2 = findViewById(R.id.mobile_number_2);
        mobileNumber3 = findViewById(R.id.mobile_number_3);
        mobileNumber4 = findViewById(R.id.mobile_number_4);
        streetAddress = findViewById(R.id.street_address);
        town = findViewById(R.id.town);
        city = findViewById(R.id.city);
        region = findViewById(R.id.region);

        btnSubmitDepartment = findViewById(R.id.btn_submit_edit_department);
        btnDeleteDepartment = findViewById(R.id.btn_delete_edit_department);
        btnSubmitContact = findViewById(R.id.btn_submit_edit_contact);
        btnDeleteContact = findViewById(R.id.btn_delete_edit_contact);

        // Initialize Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Load departments and populate spinners
        loadDepartments();

        // Set department spinner listeners
        departmentSpinnerEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AdminDepartment selectedDepartment = departmentList.get(position);
                editDepartmentName.setText(selectedDepartment.getDepartmentName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        departmentSpinnerContact.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AdminDepartment selectedDepartment = departmentList.get(position);
                loadEmergencyContacts(selectedDepartment.getDepartmentId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        contactSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedContact = contactList.get(position);
                populateContactFields(selectedContact);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Submit department
        btnSubmitDepartment.setOnClickListener(v -> updateDepartment());

        // Delete department
        btnDeleteDepartment.setOnClickListener(v -> deleteDepartment());

        // Submit emergency contact
        btnSubmitContact.setOnClickListener(v -> updateEmergencyContact());

        // Delete emergency contact
        btnDeleteContact.setOnClickListener(v -> deleteEmergencyContact());
    }

    private void loadDepartments() {
        Call<AdminDepartmentResponse> call = taskApi.getAdminDepartments();
        call.enqueue(new Callback<AdminDepartmentResponse>() {
            @Override
            public void onResponse(Call<AdminDepartmentResponse> call, Response<AdminDepartmentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    departmentList = response.body().getData();
                    populateDepartmentSpinners();
                }
            }

            @Override
            public void onFailure(Call<AdminDepartmentResponse> call, Throwable t) {
                Toast.makeText(AdminEditEmergencyActivity.this, "Failed to load departments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateDepartmentSpinners() {
        ArrayAdapter<AdminDepartment> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departmentList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinnerEdit.setAdapter(adapter);
        departmentSpinnerContact.setAdapter(adapter);
    }

    private void loadEmergencyContacts(int departmentId) {
        Call<AdminContactResponse> call = taskApi.getAdminContactsForDepartment(departmentId);
        call.enqueue(new Callback<AdminContactResponse>() {
            @Override
            public void onResponse(Call<AdminContactResponse> call, Response<AdminContactResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    contactList = response.body().getData();
                    populateContactSpinner();
                }
            }

            @Override
            public void onFailure(Call<AdminContactResponse> call, Throwable t) {
                Toast.makeText(AdminEditEmergencyActivity.this, "Failed to load contacts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateContactSpinner() {
        ArrayAdapter<AdminContact> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, contactList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contactSpinner.setAdapter(adapter);
    }

    private void populateContactFields(AdminContact contact) {
        buildingName.setText(contact.getBuildingName());
        landlineNumber1.setText(contact.getLandline1());
        landlineNumber2.setText(contact.getLandline2());
        mobileNumber1.setText(contact.getMobile1());
        mobileNumber2.setText(contact.getMobile2());
        mobileNumber3.setText(contact.getMobile3());
        mobileNumber4.setText(contact.getMobile4());
        streetAddress.setText(contact.getStreetAddress());
        town.setText(contact.getTown());
        city.setText(contact.getCity());
        region.setText(contact.getRegion());

        // Update the map location
        selectedLatLng = new LatLng(contact.getLatitude(), contact.getLongitude());
        updateMapLocation(selectedLatLng);
    }

    private void updateDepartment() {
        String newDepartmentName = editDepartmentName.getText().toString();
        AdminDepartment selectedDepartment = (AdminDepartment) departmentSpinnerEdit.getSelectedItem();

        if (!newDepartmentName.isEmpty()) {
            Call<Void> call = taskApi.updateDepartment(selectedDepartment.getDepartmentId(), newDepartmentName);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(AdminEditEmergencyActivity.this, "Department updated successfully", Toast.LENGTH_SHORT).show();
                    refreshDepartments(); // Refresh departments after update
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(AdminEditEmergencyActivity.this, "Failed to update department", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please enter a new department name", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteDepartment() {
        AdminDepartment selectedDepartment = (AdminDepartment) departmentSpinnerEdit.getSelectedItem();
        Call<Void> call = taskApi.deleteDepartment(selectedDepartment.getDepartmentId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(AdminEditEmergencyActivity.this, "Department deleted successfully", Toast.LENGTH_SHORT).show();
                refreshDepartments(); // Refresh departments after deletion
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminEditEmergencyActivity.this, "Failed to delete department", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmergencyContact() {
        String building = buildingName.getText().toString();
        String landline1 = landlineNumber1.getText().toString();
        String landline2 = landlineNumber2.getText().toString();
        String mobile1 = mobileNumber1.getText().toString();
        String mobile2 = mobileNumber2.getText().toString();
        String mobile3 = mobileNumber3.getText().toString();
        String mobile4 = mobileNumber4.getText().toString();
        String street = streetAddress.getText().toString();
        String townName = town.getText().toString();
        String cityName = city.getText().toString();
        String regionName = region.getText().toString();

        if (selectedContact != null && selectedLatLng != null) {
            int departmentId = ((AdminDepartment) departmentSpinnerContact.getSelectedItem()).getDepartmentId();

            Call<Void> call = taskApi.updateEmergencyContact(
                    selectedContact.getId(), departmentId, building, landline1, landline2, mobile1, mobile2, mobile3, mobile4,
                    street, townName, cityName, regionName, selectedLatLng.latitude, selectedLatLng.longitude);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminEditEmergencyActivity.this, "Contact updated successfully", Toast.LENGTH_SHORT).show();
                        refreshContacts(); // Refresh contacts after update
                    } else {
                        Toast.makeText(AdminEditEmergencyActivity.this, "Failed to update contact", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(AdminEditEmergencyActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteEmergencyContact() {
        Call<Void> call = taskApi.deleteEmergencyContact(selectedContact.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(AdminEditEmergencyActivity.this, "Contact deleted successfully", Toast.LENGTH_SHORT).show();
                refreshContacts(); // Refresh contacts after deletion
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminEditEmergencyActivity.this, "Failed to delete contact", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshDepartments() {
        // Reload departments and refresh department spinner
        loadDepartments();
        // Optionally reset the fields
        editDepartmentName.setText("");
    }

    private void refreshContacts() {
        // Reload contacts for the currently selected department
        AdminDepartment selectedDepartment = (AdminDepartment) departmentSpinnerContact.getSelectedItem();
        loadEmergencyContacts(selectedDepartment.getDepartmentId());
        // Optionally reset the fields
        clearContactFields();
    }

    private void clearContactFields() {
        // Clear all the contact fields
        buildingName.setText("");
        landlineNumber1.setText("");
        landlineNumber2.setText("");
        mobileNumber1.setText("");
        mobileNumber2.setText("");
        mobileNumber3.setText("");
        mobileNumber4.setText("");
        streetAddress.setText("");
        town.setText("");
        city.setText("");
        region.setText("");
        selectedLatLng = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        isMapReady = true;

        mMap.setOnMapClickListener(latLng -> {
            selectedLatLng = latLng;
            updateMapLocation(latLng);
        });
    }

    private void updateMapLocation(LatLng latLng) {
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
