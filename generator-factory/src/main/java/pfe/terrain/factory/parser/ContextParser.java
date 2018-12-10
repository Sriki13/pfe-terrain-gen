package pfe.terrain.factory.parser;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pfe.terrain.factory.exception.ContextParsingException;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.context.MapContext;
import pfe.terrain.gen.algo.exception.NotParsableContractException;
import pfe.terrain.gen.constraints.AdditionalConstraint;
import pfe.terrain.gen.parser.ConstraintParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContextParser {
    private Context context;
    private List<AdditionalConstraint> constraints;


    public ContextParser(String context, List<Contract> contracts) throws ContextParsingException{
        Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(gson.fromJson(context,Map.class),Map.class);

        if(!element.isJsonObject()){
            throw new ContextParsingException("wrong json format");
        }

        JsonObject obj = element.getAsJsonObject();

        if(obj.has("context")){
            try {
                Map<String, Object> map = gson.fromJson(obj.get("context"), Map.class);
                this.context = new MapContext(map, contracts);
            } catch (Exception e){
                throw new ContextParsingException("error in context definition");
            }
        } else {
            this.context = new MapContext();
        }

        if(obj.has("constraint")){
            try{
                JsonArray array = obj.getAsJsonArray("constraint");
                List<Map> maps = gson.fromJson(array,List.class);

                ConstraintParser constraintParser = new ConstraintParser();
                this.constraints = constraintParser.listToConstraints(maps,contracts);

            } catch (Exception e){
                throw new ContextParsingException("error in context definition");
            }
        } else {
            this.constraints = new ArrayList<>();
        }
    }

    public Context getContext() {
        return context;
    }

    public List<AdditionalConstraint> getConstraints() {
        return constraints;
    }
}
