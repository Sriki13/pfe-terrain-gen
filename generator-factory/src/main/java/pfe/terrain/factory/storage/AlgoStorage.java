package pfe.terrain.factory.storage;

import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.exception.NoSuchAlgorithmException;
import pfe.terrain.factory.extern.AlgoDataFetcher;
import pfe.terrain.factory.extern.ArtifactoryAlgoLister;
import pfe.terrain.factory.utils.Cache;
import pfe.terrain.factory.utils.Fetcher;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlgoStorage {
    private long defaultTimeout = 180000;
    private Cache<List<String>> algoList;
    private Map<String,Cache<Algorithm>> algoContractMap;

    public AlgoStorage(){
        this.algoList = new Cache<>(new ArtifactoryAlgoLister(),this.defaultTimeout);
        this.algoContractMap = new HashMap<>();
    }

    public List<Algorithm> getAlgoList() throws Exception{
        List<Algorithm> algorithms = new ArrayList<>();

        List<String> toRemove = new ArrayList<>();

        List<String> algorithmIds = this.algoList.get();
        for(String algorithmId : algorithmIds){
            if (!this.algoContractMap.containsKey(algorithmId)){
                this.algoContractMap.put(algorithmId,
                        new Cache<>(new AlgoDataFetcher(algorithmId),this.defaultTimeout));
            }
            try {
                algorithms.add(this.algoContractMap.get(algorithmId).get());
            } catch (Exception e){
                toRemove.add(algorithmId);
            }
        }

        for(String toRem : toRemove){
            this.algoList.get().remove(toRem);
            this.algoContractMap.remove(toRem);
        }

        return algorithms;
    }

    public List<Algorithm> algosFromStrings(List<String> algoIds) throws Exception{
        List<Algorithm> requiredAlgorithms = new ArrayList<>();
        for(String algo : algoIds){
            requiredAlgorithms.add(this.algoFromString(algo));
        }

        return requiredAlgorithms;
    }

    private Algorithm algoFromString(String algo) throws Exception{
        for(Algorithm algorithm : this.getAlgoList()) {
            if(algorithm.getName().equals(algo)){
                return algorithm;
            }
        }
        throw new NoSuchAlgorithmException(algo);
    }

}
