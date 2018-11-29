package pfe.terrain.factory;

import pfe.terrain.factory.controller.ServiceController;
import pfe.terrain.factory.holder.Algorithm;
import pfe.terrain.factory.parser.JsonParser;
import spark.Spark;

import java.util.List;
import java.util.logging.Level;

import static spark.Spark.get;
import static spark.Spark.port;


public class Main {
    public static void main(String[] args) {
        ServiceController controller = new ServiceController();
        JsonParser parser = new JsonParser();

        port(9090);

        get("/algorithms", (request, response) -> {
            List<Algorithm> algos;
            try{
                algos = controller.getAlgoList();
            }catch (Exception e){
                return e.getMessage();
            }

            return parser.algoListToJson(algos);
        });
    }
}
