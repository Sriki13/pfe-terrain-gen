package pfe.terrain.generatorService.initializer;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.MapContext;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.parsing.ContextParser;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class ContextInitializer {
    private String contextPath;

    public ContextInitializer(String path){
        this.contextPath = path;
    }

    public ContextInitializer(){
        this.contextPath = this.getClass().getClassLoader().getResource("context.json").getFile();
    }

    public String getContextString(){
        try {
            FileReader reader = new FileReader(this.contextPath);

            Scanner scanner = new Scanner(reader);
            StringBuilder builder = new StringBuilder();

            while(scanner.hasNextLine()){
                builder.append(scanner.nextLine());
            }

            return builder.toString();

        } catch (Exception e){
            System.err.println("could not load context file, loading empty context");
            return "{}";
        }
    }

    public Context getContext(List<Contract> contracts){
        ContextParser parser = new ContextParser(this.getContextString());

        Context context = new MapContext(parser.getMap(),contracts);

        return context;
    }
}
