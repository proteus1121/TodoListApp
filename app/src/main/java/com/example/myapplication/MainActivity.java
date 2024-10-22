package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.Task;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

//    private DatabaseHelper dbHelper;
    private TaskApiService taskApiService;
    private ArrayList<Task> tasks;
    private ArrayAdapter<Task> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        dbHelper = new DatabaseHelper(this);
        taskApiService = ApiClient.getRetrofitClient().create(TaskApiService.class);
        tasks = new ArrayList<>();

        final EditText editTextTask = findViewById(R.id.editTextTask);
        Button buttonAddTask = findViewById(R.id.buttonAddTask);
        ListView listViewTasks = findViewById(R.id.listViewTasks);

        // Initialize the adapter for ListView
        adapter = new ArrayAdapter<Task>(this, R.layout.list_item, tasks) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
                }

                TextView textViewTask = convertView.findViewById(R.id.textViewTask);
                Button buttonEditTask = convertView.findViewById(R.id.buttonEditTask);
                Button buttonDeleteTask = convertView.findViewById(R.id.buttonDeleteTask);

                Task task = getItem(position);
                textViewTask.setText(task.getText());

                // Edit button functionality
                buttonEditTask.setOnClickListener(v -> showEditDialog(position));

                // Delete button functionality
                buttonDeleteTask.setOnClickListener(v -> {
                   // dbHelper.deleteTask(position + 1); // Assuming taskId corresponds to position + 1
                   // tasks.remove(position);
                   // adapter.notifyDataSetChanged(); // Refresh the ListView
                });

                return convertView;
            }
        };

        // Set the adapter to the ListView
        listViewTasks.setAdapter(adapter);

        // Load existing tasks from the database
        loadTasks();

        // Add task on button click
        buttonAddTask.setOnClickListener(v -> {
            String taskText = editTextTask.getText().toString().trim();
            if (!taskText.isEmpty()) {
                Task newTask = new Task(taskText);
                addTaskToApi(newTask);
//                dbHelper.addTask(taskText);
//                tasks.add(newTask);
                adapter.notifyDataSetChanged(); // Refresh the ListView
                editTextTask.setText(""); // Clear the EditText
            }
        });
    }

    private void addTaskToApi(Task task) {
        taskApiService.addTask(task).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tasks.add(response.body());
                    adapter.notifyDataSetChanged(); // Refresh the ListView
                } else {
                    Log.e("API_ERROR", "Failed to add task.");
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Log.e("API_ERROR", "Failed to add task: " + t.getMessage());
            }
        });
    }

    private void loadTasks() {
        taskApiService.getTasks().enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tasks.clear();
                    tasks.addAll(response.body());
                    adapter.notifyDataSetChanged(); // Refresh the ListView
                } else {
                    Log.e("API_ERROR", "Failed to load tasks.");
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Log.e("API_ERROR", "Failed to load tasks: " + t.getMessage());
            }
        });
    }

//    private void loadTasks() {
//        tasks.clear();
//        for (String task : dbHelper.getAllTasks()) {
//            tasks.add(new Task(task));
//        }
//        adapter.notifyDataSetChanged(); // Notify adapter of data change
//    }

    private void showEditDialog(int position) {
        Task task = tasks.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Task");

        final EditText input = new EditText(this);
        input.setText(task.getText());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String updatedTaskText = input.getText().toString().trim();
            if (!updatedTaskText.isEmpty()) {
//                dbHelper.deleteTask(position + 1); // Remove old task
//                dbHelper.addTask(updatedTaskText); // Add updated task
                task.setText(updatedTaskText);
                adapter.notifyDataSetChanged(); // Refresh the ListView
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}