package pfe.terrain.generatorService;

import pfe.terrain.generatorService.controller.ServiceController;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) throws Exception {
        ServiceController controller = new ServiceController();
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

            controller.setContext(request.body());

            return "{\"status\" : \"OK\"}";
        });
    }
}
