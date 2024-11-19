package NotificationInbox;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import readyfiji.app.R;
import readyfiji.app.RetrofitClientInstance;
import readyfiji.app.TaskApi;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<NotificationModel> notificationList;
    private Context context;
    private boolean[] expandedStates;  // Array to store the expanded state of each item
    private int userId;  // This should be the current user's ID

    public NotificationAdapter(List<NotificationModel> notificationList, Context context, int userId) {
        this.notificationList = notificationList;
        this.context = context;
        this.expandedStates = new boolean[notificationList.size()];  // Initialize the expanded states
        this.userId = userId;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationModel notification = notificationList.get(position);

        // Set title and message
        holder.title.setText(notification.getTitle());
        holder.message.setText(notification.getMessage());

        // Format and display the createdAt timestamp
        String formattedDateTime = formatDateTime(notification.getCreatedAt());
        holder.createdAt.setText(formattedDateTime);

        // Handle the expanded state for the message
        boolean isExpanded = expandedStates[position];
        holder.message.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.expandIcon.setImageResource(isExpanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);

        // Style the title based on whether the notification is read
        holder.title.setTypeface(null, notification.isRead() ? Typeface.NORMAL : Typeface.BOLD);

        // Handle click to toggle expand/collapse and mark as read if needed
        holder.itemView.setOnClickListener(v -> {
            if (!notification.isRead()) {
                // Log the user ID and notification ID before calling the API
                Log.d("NotificationAdapter", "Marking notification as read. User ID: " + userId + ", Notification ID: " + notification.getId());

                // Mark the notification as read via API call
                markNotificationAsRead(userId, notification.getId(), position);
            }

            // Toggle the expanded state and refresh the item view
            expandedStates[position] = !expandedStates[position];
            notifyItemChanged(position);
        });
    }



    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    // Update the list and notify the adapter
    public void updateList(List<NotificationModel> newList) {
        notificationList = newList;
        expandedStates = new boolean[notificationList.size()];  // Reset expanded states for the new list
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, createdAt;
        ImageView expandIcon;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notificationTitle);
            message = itemView.findViewById(R.id.notificationMessage);
            createdAt = itemView.findViewById(R.id.notificationTimestamp);
            expandIcon = itemView.findViewById(R.id.expandIcon);  // Reference for the expand/collapse arrow icon
        }
    }

    // Method to format the date from the server response
    private String formatDateTime(String originalDateTime) {
        try {
            // Original date format from the server: yyyy-MM-dd HH:mm:ss
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            // Desired format: dd-MM-yyyy hh:mm a
            SimpleDateFormat desiredFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
            Date date = originalFormat.parse(originalDateTime);
            return desiredFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return originalDateTime;  // Return the original if parsing fails
        }
    }

    // API call to mark notification as read
    private void markNotificationAsRead(int userId, int notificationId, int position) {
        Log.d("NotificationAdapter", "Sending API request to mark as read. User ID: " + userId + ", Notification ID: " + notificationId);

        TaskApi api = RetrofitClientInstance.getRetrofitInstance().create(TaskApi.class);
        Call<ResponseBody> call = api.markNotificationAsRead(userId, notificationId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("NotificationAdapter", "Notification marked as read on server. Position: " + position);
                    notificationList.get(position).setRead(1);  // Update local state
                    notifyItemChanged(position);  // Refresh UI
                } else {
                    Log.e("NotificationAdapter", "Failed to mark notification as read. Response: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("NotificationAdapter", "API call failed: " + t.getMessage());
            }
        });
    }



}
