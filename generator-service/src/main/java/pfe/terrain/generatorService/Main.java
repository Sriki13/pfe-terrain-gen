package pfe.terrain.generatorService;

import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.generatorService.controller.ServiceController;
import pfe.terrain.generatorService.parser.JsonParser;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

public class Main {

    public static void main(String[] args) throws Exception {



        ServiceController controller = new ServiceController();

        port(8080);


        get("/execute", (request, response) -> {
            response.type("application/json");
            return controller.execute();
        });

        post("/context", (request, response) -> {
            response.type("application/json");

            controller.setContext(request.body());

            return "{\"status\" : \"OK\"}";
        });

        get("/parameters", (request, response) -> {
            response.type("application/json");
            JsonParser parser = new JsonParser();
            return parser.parseKeys(controller.getParameters());
        });
    }
}
