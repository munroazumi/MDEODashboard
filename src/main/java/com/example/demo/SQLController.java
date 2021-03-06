package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // This means that this class is a Controller
@RequestMapping(path="/demo") // This means URL's start with /demo (after Application path)
public class SQLController {
  @Autowired // This means to get the bean called jobRepository
         // Which is auto-generated by Spring, we will use it to handle the data
	private JobRepository jobRepository;

	@PostMapping(path="/add") // Map ONLY POST Requests
	public @ResponseBody String addNewJob (
		@RequestParam String name,
		@RequestParam String status,
		@RequestParam Integer timestarted,
		@RequestParam String timefinished) {
    // @ResponseBody means the returned String is the response, not a view name
    // @RequestParam means it is a parameter from the GET or POST request

    Job n = new Job();
    n.setStatus(status);
    n.setName(name);
    n.setTimeStarted(timestarted);
    n.setTimeFinished(timefinished);
    jobRepository.save(n);
    return "Saved";
  }

  @GetMapping(path="/all")
  public @ResponseBody Iterable<Job> getAllJobs() {
    // This returns a JSON or XML with the jobs
    return jobRepository.findAll();
  }
}