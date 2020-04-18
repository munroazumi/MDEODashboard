package models.cra.fitness;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.IGuidanceFunction;

@SuppressWarnings("all")
public abstract class AbstractModelQueryFitnessFunction implements IGuidanceFunction {
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
  
  /**
   * Helper method returning true if the given EObject is an instance of the named EClass.
   */
  public boolean isOfClass(final EObject o, final String className) {
    return o.eClass().getName().equals(className);
  }
  
  /**
   * Helper method which returns the named encapsulated features for a class.
   */
  public Iterable<EObject> getClassFeatures(final EObject classObject, final String element) {
    Iterable<EObject> _xblockexpression = null;
    {
      final Object object = this.getFeature(classObject, "encapsulates");
      BasicEList<EObject> features = new BasicEList<EObject>();
      if ((object instanceof EObject)) {
        features.add(((EObject)object));
      } else {
        features = ((BasicEList<EObject>) object);
      }
      final Function1<EObject, Boolean> _function = (EObject feature) -> {
        return Boolean.valueOf(feature.eClass().getName().equals(element));
      };
      _xblockexpression = IterableExtensions.<EObject>filter(features, _function);
    }
    return _xblockexpression;
  }
}
