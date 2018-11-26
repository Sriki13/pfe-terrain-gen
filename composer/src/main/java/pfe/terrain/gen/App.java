package pfe.terrain.gen;


import pfe.terrain.gen.algo.constraints.Contract;

import pfe.terrain.gen.algo.parsing.OrderParser;
import pfe.terrain.gen.exception.InvalidContractException;
import pfe.terrain.gen.exception.MissingRequiredException;
import pfe.terrain.gen.exception.NoSuchContractException;
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

    public static void main(String[] args) throws IOException, InvalidContractException, Exception {
        App app = new App();

        app.chooseAlgo(
        );
        app.setupGenerator();
    }

    private HashMap<Contract, String> nameToJar = new HashMap<>();

    private String addSuffixPrefix(String str) {
        return "algo." + str + "-1.0-SNAPSHOT.jar";
    }

    private List<Contract> available;
    private List<Contract> priority;
    private String destPath = "../gen/src/main/resources/order.json";
    private String jarPath = "target/lib/";
    private String jarDestPath = "../gen/lib/";

    public App() {
    }

    public List<Contract> getOrderedContract() throws InvalidContractException, UnsolvableException, MissingRequiredException {
        ChocoDependencySolver solver = new ChocoDependencySolver(this.available, this.priority, new FinalContract());
        return solver.orderContracts();
    }

    public void createJar(List<Contract> include, String jarPath, String jarDestPath) throws IOException {
        List<String> jars = include.stream()
                .map(item -> nameToJar.get(item))
                .collect(Collectors.toList());
        File lib = new File(jarDestPath);
        File[] contents = lib.listFiles();
        if (contents != null && contents.length > 0) {
            for (File file : contents) {
                Files.delete(file.toPath());
            }
        }
        for (String jar : jars) {
            if (jar.contains(".jar")) {
                Files.copy(Paths.get(jarPath + jar), Paths.get(jarDestPath + jar));
            }
        }
    }

    public void createOrderTextFile(List<Contract> contracts, String destPath) throws IOException {
        OrderParser parser = new OrderParser();
        String json = parser.writeList(contracts);

        File file = new File(destPath);

        file.createNewFile();

        FileWriter writer = new FileWriter(file);
        writer.write(json);
        writer.close();
    }

    public void setupGenerator() {
        try {
            List<Contract> contracts = getOrderedContract();
            this.createJar(contracts, this.jarPath, this.jarDestPath);
            this.createOrderTextFile(contracts, this.destPath);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public String getAlgoList() {
        StringBuilder builder = new StringBuilder();

        for (Contract ctr : available) {
            builder.append(ctr.getName());
            builder.append("\n");
        }

        return builder.toString();
    }

    public void chooseAlgo(String... algos) throws NoSuchContractException {
        for (String name : algos) {
            this.priority.add(findContractByName(name));
        }
    }

    private Contract findContractByName(String name) throws NoSuchContractException {
        for (Contract ctr : available) {
            if (ctr.getName().equals(name)) {
                return ctr;
            }
        }
        throw new NoSuchContractException();
    }
}

