package pfe.terrain.factory.controller;

import pfe.terrain.factory.compatibility.CompatibilityChecker;
import pfe.terrain.factory.entities.Composition;
import pfe.terrain.factory.exception.CannotReachRepoException;
import pfe.terrain.factory.exception.CompatibilityException;
import pfe.terrain.factory.exception.CompositionAlreadyExistException;
import pfe.terrain.factory.exception.NoSuchCompoException;
import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.pom.BasePom;
import pfe.terrain.factory.storage.AlgoStorage;
import pfe.terrain.factory.storage.CompatibilityStorage;
import pfe.terrain.factory.storage.CompoStorage;
import pfe.terrain.gen.DependencySolver;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.NotExecutableContract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.exception.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceController {
    private Logger logger = Logger.getLogger("controller");

    private AlgoStorage algoStorage;
    private CompoStorage compoStorage;
    private CompatibilityStorage compatStorage;

    public ServiceController() {
        this.compatStorage = new CompatibilityStorage();
        algoStorage = new AlgoStorage();
        this.compoStorage = new CompoStorage();
        try {
            this.getAlgoList();
        } catch (Exception e){
            logger.log(Level.WARNING,"cannot reach repo at init");
        }
    }

    public ServiceController(AlgoStorage algoStorage){
        this.algoStorage = algoStorage;
        this.compoStorage = new CompoStorage();
        this.compatStorage = new CompatibilityStorage();

    }

    public List<Algorithm> getAlgoList() throws IOException, CannotReachRepoException,Exception {
        return this.algoStorage.getAlgoList();
    }

    public BasePom getGenerator(List<String> algos) throws Exception {
        return pomFromAlgo(this.algoStorage.algosFromStrings(algos));
    }

    public List<Composition> getCompositions(){
        return this.compoStorage.getCompositions();
    }

    public Composition addComposition(String name, List<String> algoList, String context) throws Exception{
        for(Composition composition : this.compoStorage.getCompositions()){
            if(composition.getName().equals(name)){
                throw new CompositionAlreadyExistException();
            }
        }

        Composition composition  = new Composition(name,this.algoStorage.algosFromStrings(algoList),context);
        this.check(composition);
        this.compoStorage.addComposition(composition);
        return composition;
    }

    public BasePom getCompositionPom(String compoName) throws NoSuchCompoException{
        Composition composition = this.getCompoByName(compoName);

        return composition.getPom();
    }

    public Context getCompositionContext(String compoName) throws NoSuchCompoException{
        Composition composition = this.getCompoByName(compoName);

        return composition.getContext();
    }

    public void deleteComposition(String name) throws NoSuchCompoException{
        Composition composition = getCompoByName(name);

        this.compoStorage.removeComposition(composition);


    }

    private BasePom pomFromAlgo(List<Algorithm> algorithms){
        BasePom pom = new BasePom();

        for(Algorithm algo : algorithms){
            pom.addDependency(algo.toDependency());
        }

        return pom;
    }

    private Composition getCompoByName(String name) throws NoSuchCompoException{
        for(Composition compo : this.getCompositions()){
            if(compo.getName().equals(name)){
                return compo;
            }
        }
        throw new NoSuchCompoException();
    }

    private boolean check(Composition composition) throws InvalidContractException,UnsolvableException,MissingRequiredException,DuplicatedProductionException, MultipleEnderException, CompatibilityException {
        List<Contract> contracts = new ArrayList<>();

        for(Algorithm algorithm : composition.getAlgorithms()){
            contracts.add(algorithm.getContract());
        }

        new CompatibilityChecker(composition.getAlgorithms()).check();

        DependencySolver solver = new DependencySolver(contracts,contracts,new NotExecutableContract("final","final contract",new HashSet<>(),new Constraints(new HashSet<>(),new HashSet<>())));
        solver.orderContracts(composition.getConstraintsArray());

        return true;
    }
}
