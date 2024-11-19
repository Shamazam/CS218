package AboutUs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutUsActivity extends AppCompatActivity {

    private LinearLayout contentLayout;
    private TaskApi apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // Bind to the dynamic content layout
        contentLayout = findViewById(R.id.dynamicContentContainer);

        // Set up back button functionality
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> onBackPressed());

        // Initialize Retrofit API service
        apiService = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);

        // Fetch About Us data from the server
        fetchAboutUsData();
    }

    // Fetch About Us data
    private void fetchAboutUsData() {
        Call<List<AboutUsSection>> call = apiService.getAboutUsSections();
        call.enqueue(new Callback<List<AboutUsSection>>() {
            @Override
            public void onResponse(Call<List<AboutUsSection>> call, Response<List<AboutUsSection>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AboutUsSection> sections = response.body();
                    // Loop through each section and create views dynamically
                    for (AboutUsSection section : sections) {
                        if (!section.getTitle().equalsIgnoreCase("Contact Us")) {
                            createSectionView(section);
                        }
                    }
                    // Now add the "Contact Us" section last
                    fetchContactUsData();
                } else {
                    Toast.makeText(AboutUsActivity.this, "Failed to load About Us data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AboutUsSection>> call, Throwable t) {
                Toast.makeText(AboutUsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createSectionView(AboutUsSection section) {
        // Create a container for each section with rounded background
        LinearLayout sectionContainer = new LinearLayout(this);
        sectionContainer.setOrientation(LinearLayout.VERTICAL);
        sectionContainer.setBackground(getResources().getDrawable(R.drawable.rounded_background));
        sectionContainer.setPadding(24, 24, 24, 24);

        // Set layout parameters with margins
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 16, 0, 16);  // Adds margin between sections
        sectionContainer.setLayoutParams(params);

        // Create and style the section title
        TextView sectionTitle = new TextView(this);
        sectionTitle.setText(section.getTitle());
        sectionTitle.setTextSize(18);
        sectionTitle.setTypeface(null, Typeface.BOLD);
        sectionTitle.setPadding(0, 16, 0, 8);
        sectionTitle.setGravity(android.view.Gravity.CENTER);  // Center the title
        sectionContainer.addView(sectionTitle);

        // Add each content item for the section
        for (AboutUsContent content : section.getContent()) {
            TextView contentView = new TextView(this);
            contentView.setText(content.getContent());
            contentView.setPadding(0, 0, 0, 16);

            // Justify the text content
            contentView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);  // Left-align text
            contentView.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);  // Justify text

            // Add the content to the section container
            sectionContainer.addView(contentView);
        }

        // Add the section container to the dynamic content layout
        contentLayout.addView(sectionContainer);
    }


    // Fetch Contact Us data
    private void fetchContactUsData() {
        Call<ContactUs> call = apiService.getContactUs();
        call.enqueue(new Callback<ContactUs>() {
            @Override
            public void onResponse(Call<ContactUs> call, Response<ContactUs> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ContactUs contactUs = response.body();
                    createContactUsView(contactUs);
                } else {
                    Toast.makeText(AboutUsActivity.this, "Failed to load Contact Us data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ContactUs> call, Throwable t) {
                Toast.makeText(AboutUsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createContactUsView(ContactUs contactUs) {
        // Create a container for the "Contact Us" section with rounded background
        LinearLayout contactUsContainer = new LinearLayout(this);
        contactUsContainer.setOrientation(LinearLayout.VERTICAL);
        contactUsContainer.setBackground(getResources().getDrawable(R.drawable.rounded_background));
        contactUsContainer.setPadding(24, 24, 24, 24);

        // Set layout parameters with margins
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 16, 0, 16);  // Adds margin between sections
        contactUsContainer.setLayoutParams(params);

        // Create and style the "Contact Us" title
        TextView contactUsLabel = new TextView(this);
        contactUsLabel.setText("Contact Us");
        contactUsLabel.setTextSize(18);
        contactUsLabel.setTypeface(null, Typeface.BOLD);
        contactUsLabel.setPadding(0, 16, 0, 8);
        contactUsLabel.setGravity(android.view.Gravity.CENTER);  // Center the "Contact Us" title
        contactUsContainer.addView(contactUsLabel);

        // Add Email section with icon
        LinearLayout emailLayout = new LinearLayout(this);
        emailLayout.setOrientation(LinearLayout.HORIZONTAL);
        emailLayout.setPadding(0, 8, 0, 8);

        // Email Icon
        ImageView emailIcon = new ImageView(this);
        emailIcon.setImageResource(R.drawable.ic_email);  // Assuming you have an email icon in your drawable resources
        emailIcon.setPadding(0, 0, 16, 0);  // Padding between icon and text
        emailLayout.addView(emailIcon);

        // Email Text
        TextView emailView = new TextView(this);
        emailView.setText(contactUs.getEmail());
        emailView.setTextColor(Color.BLUE);
        emailView.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + contactUs.getEmail()));
            startActivity(Intent.createChooser(emailIntent, "Send email using"));
        });
        emailLayout.addView(emailView);

        // Add emailLayout to the container
        contactUsContainer.addView(emailLayout);

        // Add Phone section with icon
        LinearLayout phoneLayout = new LinearLayout(this);
        phoneLayout.setOrientation(LinearLayout.HORIZONTAL);
        phoneLayout.setPadding(0, 8, 0, 8);

        // Phone Icon
        ImageView phoneIcon = new ImageView(this);
        phoneIcon.setImageResource(R.drawable.ic_phone);  // Assuming you have a phone icon in your drawable resources
        phoneIcon.setPadding(0, 0, 16, 0);  // Padding between icon and text
        phoneLayout.addView(phoneIcon);

        // Phone Text
        TextView phoneView = new TextView(this);
        phoneView.setText(contactUs.getPhone());
        phoneView.setTextColor(Color.BLUE);
        phoneView.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactUs.getPhone()));
            startActivity(callIntent);
        });
        phoneLayout.addView(phoneView);

        // Add phoneLayout to the container
        contactUsContainer.addView(phoneLayout);

        // Add the Contact Us container to the dynamic content layout
        contentLayout.addView(contactUsContainer);
    }

}
