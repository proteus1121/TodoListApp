package com.example.myapplication;

import android.os.Bundle;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ArrayList<Task> tasks;
    private ArrayAdapter<Task> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
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
                    dbHelper.deleteTask(position + 1); // Assuming taskId corresponds to position + 1
                    tasks.remove(position);
                    adapter.notifyDataSetChanged(); // Refresh the ListView
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
                dbHelper.addTask(taskText);
                tasks.add(newTask);
                adapter.notifyDataSetChanged(); // Refresh the ListView
                editTextTask.setText(""); // Clear the EditText
            }
        });
    }

    private void loadTasks() {
        tasks.clear();
        for (String task : dbHelper.getAllTasks()) {
            tasks.add(new Task(task));
        }
        adapter.notifyDataSetChanged(); // Notify adapter of data change
    }

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
                dbHelper.deleteTask(position + 1); // Remove old task
                dbHelper.addTask(updatedTaskText); // Add updated task
                task.setText(updatedTaskText);
                adapter.notifyDataSetChanged(); // Refresh the ListView
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private static class Task {
        private String text;

        public Task(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}