package models.argumentation.fitness;

import java.util.List;
import java.util.Map;
import models.argumentation.fitness.EcoreScenarioParser;
import net.sf.tweety.arg.dung.syntax.Argument;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.InputOutput;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.IGuidanceFunction;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.guidance.Solution;

@SuppressWarnings("all")
public class MinimiseArgumentsPutForward implements IGuidanceFunction {
  @Override
  public double computeFitness(final Solution solution) {
    EObject _model = solution.getModel();
    EcoreScenarioParser scenario = new EcoreScenarioParser(_model);
    Map<String, Argument> frameworkArguments = scenario.getArgumentationFrameworkArguments();
    List<Argument> argumentsPutForward = scenario.getArgumentsPutForward(frameworkArguments);
    return argumentsPutForward.size();
  }
  
  @Override
  public String getName() {
    return "Minimise persuader arguments put forward";
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
