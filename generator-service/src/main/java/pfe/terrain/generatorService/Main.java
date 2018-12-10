package pfe.terrain.generatorService;

import pfe.terrain.generatorService.controller.ServiceController;
import pfe.terrain.generatorService.parser.JsonParser;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) throws Exception {
        ServiceController controller = new ServiceController();
        JsonParser parser = new JsonParser();

        Logger logger = Logger.getLogger("WebService");

        Map<String, Object> baseContext = controller.getContextMap();

        logger.log(Level.INFO, "Base Context : " + parser.parseMap(baseContext));
        logger.log(Level.INFO, "Constraints: " + controller.getConstraintList());

        port(8080);

        get("/execute/:export", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");

            response.type("application/json");
            try {
                controller.execute();
                return controller.getProperty(request.params("export"));
            } catch (Exception e) {
                response.status(500);
                return parser.exceptionToJson(e);
            }
        });

        get("/executionChart", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");

            response.type("application/octet-stream");
            try {
                return controller.getExecutionChart();
            } catch (Exception e) {
                response.status(500);
                return parser.exceptionToJson(e);
            }
        });

        get("/graph", ((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");

            logger.log(Level.INFO, "Serving graph");

            response.type("application/json");
            try {
                return controller.getGraph();
            } catch (Exception e) {
                response.status(500);
                return parser.exceptionToJson(e);
            }
        }));

        post("/context", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");

            logger.log(Level.INFO, "Setting new context");

            response.type("application/json");

            try {
                return parser.parseMap(controller.setContext(request.body()));
            } catch (Exception e) {
                response.status(500);
                return parser.exceptionToJson(e);
            }
        });

        get("/parameters", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");

            logger.log(Level.INFO, "Serving parameters");

            response.type("application/json");


            try {
                return parser.parseKeys(controller.getParameters());
            } catch (Exception e) {
                response.status(500);
                return parser.exceptionToJson(e);
            }
        });

        get("/algorithms", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");

            logger.log(Level.INFO, "Serving algorithms list");

            response.type("application/json");
            try {
                return parser.parseAlgo(controller.getAlgoList());
            } catch (Exception e) {
                response.status(500);
                return parser.exceptionToJson(e);
            }


        });


        options("/context", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Headers", "*");

            logger.log(Level.INFO, "serving option context, not implemented");

            return "OK";
        });
    }
}
