package com.example.myapplication.model;

public class Task {
    private String id;
    private String text;

    public Task(String text) {
        this.text = text;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}