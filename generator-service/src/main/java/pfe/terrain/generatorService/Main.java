package pfe.terrain.generatorService;

import pfe.terrain.generatorService.controller.ServiceController;
import pfe.terrain.generatorService.parser.JsonParser;

import java.util.logging.Level;
import java.util.logging.Logger;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) throws Exception {
        ServiceController controller = new ServiceController();
        JsonParser parser = new JsonParser();

        Logger logger = Logger.getLogger("WebService");


        port(8080);

        get("/execute", (request, response) -> {
            logger.log(Level.INFO,"Beginning map generation");
            long start = System.currentTimeMillis();

            response.type("application/json");
            String map = controller.execute();

            long time = System.currentTimeMillis() - start;
            logger.log(Level.INFO,"map generation done in : " + time + " ms");

            return map;
        });

        get("/graph", ((request, response) -> {
            logger.log(Level.INFO,"Serving graph");

            response.type("application/json");
            return controller.getGraph();
        }));

        post("/context", (request, response) -> {
            logger.log(Level.INFO,"Setting new context");

            response.type("application/json");
            return parser.parseMap(controller.setContext(request.body()));
        });

        get("/parameters", (request, response) -> {
            logger.log(Level.INFO,"Serving parameters");

            response.type("application/json");
            return parser.parseKeys(controller.getParameters());
        });

        get("/algorithms", (request, response) -> {
            logger.log(Level.INFO,"Serving algorithms list");

            response.type("application/json");
            return parser.parseAlgo(controller.getAlgoList());
        });
    }
}
