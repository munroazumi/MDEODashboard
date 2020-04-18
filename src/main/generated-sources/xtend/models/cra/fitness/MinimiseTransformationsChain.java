package models.cra.fitness;

import models.cra.fitness.AbstractModelQueryFitnessFunction;
import org.eclipse.xtext.xbase.lib.Conversions;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.guidance.Solution;

@SuppressWarnings("all")
public class MinimiseTransformationsChain extends AbstractModelQueryFitnessFunction {
  @Override
  public double computeFitness(final Solution model) {
    return ((Object[])Conversions.unwrapArray(model.getTransformationsChain(), Object.class)).length;
  }
  
  @Override
  public String getName() {
    return "Minimise Transformations Chain";
  }
}
