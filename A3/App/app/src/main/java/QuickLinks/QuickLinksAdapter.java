package QuickLinks;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import readyfiji.app.R;

public class QuickLinksAdapter extends RecyclerView.Adapter<QuickLinksAdapter.ViewHolder> {

    private List<Category> categoryList;

    public QuickLinksAdapter(List<Category> categories) {
        this.categoryList = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryTitle.setText(category.getCategoryName());

        // Clear previous views before adding new ones
        holder.linkContainer.removeAllViews();

        // Populate the links in the collapsible section
        for (Link link : category.getLinks()) {
            TextView linkView = new TextView(holder.itemView.getContext());
            linkView.setText("• " + link.getTitle());  // Add bullet point
            linkView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.blue));  // Set text color to blue
            linkView.setTextSize(16);

            // Handle link click
            linkView.setOnClickListener(v -> {
                if (!TextUtils.isEmpty(link.getUrl())) {
                    String url = link.getUrl();

                    // Ensure URL starts with "http://"
                    if (!url.startsWith("http://")) {
                        url = "http://" + url.replaceFirst("^https?://", "");
                    }

                    try {
                        // Create the intent to view the URL
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                        // Create a chooser to let the user select the app
                        Intent chooser = Intent.createChooser(browserIntent, "Open link with");

                        // Log the URL for debugging
                        Log.d("Opening URL", url);

                        // Check if there's an app that can handle this intent
                        if (chooser.resolveActivity(holder.itemView.getContext().getPackageManager()) != null) {
                            holder.itemView.getContext().startActivity(chooser);
                        } else {
                            // If no app can handle the URL, show an error message
                            Toast.makeText(holder.itemView.getContext(), "No application available to handle URL", Toast.LENGTH_SHORT).show();
                            Log.e("LinkError", "No application available to handle URL: " + url);
                        }
                    } catch (Exception e) {
                        // Log the exception if something goes wrong
                        Toast.makeText(holder.itemView.getContext(), "Error opening link", Toast.LENGTH_SHORT).show();
                        Log.e("LinkError", "Exception while opening URL: " + e.getMessage(), e);
                    }
                } else {
                    Toast.makeText(holder.itemView.getContext(), "URL is empty or invalid", Toast.LENGTH_SHORT).show();
                }
            });

            holder.linkContainer.addView(linkView);
        }

        // Set initial arrow direction (collapsed state)
        holder.arrowIcon.setImageResource(R.drawable.ic_arrow_down);

        // Handle click for both the title and the arrow icon
        holder.categoryHeader.setOnClickListener(v -> {
            if (holder.linkContainer.getVisibility() == View.VISIBLE) {
                // Collapse: Hide links and change arrow direction
                holder.linkContainer.setVisibility(View.GONE);
                holder.arrowIcon.setImageResource(R.drawable.ic_arrow_down);  // Arrow pointing down
            } else {
                // Expand: Show links and change arrow direction
                holder.linkContainer.setVisibility(View.VISIBLE);
                holder.arrowIcon.setImageResource(R.drawable.ic_arrow_up);  // Arrow pointing up
            }
        });
    }

    @Override
    public int getItemCount() {
        // Safeguard against potential null categoryList
        return categoryList != null ? categoryList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        LinearLayout linkContainer;
        ImageView arrowIcon;
        LinearLayout categoryHeader;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.categoryTitle);
            linkContainer = itemView.findViewById(R.id.linkContainer);
            arrowIcon = itemView.findViewById(R.id.arrowIcon);  // Initialize arrow icon
            categoryHeader = itemView.findViewById(R.id.categoryHeader);  // Initialize category header for click
        }
    }
}
