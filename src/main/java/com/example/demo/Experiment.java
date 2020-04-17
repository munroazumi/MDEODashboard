package com.example.demo;

import org.json.simple.JSONObject;

public class Experiment {

    private String id;
    private String status;
    private String name;
    private String timeElapsed;
    private String timeFinished;
    public static JSONObject job;

    public Experiment(String id, String status, String name, String timeElapsed, String timeFinished) {
        this.id = id;
        this.status = status;
        this.name = name;
        this.timeElapsed = timeElapsed;
        this.timeFinished = timeFinished;
    }
}