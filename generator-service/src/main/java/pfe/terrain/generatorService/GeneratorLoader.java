package pfe.terrain.generatorService;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.generatorService.exception.CannotUseGeneratorException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class GeneratorLoader {
    private String folderPath;

    public GeneratorLoader(String folderPath){
        this.folderPath = folderPath;


    }

    public GeneratorLoader(){
        this.folderPath = "./lib";


    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    private List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();

        File folder = new File(path);
        if(!folder.canRead()){
            throw new IOException();
        }

        for (final File fileEntry : folder.listFiles()) {
            if(!fileEntry.isDirectory() && fileEntry.getName().contains(".jar")){
                filenames.add(fileEntry.getCanonicalPath());
            }
        }


        return filenames;
    }

    public List<Generator> load() throws IOException{
        List<Generator> generators = new ArrayList<>();

        for(String path : this.getResourceFiles(this.folderPath)){
            try {
                Generator gen = new BashGenerator(path);
                generators.add(gen);
            } catch (CannotUseGeneratorException e){
                System.err.println("cannot load generator : " + path);
            }
        }

        return generators;
    }

}
