package pfe.terrain.gen;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
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
    private int id;
    private Context context;
    private String orderFilePath = "/order.json";

    public MapGenerator() {
        this.islandMap = new IslandMap();

        this.context = new Context();

        this.contracts = new ArrayList<>();
        this.getContractOrder();
        this.instantiateContracts();

    }

    public MapGenerator(List<Contract> contracts){
        this.contracts = contracts;
        this.islandMap = new IslandMap();
        this.context = new Context();
    }

    public MapGenerator(String jsonContext) throws WrongTypeException {
        this.islandMap = new IslandMap();

        this.contracts = new ArrayList<>();
        this.getContractOrder();
        this.instantiateContracts();

        ContextParser parser = new ContextParser(jsonContext);

        this.context = new MapContext(parser.getMap(), this.contracts);
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
    public int getId() {
        return id;
    }

    @Override
    public void setParams(Context map) {
        this.context = map;
    }

    @Override
    public List<Contract> getContracts() {
        return this.contracts;
    }

    private void getContractOrder() {
        try {
            StringBuilder result = new StringBuilder();
            InputStream stream = AppGen.class.getResourceAsStream(this.orderFilePath);
            Scanner scanner = new Scanner(stream);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();
            OrderParser parser = new OrderParser();
            this.orderedContracts = parser.getList(result.toString());
            this.id = result.toString().hashCode();
            this.orderedContracts.sort((o1, o2) -> {
                if (o1 == o2) return 0;
                if (o1.getOrder() > o2.getOrder()) return 1;
                else return -1;
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void instantiateContracts() {

        try {
            Reflections reflections = new Reflections("pfe.terrain.gen", new SubTypesScanner(false));
            Set<Class<? extends Contract>> subTypes = reflections.getSubTypesOf(Contract.class);

            for (Class cl : subTypes) {
                try {
                    contracts.add((Contract) cl.newInstance());
                } catch (InstantiationException e) {
                    System.err.println(cl.getName() + " was not instantiated");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
