package pfe.terrain.factory;

import com.google.gson.Gson;
import pfe.terrain.factory.controller.ServiceController;
import pfe.terrain.factory.parser.JsonParser;

import java.util.List;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;


public class Main {
    public static void main(String[] args) {
        ServiceController controller = new ServiceController();
        JsonParser parser = new JsonParser();

        port(9090);

        get("/algorithms", (request, response) -> {
            response.type("application/xml");

            try{
                return parser.algoListToJson(controller.getAlgoList());
            }catch (Exception e){
                return parser.exceptionToJson(e);
            }
        });

        post("/generator", (request,response) -> {
            Gson gson = new Gson();

            try{
                return controller.getGenerator(gson.fromJson(request.body(),List.class)).toString();
            } catch (Exception e){
                return parser.exceptionToJson(e);
            }
        });
    }
}
