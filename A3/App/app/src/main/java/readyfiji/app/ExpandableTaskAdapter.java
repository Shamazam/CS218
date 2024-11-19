package readyfiji.app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpandableTaskAdapter extends RecyclerView.Adapter<ExpandableTaskAdapter.DisasterViewHolder> {
    private Context context;
    private List<Disaster> disasterList;
    private TaskApi taskApi;
    private Map<Integer, Boolean> disasterExpandedState = new HashMap<>(); // Track expanded/collapsed state
    private int userId; // To track userId

    public ExpandableTaskAdapter(Context context, List<Disaster> disasterList, TaskApi taskApi, int userId) {
        this.context = context;
        this.disasterList = disasterList;
        this.taskApi = taskApi;
        this.userId = userId; // Initialize userId
    }

    public static class DisasterViewHolder extends RecyclerView.ViewHolder {
        public TextView disasterName;
        public LinearLayout taskListLayout;
        public EditText addTaskInput;
        public Button addTaskButton;
        public ImageView expandArrow; // Arrow to indicate expand/collapse

        public DisasterViewHolder(View view) {
            super(view);
            disasterName = view.findViewById(R.id.disasterName);
            taskListLayout = view.findViewById(R.id.taskListLayout);
            addTaskInput = view.findViewById(R.id.addTaskInput);
            addTaskButton = view.findViewById(R.id.addTaskButton);
            expandArrow = view.findViewById(R.id.expandArrow); // Initialize the arrow icon
        }
    }

    @Override
    public DisasterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disaster_item, parent, false);
        return new DisasterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DisasterViewHolder holder, int position) {
        final Disaster disaster = disasterList.get(position);
        holder.disasterName.setText(disaster.getDisasterName());

        // Determine if expanded
        boolean isExpanded = disasterExpandedState.getOrDefault(disaster.getDisasterId(), false);
        holder.taskListLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        // Change the arrow icon based on expanded/collapsed state
        holder.expandArrow.setImageResource(isExpanded ? R.drawable.ic_arrow_down : R.drawable.ic_arrow_right);

        // Handle expand/collapse logic for both the name and arrow
        View.OnClickListener toggleExpandCollapse = v -> {
            boolean currentlyExpanded = disasterExpandedState.getOrDefault(disaster.getDisasterId(), false);
            disasterExpandedState.put(disaster.getDisasterId(), !currentlyExpanded);
            notifyItemChanged(position);
        };

        holder.disasterName.setOnClickListener(toggleExpandCollapse);  // Clicking the disaster name toggles
        holder.expandArrow.setOnClickListener(toggleExpandCollapse);  // Clicking the arrow also toggles

        // Populate task list for the disaster
        holder.taskListLayout.removeAllViews();
        List<TaskItem> taskItems = disaster.getTasks();
        for (int i = 0; i < taskItems.size(); i++) {
            final TaskItem task = taskItems.get(i);
            View taskView = LayoutInflater.from(context).inflate(R.layout.task_item, holder.taskListLayout, false);

            TextView taskName = taskView.findViewById(R.id.taskName);
            CheckBox taskCheckbox = taskView.findViewById(R.id.taskCheckbox);
            Button editTaskButton = taskView.findViewById(R.id.editTaskButton);
            Button deleteTaskButton = taskView.findViewById(R.id.deleteTaskButton);

            taskName.setText(task.getTaskName());
            taskCheckbox.setChecked(task.isCompleted());

            // Handle task completion toggle (update server for all tasks including admin tasks)
            taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setCompleted(isChecked);
                updateTaskCompletedStatus(disaster, task); // Always update the server, even for admin tasks
            });

            final int index = i;

            // Check if the task was created by the user
            if ("user".equals(task.getTaskType())) {
                // Enable Edit and Delete buttons for user-created tasks
                editTaskButton.setVisibility(View.VISIBLE);
                deleteTaskButton.setVisibility(View.VISIBLE);

                // Add debug log to check the task ID
                Log.d("ExpandableTaskAdapter", "Task ID before delete: " + task.getTaskId());

                deleteTaskButton.setOnClickListener(v -> {
                    // Ensure the task ID is valid before calling the delete method
                    if (task.getTaskId() != 0) {
                        deleteTaskFromServer(disaster, task, index); // Use the correct task item here
                    } else {
                        Log.e("ExpandableTaskAdapter", "Attempted to delete task with invalid ID: " + task.getTaskId());
                        Toast.makeText(context, "Cannot delete task with invalid ID", Toast.LENGTH_SHORT).show();
                    }
                });

                editTaskButton.setOnClickListener(v -> openEditTaskDialog(disaster, task, index, holder.taskListLayout));  // Pass the taskListLayout
            } else {
                // Hide or disable Edit and Delete buttons for admin-created tasks
                editTaskButton.setVisibility(View.GONE);
                deleteTaskButton.setVisibility(View.GONE);
            }

            holder.taskListLayout.addView(taskView);
        }

        // Handle adding new tasks
        holder.addTaskButton.setOnClickListener(v -> {
            String newTaskName = holder.addTaskInput.getText().toString();
            if (!newTaskName.isEmpty()) {
                addTaskToServer(disaster, newTaskName);
                holder.addTaskInput.setText("");  // Clear the input field after adding
            } else {
                Toast.makeText(context, "Task name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return disasterList.size();
    }

    // Add a method to update the list of disasters
    public void updateDisasters(List<Disaster> updatedDisasterList) {
        this.disasterList.clear();  // Clear the current list
        this.disasterList.addAll(updatedDisasterList);  // Add the new list
        notifyDataSetChanged();  // Notify adapter about data changes
    }

    // Add a new task to a specific disaster
    private void addTaskToServer(Disaster disaster, String taskName) {
        // Call the API to add the task and get the newly created TaskItem from the server
        taskApi.addTask(disaster.getDisasterId(), taskName, "user", userId).enqueue(new Callback<TaskItem>() {
            @Override
            public void onResponse(Call<TaskItem> call, Response<TaskItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Get the full TaskItem object from the response
                    TaskItem newTask = response.body();  // This includes the correct task_id from the server

                    // Add the task to the disaster's task list with the correct task_id
                    addTaskToDisaster(disaster, newTask);

                    // Notify the adapter that data has changed
                    notifyDataSetChanged();

                    // Inform the user the task was added successfully
                    Toast.makeText(context, "Task added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle the case where the server response is not successful
                    Toast.makeText(context, "Failed to add task", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TaskItem> call, Throwable t) {
                // Handle failure in communication with the server
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Update the tasks for a specific disaster
    public void updateTasksForDisaster(Disaster disaster, List<TaskItem> newTaskList) {
        for (Disaster d : disasterList) {
            if (d.getDisasterId() == disaster.getDisasterId()) {
                d.setTasks(newTaskList);
                break;
            }
        }
        notifyDataSetChanged();
    }

    // Add a new task to a specific disaster
    public void addTaskToDisaster(Disaster disaster, TaskItem task) {
        for (Disaster d : disasterList) {
            if (d.getDisasterId() == disaster.getDisasterId()) {
                d.getTasks().add(task);
                notifyDataSetChanged();
                break;
            }
        }
    }

    // Remove a task from a specific disaster
    public void removeTaskFromDisaster(Disaster disaster, int position) {
        for (Disaster d : disasterList) {
            if (d.getDisasterId() == disaster.getDisasterId()) {
                d.getTasks().remove(position);  // Remove the task from the local list
                notifyDataSetChanged();  // Notify adapter to refresh the UI
                break;
            }
        }
    }


    // Method to update the task completed status on the server
    private void updateTaskCompletedStatus(Disaster disaster, TaskItem task) {
        taskApi.updateTask(task.getTaskId(), task.getTaskName(), task.getTaskType(), task.isCompleted() ? 1 : 0, userId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, "Task status updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to update task status", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to delete a task from the server and refresh the list after deletion
    private void deleteTaskFromServer(Disaster disaster, TaskItem task, int position) {
        Log.d("ExpandableTaskAdapter", "Attempting to delete task with ID: " + task.getTaskId());

        taskApi.deleteTask(task.getTaskId(), task.getTaskType(), userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ExpandableTaskAdapter", "Task deleted successfully from server.");
                    // Remove the task locally
                    removeTaskFromDisaster(disaster, position);
                    Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show();

                    // Fetch the updated list of tasks from the server to refresh the UI
                    fetchTasksForDisaster(disaster);
                } else {
                    Log.e("ExpandableTaskAdapter", "Failed to delete task from server. Response code: " + response.code());
                    Toast.makeText(context, "Failed to delete task from server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ExpandableTaskAdapter", "Error while deleting task: " + t.getMessage());
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to fetch tasks from the server for a specific disaster
    private void fetchTasksForDisaster(Disaster disaster) {
        taskApi.getTasksForDisaster(disaster.getDisasterId(), userId).enqueue(new Callback<List<TaskItem>>() {
            @Override
            public void onResponse(Call<List<TaskItem>> call, Response<List<TaskItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Update tasks with the fresh data from the server
                    updateTasksForDisaster(disaster, response.body());
                } else {
                    Toast.makeText(context, "Failed to refresh tasks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TaskItem>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Method to open an edit task dialog
    private void openEditTaskDialog(Disaster disaster, TaskItem task, int taskPosition, LinearLayout taskListLayout) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("Edit Task");

        final EditText input = new EditText(context);
        input.setText(task.getTaskName());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTaskName = input.getText().toString();
            if (!newTaskName.isEmpty()) {
                // Update the task's name in the UI immediately
                task.setTaskName(newTaskName);

                // Now we need to directly update the task name in the view instead of notifying the adapter to refresh everything
                View taskView = taskListLayout.getChildAt(taskPosition); // Get the task view directly by its position
                TextView taskName = taskView.findViewById(R.id.taskName);
                taskName.setText(newTaskName);  // Update the text immediately

                // Call server to update task
                taskApi.updateTask(task.getTaskId(), task.getTaskName(), task.getTaskType(), task.isCompleted() ? 1 : 0, userId)
                        .enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(context, "Task updated successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Failed to update task", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
