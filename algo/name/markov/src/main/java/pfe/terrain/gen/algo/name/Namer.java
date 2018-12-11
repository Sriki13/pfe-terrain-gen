package pfe.terrain.gen.algo.name;


import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.OptionalKey;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.island.geometry.FaceSet;
import pfe.terrain.gen.algo.name.markov.MarkovNameGenerator;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.MarkerType;
import pfe.terrain.gen.algo.types.StringType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Namer extends Contract {
    private Logger logger = Logger.getLogger("namer");

    public static final Key<MarkerType> HAS_LAKES_KEY = new Key<>("LAKES", MarkerType.class);
    public static final Key<MarkerType> CITY_KEY = new SerializableKey<>(new OptionalKey<>(FACES_PREFIX + "HAS_CITY", MarkerType.class), "isCity");

    public static final Key<StringType> LAKE_NAME = new SerializableKey<>(new OptionalKey<>("LAKE_NAME", StringType.class), "name");
    public static final Key<StringType> CITY_NAME = new SerializableKey<>(new OptionalKey<>("CITY_NAME", StringType.class), "name");

    private MarkovNameGenerator nameGenerator;

    public Namer(){
        try {
            this.nameGenerator = new MarkovNameGenerator(4, 8);
        } catch (Exception e){
            logger.log(Level.WARNING,"cannot instantiate name generator");
        }
    }

    @Override
    public Constraints getContract() {
        return new Constraints(new HashSet<>(Arrays.asList(HAS_LAKES_KEY,CITY_KEY,FACES))
        ,new HashSet<>(Arrays.asList(CITY_NAME)));
    }

    @Override
    public String getDescription() {
        return "add name to the cities";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        FaceSet faces = map.getProperty(FACES);

        for(Face face : faces){
            if(face.hasProperty(CITY_KEY)){
                face.putProperty(CITY_NAME,new StringType(this.nameGenerator.getName()));
            }
        }
    }
}
