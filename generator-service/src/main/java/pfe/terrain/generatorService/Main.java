package pfe.terrain.generatorService;

import pfe.terrain.generatorService.controller.ServiceController;
import pfe.terrain.generatorService.parser.JsonParser;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) throws Exception {
        ServiceController controller = new ServiceController();
        JsonParser parser = new JsonParser();
        port(8080);

        get("/execute", (request, response) -> {
            response.type("application/json");
            return controller.execute();
        });

        get("/graph", ((request, response) -> {
            response.type("application/json");
            return controller.getGraph();
        }));

        post("/context", (request, response) -> {
            response.type("application/json");
            return parser.parseMap(controller.setContext(request.body()));
        });

        get("/parameters", (request, response) -> {
            response.type("application/json");
            return parser.parseKeys(controller.getParameters());
        });
    }
}
