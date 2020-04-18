package com.example.demo;

import javax.persistence.*;

@Entity // This tells Hibernate to make a table out of this class
//@Table(name = "jobs")
public class Job {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;
  private String status;
  private String name;
  private Integer timestarted;
  private String timefinished;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getTimeStarted() {
    return timestarted;
  }

  public void setTimeStarted(Integer timestarted) {
    this.timestarted = timestarted;
  }

  public String getTimeFinished() {
    return timefinished;
  }

  public void setTimeFinished(String timefinished) {
    this.timefinished = timefinished;
  }
}