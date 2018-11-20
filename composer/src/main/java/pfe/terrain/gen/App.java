package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.gridcreator.GridPoints;
import pfe.terrain.gen.algo.gridcreator.RandomPoints;
import pfe.terrain.gen.algo.gridcreator.RelaxedPoints;
import pfe.terrain.gen.algo.parsing.OrderParser;
import pfe.terrain.gen.exception.InvalidContractException;
import pfe.terrain.gen.exception.MissingRequiredException;
import pfe.terrain.gen.exception.UnsolvableException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class App {

    public static void main(String[] args) throws IOException, InvalidContractException {
        App app = new App();
        File file = new File("target/lib");
        System.out.println(file.getCanonicalPath());
        app.setupGenerator();


    }

    private HashMap<Contract, String> nameToJar = new HashMap<>();

    private String addSuffixPrefix(String str) {
        return "algo." + str + "-1.0-SNAPSHOT.jar";
    }

    private List<Contract> available;
    private List<Contract> priority;

    public App() {
        Contract gridPoints = new GridPoints();
        Contract relaxedPoints = new RelaxedPoints();
        Contract randomPoints = new RandomPoints();
        Contract meshBuilder = new MeshBuilder();

        available = new ArrayList<>();
        available.add(gridPoints);
        available.add(relaxedPoints);
        available.add(randomPoints);
        available.add(meshBuilder);

        priority = new ArrayList<>();
        priority.add(gridPoints);
        priority.add(meshBuilder);

        nameToJar.put(gridPoints, addSuffixPrefix("gridcreator.grid"));
        nameToJar.put(randomPoints, addSuffixPrefix("gridcreator.random"));
        nameToJar.put(relaxedPoints, addSuffixPrefix("gridcreator.relaxed"));
        nameToJar.put(meshBuilder, addSuffixPrefix("mesh.builder"));
    }

    public List<Contract> getOrderedContract() throws InvalidContractException, UnsolvableException, MissingRequiredException {
        ChocoDependencySolver solver = new ChocoDependencySolver(this.available,this.priority,new FinalContract());
        return solver.orderContracts();
    }

    public void createJar(List<Contract> include) throws IOException {
        List<String> jars = include.stream()
                .map(item -> nameToJar.get(item))
                .collect(Collectors.toList());
        File lib = new File("../gen/lib");
        File[] contents = lib.listFiles();
        if (contents != null && contents.length > 0) {
            for (File file : contents) {
                Files.delete(file.toPath());
            }
        }
        for (String jar : jars) {
            Files.copy(Paths.get("target/lib/" + jar), Paths.get("../gen/lib/" + jar));
        }
    }

    private void createOrderTextFile(List<Contract> contracts) throws IOException{
        OrderParser parser = new OrderParser();
        String json = parser.writeList(contracts);

        File file = new File("../gen/src/main/resources/order.json");

        file.createNewFile();

        FileWriter writer = new FileWriter(file);
        writer.write(json);
        writer.close();
    }

    public void setupGenerator(){
        try{
            List<Contract> contracts = getOrderedContract();
            this.createJar(contracts);
            this.createOrderTextFile(contracts);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}

