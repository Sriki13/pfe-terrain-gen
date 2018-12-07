package pfe.terrain.gen.algo.constraints;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.exception.NotParsableContractException;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.CoordSet;
import pfe.terrain.gen.algo.island.geometry.EdgeSet;
import pfe.terrain.gen.algo.island.geometry.FaceSet;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Contract implements Parameters {

    public static Set<Key> asKeySet(Key... params) {
        return Stream.of(params).collect(Collectors.toSet());
    }

    public static Set<Param> asParamSet(Param... params) {
        return Stream.of(params).collect(Collectors.toSet());
    }

    public static final String VERTICES_PREFIX = "VERTICES_";
    public static final String EDGES_PREFIX = "EDGES_";
    public static final String FACES_PREFIX = "FACES_";

    public static final Key<CoordSet> VERTICES = new Key<>("VERTICES", CoordSet.class);
    public static final Key<EdgeSet> EDGES = new Key<>("EDGES", EdgeSet.class);
    public static final Key<FaceSet> FACES = new Key<>("FACES", FaceSet.class);
    public static final Key<Integer> SIZE = new Key<>("SIZE", Integer.class);
    public static final Key<Integer> SEED = new Key<>("SEED", Integer.class);

    public abstract Constraints getContract();

    public long debugExecute(TerrainMap map, Context context) {
        String algorithmName = this.getClass().getSimpleName();
        Logger logger = Logger.getLogger(algorithmName);
        String titleCard = "-------------------------";
        logger.info(titleCard + " Executing algorithm " + algorithmName + " " + titleCard);
        long startTime = System.nanoTime();
        execute(map, context);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        logger.info("Done executing algorithm " + algorithmName + " in " + duration / 1000 + " microseconds");
        logger.info("\nVerifying contract...");
        for (Key key : getContract().getCreated()) {
            if (key.isOptional()) {
                continue;
            }
            logger.info("Verifying presence of key : " + key);
            if (!(map.assertContaining(key))) {
                logger.log(Level.SEVERE, "Unrespected contract for " + algorithmName);
                throw new NoSuchKeyException(key.getId());
            }
            logger.info(key.toString() + " is set");
        }
        logger.info(titleCard + " Execution and Verification of " + algorithmName + " done " + titleCard + "\n\n");
        return duration;
    }

    public abstract void execute(TerrainMap map, Context context);

    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Contract)) return false;
        return this.getName().equals(((Contract) obj).getName());
    }

    public static NotExecutableContract fromJson(String json) throws NotParsableContractException {
        Gson gson = new Gson();
        JsonElement rootElement;
        try {
            rootElement = gson.toJsonTree(gson.fromJson(json, Map.class), Map.class);
        } catch (Exception e){
            throw new NotParsableContractException("the given string is not Json");
        }

        if(!rootElement.isJsonObject()){
            throw new NotParsableContractException("wrong json format");
        }

        JsonObject root = rootElement.getAsJsonObject();

        String name;
        if(root.has("name")){
            try {
                name = root.get("name").getAsString();
            } catch (Exception e){
                throw new NotParsableContractException("name value is wrong");
            }
        }else {
            throw new NotParsableContractException("missing name");
        }

        Set<Param> parameters = new HashSet<>();
        if(root.has("parameters") && root.get("parameters").isJsonArray()){
            JsonArray paramArray = root.get("parameters").getAsJsonArray();

            for(JsonElement paramElement : paramArray){
                try{
                    JsonObject paramObj = paramElement.getAsJsonObject();

                    String label = paramObj.get("label").getAsString();
                    String description = paramObj.get("description").getAsString();
                    Class type = Class.forName(paramObj.get("type").getAsString());
                    String def = paramObj.get("default").getAsString();
                    String id = paramObj.get("id").getAsString();
                    String range = paramObj.get("range").getAsString();

                    parameters.add(new Param<>(id,type,range,description,def,label));
                } catch (Exception e){
                    throw new NotParsableContractException("param object is wrong");
                }
            }
        }else {
            throw new NotParsableContractException("missing parameters");
        }

        Constraints constraints;

        if(root.has("constraints") && root.get("constraints").isJsonObject()){
            JsonObject constObj = root.get("constraints").getAsJsonObject();
            try {
                Set<Key> required = keyFromString(constObj.getAsJsonArray("required"));
                Set<Key> created = keyFromString(constObj.getAsJsonArray("created"));
                Set<Key> modified = keyFromString(constObj.getAsJsonArray("modified"));
                constraints = new Constraints(required,created,modified);
            } catch (Exception e){
                throw new NotParsableContractException("missing keys in constraints");
            }
        }else {
            throw new NotParsableContractException("missing constraints");
        }





        return new NotExecutableContract(name,parameters,constraints);
    }

    public String toJson(){
        Map<String,Object> obj = new HashMap<>();
        obj.put("name",this.getName());

        Map<String,Object> constraints = new HashMap<>();

        List<String> requireds = new ArrayList<>();
        for(Key required : this.getContract().getRequired()){
            requireds.add(required.getId());
        }
        constraints.put("required",requireds);

        List<String> created = new ArrayList<>();
        for(Key create : this.getContract().getCreated()){
            created.add(create.getId());
        }
        constraints.put("created",created);

        List<String> modified = new ArrayList<>();
        for(Key modif : this.getContract().getModified()){
            modified.add(modif.getId());
        }
        constraints.put("modified",modified);

        obj.put("constraints",constraints);

        List<Object> parameters = new ArrayList<>();
        for(Param param : this.getRequestedParameters()){
            Map<String,Object> parameter = new HashMap<>();

            parameter.put("label",param.getLabel());
            parameter.put("description",param.getDescription());
            parameter.put("type",param.getType().getTypeName());
            parameter.put("default",param.getDefaultValue());
            parameter.put("id",param.getId());
            parameter.put("range",param.getRange());

            parameters.add(parameter);
        }

        obj.put("parameters",parameters);

        return new Gson().toJson(obj);
    }

    private static Set<Key> keyFromString(Iterable<JsonElement> keyStr){
        Set<Key> keys = new HashSet<>();

        for(JsonElement nameElement : keyStr ){
            Key<Void> key = new Key<>(nameElement.getAsString(),Void.class);
            keys.add(key);
        }

        return keys;
    }
}
