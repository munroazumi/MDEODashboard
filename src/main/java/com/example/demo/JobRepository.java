package com.example.demo;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.Job;

// This will be AUTO IMPLEMENTED by Spring into a Bean called jobRepository
// CRUD refers Create, Read, Update, Delete

public interface JobRepository extends CrudRepository<Job, Integer> {

}