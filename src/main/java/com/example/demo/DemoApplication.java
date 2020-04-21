package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.io.*;
import java.util.*;
import java.util.Date;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.nio.file.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.ui.Model;
import com.example.demo.storage.StorageProperties;
import com.example.demo.storage.StorageService;
import com.example.demo.storage.Find.Finder;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.sql.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.henshin.trace.TracePackage;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.moeaframework.Executor; //look into this
import org.moeaframework.Instrumenter;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import java.nio.file.Paths;
import uk.ac.kcl.inf.mdeoptimiser.languages.MoptStandaloneSetupGenerated;
import uk.ac.kcl.inf.mdeoptimiser.languages.mopt.Optimisation;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.OptimisationInterpreter;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.output.MDEOBatch;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.output.MDEOResultsOutput;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@RestController
public class DemoApplication {

	private static MoptStandaloneSetupGenerated moptStandaloneSetup = new MoptStandaloneSetupGenerated();
	private ResourceSet resourceSet = new ResourceSetImpl();

	public static void main(String[] args) {
		moptStandaloneSetup.createInjectorAndDoEMFRegistration();
		System.out.println(TracePackage.eINSTANCE.getName());
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}

	public Connection connectToSQL(Connection con) throws Exception {
		Properties connectionProps = new Properties();
    		connectionProps.put("user", "mdeo");
    		connectionProps.put("password", "asdf");
			String url = "jdbc:mysql://localhost:3306/MDEOProject";
			con = DriverManager.getConnection(url, connectionProps);
			return con;
	}
	
	@GetMapping(path = "/getjobs") //Querys MySQL database and gets all jobs. Used to populate ag-Grid in main.js
    public JSONArray getJobs() throws Exception {
    {
		JSONArray result = new JSONArray();
		Properties connectionProps = new Properties();
    		connectionProps.put("user", "mdeo");
    		connectionProps.put("password", "asdf");
			String url = "jdbc:mysql://localhost:3306/MDEOProject";
			Connection con = DriverManager.getConnection(url, connectionProps);
			String sql = "SELECT * FROM job;";
			System.out.println(sql);
			PreparedStatement posted = con.prepareStatement(sql);
			ResultSet rs = posted.executeQuery();
			while (rs.next()) {
				Integer id = rs.getInt("id");
				String status = rs.getString("status");
				String name = rs.getString("name");
				String timestarted = rs.getString("timeStarted");
				String timefinished = rs.getString("timeFinished");
				System.out.println(id + "\t" + status +
								   "\t" + name + "\t" + timestarted +
								   "\t" + timefinished);
			JSONObject jobDetails = new JSONObject();
			jobDetails.put("id", id);
        	jobDetails.put("status", status);
			jobDetails.put("name", name);
			jobDetails.put("timeStarted", timestarted);
			jobDetails.put("timeFinished", timefinished);
			result.add(jobDetails);
			}
		return result;
	}
	}
	
	@GetMapping(path = "/addtodatabase")
	public Integer post(@RequestParam("value1") String moptName) throws Exception {
		LocalDateTime startTime = LocalDateTime.now();
		final String var1 = "Running";
		final String var2 = moptName;
		final String var3 = startTime.toString();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("asdf");
			Properties connectionProps = new Properties();
    		connectionProps.put("user", "mdeo");
    		connectionProps.put("password", "asdf");
			String url = "jdbc:mysql://localhost:3306/MDEOProject";
			Connection con = DriverManager.getConnection(url, connectionProps);
			String generatedColumns[] = { "id" };
			String sql = "INSERT INTO job (status, name, timestarted) VALUES('"+var1+"', '"+var2+"', '"+var3+"')";
			System.out.println(sql);
			PreparedStatement posted = con.prepareStatement(sql, generatedColumns);
			posted.executeUpdate();
			try (ResultSet generatedKeys = posted.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				}
				else {
					throw new SQLException("Creating user failed, no ID obtained.");
				}
			}
		} catch(Exception e) {
			System.out.println(e);
		} finally {
			System.out.println("Inserted");
		}
		getJobs();
		return -1;
	}

	@PostMapping("/run-job")
	public String runJob(
		@RequestParam(value = "moptProjectPath")
		final String moptProjectPath, 
		@RequestParam(value = "configuredMoptFilePath")
		final String configuredMoptFilePath, 
		@RequestParam(value = "batch", required = false)
		final Integer batch) throws Exception {
			int endIndex = configuredMoptFilePath.indexOf(".");
			int beginIndex = configuredMoptFilePath.indexOf("java/") + 5;
			String moptName = configuredMoptFilePath.substring(beginIndex, endIndex);
			int id = this.post(moptName);
			if (id < 0) {
				return "bad";
			}
			this._runJob(moptProjectPath, configuredMoptFilePath, batch);
				Properties connectionProps = new Properties();
				connectionProps.put("user", "mdeo");
				connectionProps.put("password", "asdf");
				String url = "jdbc:mysql://localhost:3306/MDEOProject";
				Connection con = DriverManager.getConnection(url, connectionProps);
				LocalDateTime finishTime = LocalDateTime.now();
					String testsql = "SELECT timeStarted FROM job WHERE id = "+id;
					PreparedStatement queried = con.prepareStatement(testsql);
						System.out.println(testsql);
					ResultSet result = queried.executeQuery();
					result.next();
					String startTimeString = result.getString("timeStarted");
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					LocalDateTime startTime = LocalDateTime.parse(startTimeString, formatter);
					Duration dStep1 = Duration.between(startTime, finishTime);
					Long dStep2 = dStep1.toMinutes();
					String dStep3 = dStep2.toString();
					String dStep4 = dStep3.concat(" minutes)");
					String space = " (";
					String duration = space.concat(dStep4);
				String uglyFinishTime = finishTime.toString();
				String step1 = uglyFinishTime.replace("T", " ");
				String finishTimeString = step1.substring(0, 19);
				String finishAndDuration = finishTimeString.concat(duration);
				String sql = "UPDATE job SET status = 'Finished', timefinished = '"+finishAndDuration+"' WHERE id = "+id;
				System.out.println(sql);
				PreparedStatement posted = con.prepareStatement(sql);
				posted.executeUpdate();
			return "succeeded";
	}

	@PostMapping("/getresults")
	public JSONObject rowSelected(@RequestBody String resultFolder) throws Exception {
		String step1 = resultFolder.replaceAll("%22", "");
		String step2 = step1.replace("=", "");
		String folderName = step2.substring(0, step2.length() - 1);
		System.out.println(folderName);
		Finder finder = new Finder("experiment-data.*");
		Path path = java.nio.file.Paths.get("/Users/munroazumi/Desktop/IndividualProject/casestudies");
        Files.walkFileTree(path, finder);
		finder.done();
		String result = finder.findInArray(folderName);
		System.out.println(result);
		InputStream inputStream = new FileInputStream(result);
		JSONObject resultsJson = new JSONObject();
		JSONArray columnsArray = new JSONArray();
		resultsJson.put("columns", columnsArray);
		JSONArray rowsArray = new JSONArray();
		resultsJson.put("rows", rowsArray);
		CSVParser csvParser = CSVFormat.DEFAULT.parse(new InputStreamReader(inputStream));
		int index = 0;
			for (CSVRecord record : csvParser) {
				index++;
				JSONArray csvArray = columnsArray;
				if (index > 1) {
					csvArray = rowsArray;
				}
				for (int i = 0; i < record.size(); i++) {
					csvArray.add(record.get(i));
					System.out.println(record.get(i));
					}
			}
		return resultsJson;
	}

	private void _runJob(final String moptProjectPath, final String configuredMoptFilePath, final Integer batch) {
		if (((configuredMoptFilePath == null) || configuredMoptFilePath.isEmpty())) {
			throw new RuntimeException("empty configuredMoptFilePath");
		}
		final File moptFile = new File(configuredMoptFilePath);
		if (!moptFile.exists()) {
			throw new RuntimeException("configuredMoptFilePath doesn't exist");
		}

		final Resource resource = this.resourceSet.getResource(URI.createFileURI(moptFile.getAbsolutePath()), true);
		EObject _head = IterableExtensions.<EObject>head(resource.getContents());
		final Optimisation optimisationModel = ((Optimisation) _head);
		if ((optimisationModel == null)) {
			throw new RuntimeException("null optimisationModel");
		}

		final MDEOResultsOutput mdeoResultsOutput = new MDEOResultsOutput(new Date(), Paths.get(moptProjectPath),
				Paths.get(configuredMoptFilePath), optimisationModel);

		if (batch == null) {
			int experimentId = 0;
			do {
				mdeoResultsOutput.logBatch(
						this.runBatch(moptProjectPath, optimisationModel, Integer.valueOf(experimentId), false));
				experimentId++;
			} while ((experimentId < optimisationModel.getSolver().getAlgorithmBatches()));
		} else {
			mdeoResultsOutput.logBatch(this.runBatch(moptProjectPath, optimisationModel, batch, true));
		}

		mdeoResultsOutput.saveOutcome(batch);
	}

	private MDEOBatch runBatch(String moptProjectPath, Optimisation optimisationModel, Integer batch,
			boolean singleBatch) {
		OptimisationInterpreter optimisationInterpreter = new OptimisationInterpreter(moptProjectPath,
				optimisationModel);
		long startTime = System.nanoTime();
		Instrumenter optimisationOutcome = optimisationInterpreter.start();
		long endTime = System.nanoTime();
		long experimentDuration = (endTime - startTime) / 1000000L;
		Map<EPackage, List<org.eclipse.emf.henshin.model.Module>> generatedRules = optimisationInterpreter
				.getRulegenOperators();
		return new MDEOBatch(batch, experimentDuration, optimisationOutcome, generatedRules, singleBatch);
	}

}
