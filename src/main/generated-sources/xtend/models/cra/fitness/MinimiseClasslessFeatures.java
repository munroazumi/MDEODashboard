package models.cra.fitness;

import models.cra.fitness.AbstractModelQueryFitnessFunction;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.guidance.Solution;

@SuppressWarnings("all")
public class MinimiseClasslessFeatures extends AbstractModelQueryFitnessFunction {
  @Override
  public double computeFitness(final Solution solution) {
    Object _feature = this.getFeature(solution.getModel(), "features");
    final Function1<EObject, Boolean> _function = (EObject feature) -> {
      Object _feature_1 = this.getFeature(feature, "isEncapsulatedBy");
      return Boolean.valueOf((_feature_1 == null));
    };
    int fitness = IterableExtensions.size(IterableExtensions.<EObject>filter(((EList<EObject>) _feature), _function));
    InputOutput.<String>println(("Classless features: " + Integer.valueOf(fitness)));
    return fitness;
  }
  
  @Override
  public String getName() {
    return "Mimise classless features";
  }
}
