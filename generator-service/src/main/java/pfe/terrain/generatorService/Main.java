package pfe.terrain.generatorService;

import pfe.terrain.gen.MapGenerator;
import pfe.terrain.generatorService.controller.ServiceController;
import pfe.terrain.generatorService.parser.JsonParser;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getLogger("WebService");

        int port = 8080;
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
                logger.log(Level.INFO, "using port : " + port);
            } catch (Exception e) {
                logger.log(Level.INFO, "cannot read port, falling back to default : " + port);
            }
        }

        ServiceController controller = new ServiceController();
        JsonParser parser = new JsonParser();

        Map<String, Object> baseContext = controller.getContextMap();

        logger.log(Level.INFO, "Base Context : " + parser.parseMap(baseContext));
        logger.log(Level.INFO, "Constraints: " + controller.getConstraintList());

        port(port);

        get("/execute", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.type("application/json");
            try {
                controller.execute();
                return "";
            } catch (Exception e) {
                response.status(500);
                return parser.exceptionToJson(e);
            }
        });

        get("/execute/:export", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            try {
                controller.execute();
                String property = request.params("export");
                if (property == null) {
                    return "No property specified";
                }
                response.type(controller.getResponseType(property));
                logger.info("\n" + MapGenerator.SEPARATOR + controller.getProperty(property).toString().length() / 1000 + " KB\n" + MapGenerator.SEPARATOR);
                return controller.getProperty(property);
            } catch (Exception e) {
                response.status(500);
                return parser.exceptionToJson(e);
            }
        });

        get("/property/:export", ((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            try {
                String property = request.params("export");
                if (property == null) {
                    response.status(400);
                    return "No property specified";
                }
                response.type(controller.getResponseType(property));
                return controller.getProperty(property);
            } catch (Exception e) {
                response.status(500);
                return parser.exceptionToJson(e);
            }
        }));

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

            logger.log(Level.INFO, "Serving pfe.terrain.generatorService.graph");

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
