package pfe.terrain.generatorService;

import pfe.terrain.gen.algo.generator.Generator;

import java.io.File;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {

    public static void main(String[] args) throws Exception {
        GeneratorLoader generatorLoader = new GeneratorLoader();
        GeneratorRunner runner = new GeneratorRunner(generatorLoader.getGenerators());


        port(8080);
        get("/list", (request, response) -> {
            return runner.getGeneratorList();
        });

        get("/:id/execute",(request,response) -> {
            try {
                return runner.executeById(Integer.decode(request.params(":id")));
            } catch (Exception e){
                e.printStackTrace();
                return 2;
            }
        });

    }
}
