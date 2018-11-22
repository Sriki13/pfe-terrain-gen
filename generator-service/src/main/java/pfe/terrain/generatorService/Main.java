package pfe.terrain.generatorService;

import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.generatorService.parser.AnswerParser;

import java.io.File;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {

    public static void main(String[] args) throws Exception {
        GeneratorLoader generatorLoader = new GeneratorLoader();


        GeneratorRunner runner = new GeneratorRunner(generatorLoader.load());

        System.out.println("Generator loaded : ");

        for(Integer gen : runner.getGeneratorList()){
            System.out.println(gen);
        }

        port(8080);
        get("/list", (request, response) -> {
            response.type("application/json");
            return AnswerParser.intListToJson(runner.getGeneratorList());
        });

        get("/:id/execute",(request,response) -> {
            response.type("application/json");
            try {
                return runner.executeById(Integer.decode(request.params(":id")));
            } catch (Exception e){
                e.printStackTrace();
                return 2;
            }
        });

    }
}
