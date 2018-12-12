package pfe.terrain.gen;

import com.google.gson.Gson;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.OptionalKey;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.island.geometry.FaceSet;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.StringType;

import java.util.*;

/**
 * Hello world!
 *
 */
public class Caver extends Contract {

    private Context context;

    public static final Key<BooleanType> FACE_WATER_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WATER", "isWater", BooleanType.class);

    public static final Key<StringType> FACE_CAVE_KEY =
            new SerializableKey<>(new OptionalKey<>(FACES_PREFIX + "CAVE",StringType.class),"cave");

    private static final Param<Double> SUPPRESSION_PERCENTAGE = Param.generateDefaultDoubleParam("smallCaveSuppressionPercentage",
            "How many of the small caves to delete, a very small portion (0.0) or all except one (1.0)", 0.7, "Small caves suppression percentage");
    private static final Param<Double> MULTIPLE_CAVES_TENDENCY = Param.generateDefaultDoubleParam("multipleCaveTendency",
             "Tendency of multiple caves to spawn, (0 = not a lot, 1.0 = max)", 0.5, "Number of caves");
    private static final Param<Double> CAVE_ROUGHNESS = Param.generateDefaultDoubleParam("caveRoughness",
             "Makes the borders of the walls appear more smooth (0.0) or rough (1.0)", 0.5, "cave walls roughness");
    private static final Param<Integer> WALL_HEIGHT_PARAM = new Param<>(
             "caveWallHeight", Integer.class, -100, 200, "The height of the cave walls", 100, "Cave wall height"
    );

    private static final Param<Integer> FLOOR_HEIGHT_PARAM = new Param<>(
            "caveFloorHeight", Integer.class, -100, 200, "The height of the cave floor", 10, "Cave floor height"
    );
    private static final Param<Double> FLOOR_ROUGHNESS_PARAM = Param.generateDefaultDoubleParam(
            "floorRoughness", "The roughness of the cave floor", 0.5, "Cave floor roughness"
    );
    private static final Param<Double> FLOOD_PARAM = Param.generateDefaultDoubleParam(
            "floorFloodLevel", "How much water should be in the cave", 0.2, "Cave flood level"
    );
    private static final Param<Double> VARIATION_PARAM = Param.generateDefaultDoubleParam("caveHeightVariation",
            "Defines the elevation variation of the cave from its borders to the center of its rooms and corridors",
            0.2, "Cave elevation");
    private static final Param<Double> MINSIZE_LINK = Param.generateDefaultDoubleParam("minSizeForLink",
            "How big a cave must be in order to be linked to the rest (0 = small, 1 = huge)", 0.2, "Minimum link for size");


    public static final Param<Integer> NB_CAVE = Param.generatePositiveIntegerParam("NB_CAVE", 20,"number of cave generated", 1,"number of cave");
    public Caver(){
        this.context = new Context();
        List<Param> params = Arrays.asList(MINSIZE_LINK,SUPPRESSION_PERCENTAGE,MULTIPLE_CAVES_TENDENCY,CAVE_ROUGHNESS,WALL_HEIGHT_PARAM,FLOOR_HEIGHT_PARAM,FLOOR_ROUGHNESS_PARAM,FLOOD_PARAM,VARIATION_PARAM);

        for(Param param : params){
            this.context.putParam(param,param.getDefaultValue());
        }
    }


    @Override
    public Constraints getContract() {
        return new Constraints(asKeySet(FACES,FACE_WATER_KEY),
                asKeySet(FACE_CAVE_KEY));
    }

    @Override
    public String getDescription() {
        return "place cave on the map with an associated context";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        int nbCave = context.getParamOrDefault(NB_CAVE);

        for(Face face : getFaces(nbCave,map.getProperty(FACES))){
            face.putProperty(FACE_CAVE_KEY,new StringType(contextToJson()));
        }

    }

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(NB_CAVE);
    }

    private Set<Face> getFaces(int nb, FaceSet set){
        Set<Face> faces = new HashSet<>();

        List<Face> faceList = new ArrayList<>(set);

        while(faces.size() < nb){
            Random rand = new Random();
            Face face = faceList.get(rand.nextInt(set.size()));
            if(!face.getProperty(FACE_WATER_KEY).value) {
                faces.add(face);
            }
        }

        return faces;
    }

    private String contextToJson(){
        Map<String,Object> map = new HashMap<>();

        for(Param param : this.context.getProperties().keySet()){
            map.put(param.getId(),this.context.getParamOrDefault(param));
        }

        return new Gson().toJson(map);
    }
}
