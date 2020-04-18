package models.argumentation.fitness;

import argumentation.scenario.generator.Scenario;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.GroundReasoner;
import net.sf.tweety.arg.dung.PreferredReasoner;
import net.sf.tweety.arg.dung.parser.FileFormat;
import net.sf.tweety.arg.dung.prover.ProboSolver;
import net.sf.tweety.arg.dung.semantics.Problem;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.dung.writer.DungWriter;
import net.sf.tweety.commons.util.Shell;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class EcoreScenarioParser {
  private EObject solutionModel = null;
  
  private Map<String, Argument> argumentationFrameworkArguments;
  
  private Argument topic;
  
  public EcoreScenarioParser(final EObject object) {
    this.solutionModel = object;
  }
  
  public Scenario getScenario() {
    Scenario _xblockexpression = null;
    {
      final ArrayList<Argument> arguments = new ArrayList<Argument>();
      final BiConsumer<String, Argument> _function = (String p1, Argument p2) -> {
        arguments.add(p2);
      };
      this.getArgumentationFrameworkArguments().forEach(_function);
      final ArrayList<Attack> attacks = new ArrayList<Attack>();
      final Consumer<Argument> _function_1 = (Argument argument) -> {
        attacks.addAll(this.getArgumentAttacks(argument, this.getArgumentationFrameworkArguments()));
      };
      arguments.forEach(_function_1);
      DungTheory persuaderFramework = new DungTheory();
      persuaderFramework.addAll(arguments);
      persuaderFramework.addAllAttacks(attacks);
      ArrayList<DungTheory> _persuadeeArgumentationFrameworksFromSolution = this.getPersuadeeArgumentationFrameworksFromSolution();
      Argument _topic = this.getTopic();
      _xblockexpression = new Scenario(persuaderFramework, _persuadeeArgumentationFrameworksFromSolution, _topic);
    }
    return _xblockexpression;
  }
  
  @Override
  public String toString() {
    Scenario scenario = this.getScenario();
    return scenario.toString();
  }
  
  public Map<String, Argument> getArgumentationFrameworkArguments() {
    if ((this.argumentationFrameworkArguments != null)) {
      return this.argumentationFrameworkArguments;
    }
    final HashMap<String, Argument> mappedArguments = new HashMap<String, Argument>();
    final List<EObject> frameworkArguments = this.getList(this.solutionModel, "hasArgument");
    final Consumer<EObject> _function = (EObject argument) -> {
      final String argumentName = this.getString(argument, "name");
      Argument _argument = new Argument(argumentName);
      mappedArguments.put(argumentName, _argument);
    };
    frameworkArguments.forEach(_function);
    this.argumentationFrameworkArguments = mappedArguments;
    return mappedArguments;
  }
  
  public List<Attack> getArgumentAttacks(final Argument attacker, final Map<String, Argument> frameworkArguments) {
    final ArrayList<Attack> attacks = new ArrayList<Attack>();
    final Function1<EObject, Boolean> _function = (EObject frameworkArgument) -> {
      return Boolean.valueOf(this.getString(frameworkArgument, "name").equals(attacker.getName()));
    };
    List<EObject> targets = this.getList(IterableExtensions.<EObject>head(IterableExtensions.<EObject>filter(this.getList(this.solutionModel, "hasArgument"), _function)), "attacks");
    final Consumer<EObject> _function_1 = (EObject target) -> {
      if ((target != null)) {
        Argument dungAttacker = attacker;
        Argument dungTarget = frameworkArguments.get(this.getString(target, "name"));
        Attack _attack = new Attack(dungAttacker, dungTarget);
        attacks.add(_attack);
      }
    };
    targets.forEach(_function_1);
    return attacks;
  }
  
  public Argument getTopic() {
    if ((this.topic == null)) {
      String topicName = this.getString(this.getObject(this.solutionModel, "hasTopic"), "name");
      this.topic = this.getArgumentationFrameworkArguments().get(topicName);
    }
    return this.topic;
  }
  
  public List<Argument> getArgumentsPutForward(final Map<String, Argument> frameworkArguments) {
    final ArrayList<Argument> dungArgumentsPutForward = new ArrayList<Argument>();
    EObject persuader = this.getObject(this.solutionModel, "hasPersuader");
    List<EObject> argumentsPutForward = this.getList(persuader, "putsForward");
    final Consumer<EObject> _function = (EObject argumentPutForward) -> {
      String argumentName = this.getString(argumentPutForward, "name");
      Argument argument = frameworkArguments.get(argumentName);
      boolean _contains = dungArgumentsPutForward.contains(argument);
      boolean _not = (!_contains);
      if (_not) {
        dungArgumentsPutForward.add(argument);
      }
    };
    argumentsPutForward.forEach(_function);
    return dungArgumentsPutForward;
  }
  
  public ArrayList<DungTheory> getPersuadeeArgumentationFrameworksFromSolution() {
    final ArrayList<DungTheory> persuadeeArgumentatonFrameworks = new ArrayList<DungTheory>();
    final Map<String, Argument> frameworkArguments = this.getArgumentationFrameworkArguments();
    final List<Argument> argumentsPutForward = this.getArgumentsPutForward(frameworkArguments);
    final Consumer<EObject> _function = (EObject persuadeeArgumentationFramework) -> {
      final ArrayList<Argument> persuadeeArgumentationFrameworkArguments = new ArrayList<Argument>();
      final ArrayList<Attack> persuadeeArgumentationFrameworkAttacks = new ArrayList<Attack>();
      final Consumer<EObject> _function_1 = (EObject pafArgument) -> {
        final Argument dungArgument = frameworkArguments.get(this.getString(pafArgument, "name"));
        persuadeeArgumentationFrameworkArguments.add(dungArgument);
      };
      this.getList(persuadeeArgumentationFramework, "hasArgument").forEach(_function_1);
      final Consumer<Argument> _function_2 = (Argument argument) -> {
        boolean _contains = persuadeeArgumentationFrameworkArguments.contains(argument);
        boolean _not = (!_contains);
        if (_not) {
          persuadeeArgumentationFrameworkArguments.add(argument);
        }
      };
      argumentsPutForward.forEach(_function_2);
      final Consumer<Argument> _function_3 = (Argument argument) -> {
        List<Attack> attacks = this.getArgumentAttacks(argument, frameworkArguments);
        final Consumer<Attack> _function_4 = (Attack attack) -> {
          if ((persuadeeArgumentationFrameworkArguments.contains(attack.getAttacker()) && 
            persuadeeArgumentationFrameworkArguments.contains(attack.getAttacked()))) {
            persuadeeArgumentationFrameworkAttacks.add(attack);
          }
        };
        attacks.forEach(_function_4);
      };
      persuadeeArgumentationFrameworkArguments.forEach(_function_3);
      final DungTheory dungTheoryGraph = new DungTheory();
      dungTheoryGraph.addAll(persuadeeArgumentationFrameworkArguments);
      dungTheoryGraph.addAllAttacks(persuadeeArgumentationFrameworkAttacks);
      dungTheoryGraph.add(this.getTopic());
      persuadeeArgumentatonFrameworks.add(dungTheoryGraph);
    };
    this.getList(this.solutionModel, "persuadeeArgumentationFramework").forEach(_function);
    return persuadeeArgumentatonFrameworks;
  }
  
  public double countAcceptableTopicsPreferredReasoner() {
    ArrayList<DungTheory> persuadeeArgumentationFrameworks = this.getPersuadeeArgumentationFrameworksFromSolution();
    final Argument topic = this.getTopic();
    final Function2<Double, DungTheory, Double> _function = (Double result, DungTheory pafGraph) -> {
      PreferredReasoner reasoner = new PreferredReasoner(pafGraph, Semantics.CREDULOUS_INFERENCE);
      boolean query = reasoner.query(topic).getAnswerBoolean();
      if (query) {
        return Double.valueOf(((result).doubleValue() + 1));
      }
      return result;
    };
    return (double) IterableExtensions.<DungTheory, Double>fold(persuadeeArgumentationFrameworks, Double.valueOf(0d), _function);
  }
  
  public double countAcceptableTopicsGroundReasoner() {
    ArrayList<DungTheory> persuadeeArgumentationFrameworks = this.getPersuadeeArgumentationFrameworksFromSolution();
    final Argument topic = this.getTopic();
    final Function2<Double, DungTheory, Double> _function = (Double result, DungTheory pafGraph) -> {
      GroundReasoner reasoner = new GroundReasoner(pafGraph);
      boolean query = reasoner.query(topic).getAnswerBoolean();
      if (query) {
        return Double.valueOf(((result).doubleValue() + 1));
      }
      return result;
    };
    return (double) IterableExtensions.<DungTheory, Double>fold(persuadeeArgumentationFrameworks, Double.valueOf(0d), _function);
  }
  
  public double countAcceptableTopicsEqArgSolver() {
    return this.countTopicsWithNativeSolver("eqargsolver");
  }
  
  public double countAcceptableTopicsArgMatSat() {
    return this.countTopicsWithNativeSolver("argmatsat");
  }
  
  public double countTopicsWithNativeSolver(final String solver) {
    ArrayList<DungTheory> persuadeeArgumentationFrameworks = this.getPersuadeeArgumentationFrameworksFromSolution();
    final Argument topic = this.getTopic();
    final Function2<Double, DungTheory, Double> _function = (Double result, DungTheory pafGraph) -> {
      Shell _nativeShell = Shell.getNativeShell();
      ProboSolver nativeSolver = new ProboSolver(solver, _nativeShell);
      boolean query = this.justify(nativeSolver, Problem.DC_PR, pafGraph, FileFormat.TGF, topic);
      if (query) {
        return Double.valueOf(((result).doubleValue() + 1));
      }
      return result;
    };
    return (double) IterableExtensions.<DungTheory, Double>fold(persuadeeArgumentationFrameworks, Double.valueOf(0d), _function);
  }
  
  public boolean justify(final ProboSolver solver, final Problem problem, final DungTheory aaf, final FileFormat format, final Argument arg) {
    try {
      boolean _isJustificationProblem = problem.isJustificationProblem();
      boolean _not = (!_isJustificationProblem);
      if (_not) {
        throw new RuntimeException((("Fail: " + problem) + "is not a justification problem."));
      }
      String _extension = format.extension();
      String _plus = ("." + _extension);
      final File temp = File.createTempFile("aaf-", _plus);
      final DungWriter writer = DungWriter.getWriter(format);
      writer.write(aaf, temp);
      final String str = solver.solve(problem, temp, format, (" -a " + arg));
      temp.delete();
      boolean _matches = Pattern.matches("\\s*YES\\s*", str);
      if (_matches) {
        return true;
      } else {
        boolean _matches_1 = Pattern.matches("\\s*NO\\s*", str);
        if (_matches_1) {
          return false;
        } else {
          throw new RuntimeException("Calling executable did not return useful output");
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  /**
   * Helper function getting the value of the named feature (if it exists) for the given EObject.
   */
  public Object getFeature(final EObject o, final String feature) {
    Object _xblockexpression = null;
    {
      if ((o == null)) {
        InputOutput.<String>println(String.format("Null object given %s", feature));
        return null;
      }
      _xblockexpression = o.eGet(o.eClass().getEStructuralFeature(feature));
    }
    return _xblockexpression;
  }
  
  public EObject getObject(final EObject o, final String feature) {
    Object _xblockexpression = null;
    {
      Object object = this.getFeature(o, feature);
      if ((object != null)) {
        return ((EObject) object);
      }
      _xblockexpression = null;
    }
    return ((EObject)_xblockexpression);
  }
  
  public List<EObject> getList(final EObject o, final String feature) {
    ArrayList<EObject> _xblockexpression = null;
    {
      Object object = this.getFeature(o, feature);
      if ((object != null)) {
        return ((EList<EObject>) object);
      }
      _xblockexpression = new ArrayList<EObject>();
    }
    return _xblockexpression;
  }
  
  public String getString(final EObject o, final String feature) {
    Object _xblockexpression = null;
    {
      Object object = this.getFeature(o, feature);
      if ((object != null)) {
        return ((String) object);
      }
      _xblockexpression = null;
    }
    return ((String)_xblockexpression);
  }
}
