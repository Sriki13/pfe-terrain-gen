package pfe.terrain.gen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class App {

    public static void main(String[] args) throws IOException {
        App app = new App();
        File file = new File("target/lib");
        System.out.println(file.getCanonicalPath());
        app.createJar(Collections.singletonList("RandomPoints"));
    }

    private HashMap<String, String> nameToJar = new HashMap<>();

    private String addSuffix(String str) {
        return str + "-1.0-SNAPSHOT.jar";
    }

    public App() {
        nameToJar.put("GridPoints", addSuffix("gridcreator.grid"));
        nameToJar.put("RandomPoints", addSuffix("gridcreator.random"));
        nameToJar.put("RelaxedPoints", addSuffix("gridcreator.relaxed"));
    }

    public void createJar(List<String> include) throws IOException {
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

}

