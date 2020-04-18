package argumentation.model.generator;

import argumentation.scenario.generator.Scenario;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import org.dynemf.EObjectWrapper;
import org.dynemf.EPackageWrapper;
import org.dynemf.ResourceSetWrapper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class ScenarioModelGenerator {
  private Scenario scenario;
  
  private ResourceSetWrapper resourceSetWrapper;
  
  private EPackageWrapper metamodel;
  
  private EObjectWrapper<EObject> parsedScenario;
  
  public ScenarioModelGenerator(final Scenario scenario) {
    HenshinResourceSet henshinResourceSet = new HenshinResourceSet("src/main/resources/models/argumentation/generated/");
    EPackage loadedMetamodel = IterableExtensions.<EPackage>head(henshinResourceSet.registerDynamicEPackages("argumentation.ecore"));
    this.scenario = scenario;
    this.resourceSetWrapper = ResourceSetWrapper.rset().copyPackageRegistry(henshinResourceSet);
    this.metamodel = this.resourceSetWrapper.ePackage(loadedMetamodel.getNsURI());
  }
  
  public EObjectWrapper<EObject> getParsedScenario() {
    if ((this.parsedScenario == null)) {
      this.parsedScenario = this.parseScenario();
    }
    return this.parsedScenario;
  }
  
  private EObjectWrapper<EObject> parseScenario() {
    final EObjectWrapper<EObject> argumentationFramework = this.metamodel.create("ArgumentationFramework");
    final EObjectWrapper<EObject> persuader = this.metamodel.create("Persuader");
    argumentationFramework.set("hasPersuader", persuader);
    final HashMap<Argument, EObjectWrapper> argumentsMap = new HashMap<Argument, EObjectWrapper>();
    final EObjectWrapper topic = this.getEmfArgument(this.scenario.getTopic());
    argumentationFramework.add("hasArgument", topic);
    argumentationFramework.set("hasTopic", topic);
    final Consumer<Argument> _function = (Argument dungArgument) -> {
      EObjectWrapper<EObject> emfArgument = null;
      boolean _equals = dungArgument.getName().equals(this.scenario.getTopic().getName());
      if (_equals) {
        emfArgument = topic;
      } else {
        emfArgument = this.getEmfArgument(dungArgument);
      }
      argumentsMap.put(dungArgument, emfArgument);
    };
    this.scenario.getPersuadersFramework().forEach(_function);
    this.mapDungAttacksToEmf(this.scenario.getPersuadersFramework().getAttacks(), argumentsMap);
    final BiConsumer<Argument, EObjectWrapper> _function_1 = (Argument dungArgument, EObjectWrapper argument) -> {
      argumentationFramework.add("hasArgument", argument);
    };
    argumentsMap.forEach(_function_1);
    final Consumer<DungTheory> _function_2 = (DungTheory dungPersuadeeArgumentationFramework) -> {
      final EObjectWrapper<EObject> persuadeeArgumentationFramework = this.metamodel.create("PersuadeeArgumentationFramework");
      argumentationFramework.add("persuadeeArgumentationFramework", persuadeeArgumentationFramework);
      final EObjectWrapper<EObject> persuadee = this.metamodel.create("Persuadee");
      persuadeeArgumentationFramework.set("hasPersuadee", persuadee);
      final Consumer<Argument> _function_3 = (Argument dungArgument) -> {
        boolean _containsKey = argumentsMap.containsKey(dungArgument);
        boolean _not = (!_containsKey);
        if (_not) {
          InputOutput.<String>println(String.format("PF argument missing from PAF. Missing argument name: %s", dungArgument.getName()));
          argumentsMap.put(dungArgument, this.getEmfArgument(dungArgument));
        }
        persuadeeArgumentationFramework.add("hasArgument", argumentsMap.get(dungArgument));
      };
      dungPersuadeeArgumentationFramework.forEach(_function_3);
    };
    this.scenario.getPersuadeeArgumentationFrameworks().forEach(_function_2);
    return argumentationFramework;
  }
  
  public String generate(final String graphTypeName) {
    try {
      final Map<Object, Object> options = new HashMap<Object, Object>();
      options.put(XMIResource.OPTION_SCHEMA_LOCATION, Boolean.valueOf(true));
      String modelName = String.format("src/main/resources/models/argumentation/generated/PF-%s-%s-args-Audience-%s-Members-PAF-size-%s-args.xmi", graphTypeName, 
        Integer.valueOf(this.scenario.getPersuadersFramework().size()), 
        Integer.valueOf(this.scenario.getPersuadeeArgumentationFrameworks().size()), 
        Integer.valueOf(IterableExtensions.<DungTheory>head(this.scenario.getPersuadeeArgumentationFrameworks()).size()));
      ResourceSetWrapper.rset().create(modelName).add(this.getParsedScenario()).save(options);
      return modelName;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void mapDungAttacksToEmf(final Set<Attack> dungAttacks, final Map<Argument, EObjectWrapper> argumentsMap) {
    final Consumer<Attack> _function = (Attack attack) -> {
      EObjectWrapper attacker = argumentsMap.get(attack.getAttacker());
      EObjectWrapper attacked = argumentsMap.get(attack.getAttacked());
      attacker.add("attacks", attacked);
    };
    dungAttacks.forEach(_function);
  }
  
  public EObjectWrapper getEmfArgument(final Argument dungArgument) {
    EObjectWrapper<EObject> argument = this.metamodel.create("Argument");
    argument.set("name", dungArgument.getName());
    return argument;
  }
}
