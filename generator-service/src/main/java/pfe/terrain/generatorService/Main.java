package pfe.terrain.generatorService;

import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.generatorService.controller.ServiceController;
import pfe.terrain.generatorService.parser.AnswerParser;

import java.io.File;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {

    public static void main(String[] args) throws Exception {
        ServiceController controller = new ServiceController();

        System.out.println("Generator loaded : ");

        System.out.println(controller.getGenList());

        port(8080);
        get("/list", (request, response) -> {
            response.type("application/json");
            return controller.getGenList();
        });

        get("/:id/execute",(request,response) -> {
            response.type("application/json");
            return controller.executeById(Integer.valueOf(request.params(":id")));
        });

    }
}
