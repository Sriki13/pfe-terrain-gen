package pfe.terrain.factory.controller;

import pfe.terrain.factory.exception.CannotReachRepoException;
import pfe.terrain.factory.exception.NoSuchAlgorithmException;
import pfe.terrain.factory.extern.ArtifactoryAlgoLister;
import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.pom.BasePom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServiceController {


    private ArtifactoryAlgoLister lister;
    private List<Algorithm> algorithms;

    public ServiceController() {
        lister = new ArtifactoryAlgoLister();
        algorithms = new ArrayList<>();
    }

    public ServiceController(ArtifactoryAlgoLister lister){
        this();
        this.lister = lister;
    }

    public List<Algorithm> getAlgoList() throws IOException, CannotReachRepoException {
        this.algorithms = lister.getAlgo();
        return this.algorithms;
    }

    public BasePom getGenerator(List<String> algos) throws NoSuchAlgorithmException,IOException, CannotReachRepoException {
        if(algorithms.isEmpty()){
            this.getAlgoList();
        }

        return pomFromAlgo(stringToAlgos(algos));
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
}
