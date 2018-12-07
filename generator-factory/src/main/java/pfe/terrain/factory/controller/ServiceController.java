package pfe.terrain.factory.controller;

import pfe.terrain.factory.entities.Composition;
import pfe.terrain.factory.exception.CannotReachRepoException;
import pfe.terrain.factory.exception.CompositionAlreadyExistException;
import pfe.terrain.factory.exception.NoSuchCompoException;
import pfe.terrain.factory.extern.ArtifactoryAlgoLister;
import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.pom.BasePom;
import pfe.terrain.factory.storage.AlgoStorage;
import pfe.terrain.factory.storage.CompoStorage;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceController {
    private Logger logger = Logger.getLogger("controller");

    private AlgoStorage algoStorage;
    private CompoStorage compoStorage;

    public ServiceController() {
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
        this.compoStorage.addComposition(composition);
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
}
