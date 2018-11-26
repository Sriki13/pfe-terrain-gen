package pfe.terrain.gen;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.MapContext;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.*;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.gen.algo.parsing.ContextParser;
import pfe.terrain.gen.algo.parsing.OrderParser;
import pfe.terrain.gen.algo.parsing.OrderedContract;
import pfe.terrain.gen.exception.MissingContractException;
import pfe.terrain.gen.export.JSONExporter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class MapGenerator implements Generator {

    private List<OrderedContract> orderedContracts;
    private List<Contract> contracts;
    private IslandMap islandMap;
    private Context context;

    public MapGenerator(List<Contract> contracts){
        this.contracts = contracts;
        this.islandMap = new IslandMap();
        this.context = new Context();
    }



    public String generate() {

        try {
            this.executeAll();
            JSONExporter exporter = new JSONExporter();
            return exporter.export(this.islandMap).toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }


    @Override
    public void setParams(Context map) {
        this.context = map;
    }

    @Override
    public List<Contract> getContracts() {
        return this.contracts;
    }

    private void executeAll() throws Exception {
        for (Contract ctr : contracts) {
            ctr.debugExecute(this.islandMap,this.context);
        }
    }

    public void execute(OrderedContract contract) throws Exception {
        this.executeByName(contract.getName());

    }

    private void executeByName(String name) throws MissingContractException, InvalidAlgorithmParameters, DuplicateKeyException, KeyTypeMismatch, NoSuchKeyException {
        for (Contract contract : contracts) {
            if (contract.getName().equals(name)) {
                contract.debugExecute(islandMap, this.context);
                return;
            }
        }
        throw new MissingContractException(name);
    }

}
