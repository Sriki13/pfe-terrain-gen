package pfe.terrain.factory;

import com.google.gson.Gson;
import pfe.terrain.factory.controller.ServiceController;
import pfe.terrain.factory.holder.Algorithm;
import pfe.terrain.factory.parser.JsonParser;
import spark.Spark;

import java.util.List;
import java.util.logging.Level;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;


public class Main {
    public static void main(String[] args) {
        ServiceController controller = new ServiceController();
        JsonParser parser = new JsonParser();

        port(9090);

        get("/algorithms", (request, response) -> {
            response.type("application/json");

            List<Algorithm> algos;
            try{
                algos = controller.getAlgoList();
            }catch (Exception e){
                return parser.stringToJson(e.getMessage());
            }

            return parser.algoListToJson(algos);
        });

        post("/generator", (request,response) -> {
            Gson gson = new Gson();

            List<String> names = gson.fromJson(request.body(),List.class);
            try{
                return controller.getGenerator(names).toString();
            } catch (Exception e){
                return parser.stringToJson(e.getMessage());
            }
        });
    }
}
