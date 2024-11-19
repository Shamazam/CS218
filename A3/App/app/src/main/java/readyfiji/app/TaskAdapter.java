package readyfiji.app;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import readyfiji.app.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<TaskItem> taskList;
    private TaskApi taskApi;
    private Context context;
    private int userId; // Add userId for task completion tracking

    public TaskAdapter(Context context, List<TaskItem> taskList, TaskApi taskApi, int userId) {
        this.context = context;
        this.taskList = taskList;
        this.taskApi = taskApi;
        this.userId = userId; // Initialize userId
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView taskName;
        public CheckBox taskCheckbox;
        public Button editTaskButton;
        public Button deleteTaskButton;

        public TaskViewHolder(View view) {
            super(view);
            taskName = view.findViewById(R.id.taskName);
            taskCheckbox = view.findViewById(R.id.taskCheckbox);
            editTaskButton = view.findViewById(R.id.editTaskButton);
            deleteTaskButton = view.findViewById(R.id.deleteTaskButton);
        }
    }

    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        TaskItem task = taskList.get(position);

        // Set the task name and checkbox state
        holder.taskName.setText(task.getTaskName());
        holder.taskCheckbox.setOnCheckedChangeListener(null);  // Prevent triggering onBindViewHolder's listener
        holder.taskCheckbox.setChecked(task.isCompleted());   // Set the initial state
        holder.taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            updateTaskCompletedStatus(task);  // Update the task status on the server
        });


        // Show Edit and Delete buttons only if the task was created by the user
        if ("user".equals(task.getTaskType())) {
            holder.editTaskButton.setVisibility(View.VISIBLE);
            holder.deleteTaskButton.setVisibility(View.VISIBLE);
        } else {
            holder.editTaskButton.setVisibility(View.GONE);
            holder.deleteTaskButton.setVisibility(View.GONE);
        }

        // Edit task functionality
        holder.editTaskButton.setOnClickListener(v -> {
            openEditTaskDialog(task, holder.getAdapterPosition());
        });

        // Delete task functionality
        holder.deleteTaskButton.setOnClickListener(v -> {
            deleteTaskFromServer(task.getTaskId(), holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void removeTask(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
    }

    // Update tasks method to refresh the adapter
    public void updateTasks(List<TaskItem> newTaskList) {
        taskList.clear();
        taskList.addAll(newTaskList);
        notifyDataSetChanged();
    }

    // Method to add a new task locally
    public void addTask(TaskItem task) {
        taskList.add(task);
        notifyItemInserted(taskList.size() - 1);
    }

    // Method to update the task 'completed' status on the server
    private void updateTaskCompletedStatus(TaskItem task) {
        if ("user".equals(task.getTaskType())) {
            // Handle user tasks directly
            taskApi.updateTask(task.getTaskId(), task.getTaskName(), task.getTaskType(), task.isCompleted() ? 1 : 0, userId)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(context, "Task completion status updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to update task completion status", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else if ("admin".equals(task.getTaskType())) {
            // Handle admin tasks by updating user-specific completion status
            taskApi.updateTaskCompletion(task.getTaskId(), userId, task.isCompleted() ? 1 : 0)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(context, "Task completion status updated for this user", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to update task completion status", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Method to delete a task from the server
    private void deleteTaskFromServer(int taskId, int position) {
        Log.d("TaskAdapter", "Attempting to delete task with ID: " + taskId);
        taskApi.deleteTask(taskId, taskList.get(position).getTaskType(), userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("TaskAdapter", "Task deleted successfully from server.");
                    taskList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("TaskAdapter", "Failed to delete task from server.");
                    Toast.makeText(context, "Failed to delete task from server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("TaskAdapter", "Error while deleting task: " + t.getMessage());
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Method to update task details (e.g., name, type) on the server
    private void updateTaskStatus(TaskItem task) {
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

    // Method to open the Edit Task dialog
    private void openEditTaskDialog(TaskItem task, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Task");

        // Add EditText to dialog
        final EditText input = new EditText(context);
        input.setText(task.getTaskName());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTaskName = input.getText().toString();
            if (!newTaskName.isEmpty()) {
                task.setTaskName(newTaskName);
                updateTaskStatus(task); // Use the method to update the task on the server
                notifyItemChanged(position); // Notify the adapter to update the task at the given position
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
