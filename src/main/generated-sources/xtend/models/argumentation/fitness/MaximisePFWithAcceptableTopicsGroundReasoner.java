package models.argumentation.fitness;

import models.argumentation.fitness.EcoreScenarioParser;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.InputOutput;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.IGuidanceFunction;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.guidance.Solution;

@SuppressWarnings("all")
public class MaximisePFWithAcceptableTopicsGroundReasoner implements IGuidanceFunction {
  @Override
  public double computeFitness(final Solution solution) {
    EObject _model = solution.getModel();
    EcoreScenarioParser acceptableTopicsCounter = new EcoreScenarioParser(_model);
    double fitness = acceptableTopicsCounter.countAcceptableTopicsGroundReasoner();
    return (fitness * (-1));
  }
  
  @Override
  public String getName() {
    return "Maximise Persuadee Frameworks With Acceptable Topics using Ground Reasoner";
  }
  
  /**
   * Helper function getting the value of the named feature (if it exists) for the given EObject.
   */
  public Object getFeature(final EObject o, final String feature) {
    Object _xblockexpression = null;
    {
      if ((o == null)) {
        InputOutput.<String>println("Null object given");
      }
      _xblockexpression = o.eGet(o.eClass().getEStructuralFeature(feature));
    }
    return _xblockexpression;
  }
}
