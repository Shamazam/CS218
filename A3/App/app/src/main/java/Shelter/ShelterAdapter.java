package Shelter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import readyfiji.app.R;

public class ShelterAdapter extends RecyclerView.Adapter<ShelterAdapter.ShelterViewHolder> {

    private List<Shelter> shelterList;

    // Constructor
    public ShelterAdapter(List<Shelter> shelterList) {
        this.shelterList = shelterList;
    }

    @Override
    public ShelterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the shelter_list_item layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shelter_list_item, parent, false);
        return new ShelterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShelterViewHolder holder, int position) {
        Shelter shelter = shelterList.get(position);

        // Set basic shelter information
        holder.shelterName.setText(shelter.getName());
        holder.personInCharge.setText("Person in Charge: " + shelter.getPersonInCharge());

        // For the Contact, set only the number to be blue and underlined
        String primaryContactText = "Contact: ";
        holder.contactNumber.setText(primaryContactText);

        if (shelter.getContactNumber() != null && !shelter.getContactNumber().isEmpty()) {
            holder.contactNumber.append(createClickableSpan(shelter.getContactNumber(), holder.itemView.getContext()));
            holder.contactNumber.setMovementMethod(LinkMovementMethod.getInstance());  // Make the number clickable
        }

        // For the Secondary Contact, set only the number to be blue and underlined
        String secondaryContactText = "Secondary Contact: ";
        holder.secondaryContact.setText(secondaryContactText);

        if (shelter.getSecondaryContact() != null && !shelter.getSecondaryContact().isEmpty()) {
            holder.secondaryContact.append(createClickableSpan(shelter.getSecondaryContact(), holder.itemView.getContext()));
            holder.secondaryContact.setMovementMethod(LinkMovementMethod.getInstance());  // Make the number clickable
        }

        holder.address.setText("Address: " + shelter.getFullAddress());
        holder.occupancy.setText("Capacity: " + shelter.getCurrentOccupancy() + "/" + shelter.getCapacity());
        holder.isFull.setText(shelter.isFull() ? "Status: Full" : "Status: Available");

        // Expand/Collapse logic
        boolean isExpanded = shelter.isExpanded();
        holder.shelterDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.arrowIcon.setImageResource(isExpanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);

        // Click listener to expand/collapse details
        holder.itemView.setOnClickListener(v -> {
            shelter.setExpanded(!shelter.isExpanded());
            notifyItemChanged(position);  // Refresh item
        });
    }





    @Override
    public int getItemCount() {
        return shelterList.size();
    }

    // ViewHolder class to hold the views for each item
    public static class ShelterViewHolder extends RecyclerView.ViewHolder {
        public TextView shelterName, personInCharge, contactNumber, secondaryContact, address, occupancy, isFull;
        public LinearLayout shelterDetails;  // The layout containing shelter details
        public ImageView arrowIcon;  // The expand/collapse arrow icon

        public ShelterViewHolder(View itemView) {
            super(itemView);

            // Initialize views
            shelterName = itemView.findViewById(R.id.shelterName);
            personInCharge = itemView.findViewById(R.id.personInCharge);
            contactNumber = itemView.findViewById(R.id.contactNumber);
            secondaryContact = itemView.findViewById(R.id.secondaryContact);
            address = itemView.findViewById(R.id.address);
            occupancy = itemView.findViewById(R.id.occupancy);
            isFull = itemView.findViewById(R.id.isFull);
            shelterDetails = itemView.findViewById(R.id.shelterDetails);
            arrowIcon = itemView.findViewById(R.id.arrowIcon);
        }
    }

    private SpannableString createClickableSpan(String phoneNumber, Context context) {
        SpannableString spannableString = new SpannableString(phoneNumber);

        // Set the color to blue and underline
        spannableString.setSpan(new UnderlineSpan(), 0, phoneNumber.length(), 0);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, android.R.color.holo_blue_light)), 0, phoneNumber.length(), 0);

        // Set the click listener to open the dialer
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                context.startActivity(intent);  // Open phone dialer with the number
            }
        }, 0, phoneNumber.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

}
