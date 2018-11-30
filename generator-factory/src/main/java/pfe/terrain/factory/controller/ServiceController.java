package pfe.terrain.factory.controller;

import pfe.terrain.factory.exception.CannotReachRepoException;
import pfe.terrain.factory.exception.NoSuchAlgorithmException;
import pfe.terrain.factory.extern.ArtifactoryAlgoLister;
import pfe.terrain.factory.holder.Algorithm;
import pfe.terrain.factory.pom.BasePom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public BasePom getGenerator(List<String> algos) throws NoSuchAlgorithmException {
        List<Algorithm> requiredAlgorithms = new ArrayList<>();

        for(String algo : algos){
            requiredAlgorithms.add(new Algorithm(algo));
        }

        BasePom pom = new BasePom();

        for(Algorithm algo : requiredAlgorithms){
            if(!this.algorithms.contains(algo)){
                throw new NoSuchAlgorithmException(algo.getName());
            }

            pom.addDependency(algo.toDependency());
        }

        return pom;





    }
}
