package models.argumentation.fitness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import org.eclipse.xtext.xbase.lib.DoubleExtensions;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.IGuidanceFunction;
import uk.ac.kcl.inf.mdeoptimiser.libraries.core.optimisation.interpreter.guidance.Solution;

@SuppressWarnings("all")
public class MaximiseHeuristicSolver implements IGuidanceFunction {
  @Override
  public double computeFitness(final Solution model) {
    return 0;
  }
  
  @Override
  public String getName() {
    return "Heuristic arguments guidance";
  }
  
  public HashMap<Argument, Double> getWeights(final DungTheory argumentationFramework, final Argument topic, final int depth, final double decayRate) {
    HashMap<Argument, Double> weights = this.initialiseMap(argumentationFramework);
    Double currentLevelWeight = new Double((-1));
    Set<Argument> _attackers = argumentationFramework.getAttackers(topic);
    HashSet<Argument> currentLevel = new HashSet<Argument>(_attackers);
    for (int i = 0; (i < depth); i++) {
      {
        HashSet<Argument> nextLevel = new HashSet<Argument>();
        for (final Argument a : currentLevel) {
          {
            Double argWeight = weights.get(a);
            Double _valueOf = Double.valueOf((argWeight).doubleValue());
            Double _valueOf_1 = Double.valueOf((currentLevelWeight).doubleValue());
            double _plus = DoubleExtensions.operator_plus(_valueOf, _valueOf_1);
            Double newWeight = new Double(_plus);
            weights.put(a, newWeight);
            nextLevel.addAll(argumentationFramework.getAttackers(a));
          }
        }
        Double _valueOf = Double.valueOf((currentLevelWeight).doubleValue());
        double _divide = ((_valueOf).doubleValue() / decayRate);
        double _multiply = (_divide * (-1.0));
        Double _double = new Double(_multiply);
        currentLevelWeight = _double;
        currentLevel = nextLevel;
      }
    }
    return weights;
  }
  
  public HashMap<Argument, Double> initialiseMap(final DungTheory argumentationFramework) {
    HashMap<Argument, Double> weights = new HashMap<Argument, Double>();
    Collection<Argument> _nodes = argumentationFramework.getNodes();
    ArrayList<Argument> args = new ArrayList<Argument>(_nodes);
    for (final Argument a : args) {
      weights.put(a, Double.valueOf(0.0));
    }
    return weights;
  }
}
