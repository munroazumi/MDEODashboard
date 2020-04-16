package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.*;
import java.util.*;
import java.text.DateFormat;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.ui.Model;
import com.example.demo.storage.StorageProperties;
import com.example.demo.storage.StorageService;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		NondominatedPopulation result = new Executor().withProblem("UF1").withAlgorithm("NSGAII")
				.withMaxEvaluations(10000).run();

		StringBuilder sb = new StringBuilder();
		sb.append("Objective1  Objective2\n");

		for (Solution solution : result) {
			sb.append(String.format("%.4f      %.4f%n", solution.getObjective(0), solution.getObjective(1)));
		}

		System.out.println(sb.toString());
		return sb.toString();
	}

	@PostMapping("/addjson")
	public String addJson() {
		UUID uuid = UUID.randomUUID();
		String batchID = uuid.toString();

		JSONObject jobDetails = new JSONObject();
        jobDetails.put("id", batchID);
        jobDetails.put("status", "running");
		jobDetails.put("name", "userinput");
		jobDetails.put("timeElapsed", "0 minutes");
		jobDetails.put("timeFinished", "willdo");
         
        //JSONObject jobObject = new JSONObject(); 
        //jobObject.put("job", jobDetails);
         
		//Add jobs to list
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader("src/main/resources/static/FakeData.json"))
        {
			Object obj = jsonParser.parse(reader);
			JSONArray jobList = (JSONArray) obj;
			jobList.add(jobDetails);
			//Write JSON file
			try (FileWriter file = new FileWriter("src/main/resources/static/FakeData.json")) {
 
				file.write(jobList.toJSONString());
				file.flush();
	 
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
		return "yessir";
	}

	@PostMapping("/run-job")
	public String runJob(
		@RequestParam(value = "moptProjectPath")
		final String moptProjectPath, 
		@RequestParam(value = "configuredMoptFilePath")
		final String configuredMoptFilePath, 
		@RequestParam(value = "batch", required = false)
		final Integer batch) {
		this._runJob(moptProjectPath, configuredMoptFilePath, batch);
		return "succeeded";
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
