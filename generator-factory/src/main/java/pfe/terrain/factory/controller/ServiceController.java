package pfe.terrain.factory.controller;

import pfe.terrain.factory.entities.Composition;
import pfe.terrain.factory.exception.CannotReachRepoException;
import pfe.terrain.factory.exception.CompositionAlreadyExistException;
import pfe.terrain.factory.exception.NoSuchAlgorithmException;
import pfe.terrain.factory.exception.NoSuchCompoException;
import pfe.terrain.factory.extern.ArtifactoryAlgoLister;
import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.pom.BasePom;
import pfe.terrain.factory.storage.CompoStorage;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceController {
    private Logger logger = Logger.getLogger("controller");

    private ArtifactoryAlgoLister lister;
    private List<Algorithm> algorithms;
    private CompoStorage storage;

    public ServiceController() {
        lister = new ArtifactoryAlgoLister();
        algorithms = new ArrayList<>();
        this.storage = new CompoStorage();
        try {
            this.getAlgoList();
        } catch (Exception e){
            logger.log(Level.WARNING,"cannot reach repo at init");
        }
    }

    public ServiceController(ArtifactoryAlgoLister lister){
        algorithms = new ArrayList<>();
        this.storage = new CompoStorage();
        this.lister = lister;
        try {
            this.getAlgoList();
        } catch (Exception e){
            logger.log(Level.WARNING,"cannot reach repo at init");
        }
    }

    public List<Algorithm> getAlgoList() throws IOException, CannotReachRepoException {
        if(algorithms.isEmpty()){
            this.algorithms = lister.getAlgo();
        }
        return this.algorithms;
    }

    public BasePom getGenerator(List<String> algos) throws NoSuchAlgorithmException,IOException, CannotReachRepoException {
        if(algorithms.isEmpty()){
            this.getAlgoList();
        }

        return pomFromAlgo(stringToAlgos(algos));
    }

    public List<Composition> getCompositions(){
        return this.storage.getCompositions();
    }

    public Composition addComposition(String name, List<String> algoList, String context) throws CompositionAlreadyExistException,NoSuchAlgorithmException{
        for(Composition composition : this.storage.getCompositions()){
            if(composition.getName().equals(name)){
                throw new CompositionAlreadyExistException();
            }
        }

        Composition composition  = new Composition(name,stringToAlgos(algoList),context);
        this.storage.addComposition(composition);
        return composition;
    }

    public BasePom getCompositionPom(String compoName) throws NoSuchCompoException{
        Composition composition = this.getCompoByName(compoName);

        return composition.getPom();
    }

    public String getCompositionContext(String compoName) throws NoSuchCompoException{
        Composition composition = this.getCompoByName(compoName);

        return composition.getContext();
    }

    private BasePom pomFromAlgo(List<Algorithm> algorithms){
        BasePom pom = new BasePom();

        for(Algorithm algo : algorithms){
            pom.addDependency(algo.toDependency());
        }

        return pom;
    }

    private List<Algorithm> stringToAlgos(List<String> strings) throws NoSuchAlgorithmException{
        List<Algorithm> requiredAlgorithms = new ArrayList<>();

        for(String algo : strings){
            Algorithm algorithm = new Algorithm(algo);
            if(this.algorithms.contains(algorithm)){
                requiredAlgorithms.add(algorithm);
                continue;
            }
            throw new NoSuchAlgorithmException(algo);
        }

        return requiredAlgorithms;
    }

    private Composition getCompoByName(String name) throws NoSuchCompoException{
        for(Composition compo : this.getCompositions()){
            if(compo.getName().equals(name)){
                return compo;
            }
        }
        throw new NoSuchCompoException();
    }
}
