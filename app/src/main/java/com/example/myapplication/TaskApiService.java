package com.example.myapplication;

import com.example.myapplication.model.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface TaskApiService {

    @GET("task")
    Call<List<Task>> getTasks();  // Get all tasks from API

    @POST("task")
    Call<Task> addTask(@Body Task task);  // Add a new task to the API
}