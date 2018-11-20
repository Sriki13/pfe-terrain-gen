package pfe.terrain.generatorService;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import pfe.terrain.gen.algo.generator.Generator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class GeneratorLoader {

    private List<String> files;
    private List<Generator> generators;

    public GeneratorLoader(){
        this.generators = new ArrayList<>();
        this.files = new ArrayList<>();

        this.generators = this.loadClassesFromClassPath();
    }

    private List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();

        try (
                InputStream in = GeneratorLoader.class.getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;

            while ((resource = br.readLine()) != null) {
                if(resource.contains(".jar")) {
                    filenames.add(resource);
                }
            }
        }

        return filenames;
    }

    public List<String> getFiles() {
        return files;
    }

    public List<Generator> getGenerators(){return generators;}

    private List<Generator> loadClassesFromClassPath(){
        Reflections reflections = new Reflections("pfe.terrain.gen", new SubTypesScanner(false));
        Set<Class<? extends Generator>> subTypes = reflections.getSubTypesOf(Generator.class);
        System.out.println(subTypes);
        List<Generator> gens = new ArrayList<>();

        for(Class cl : subTypes){
            try {
                gens.add((Generator) cl.newInstance());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return gens;
    }

}
