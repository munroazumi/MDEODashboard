package models.cra.parameters;

import java.util.List;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.evolvers.parameters.IEvolverParametersFunction;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.guidance.Solution;

@SuppressWarnings("all")
public class BooleanEvolverParameterTrue implements IEvolverParametersFunction {
  @Override
  public Object sample(final List<Solution> arg0) {
    return Boolean.valueOf(true);
  }
}
