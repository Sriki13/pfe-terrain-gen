package pfe.terrain.gen.cave;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.OptionalKey;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.StringType;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class OreGenerator extends Contract {

    private static final Param<Double> ORE_DENSITY = Param.generateDefaultDoubleParam("oreDensity",
            "How much are there are in the walls (0 = not a lot, 1 = a lot)", 0.5, "Density of ore in the walls");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(ORE_DENSITY);
    }


    private static final Key<BooleanType> FACE_WALL_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    private static final Key<StringType> FACE_ORE_KEY =
            new SerializableKey<>(new OptionalKey<>(FACES_PREFIX + "CAVE_ORE", StringType.class), "ore");

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, SEED, FACE_WALL_KEY),
                asKeySet(FACE_ORE_KEY));
    }

    @Override
    public String getDescription() {
        return "Creates stalagmites at random in the tunnels of the cave";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        double oreDensity = context.getParamOrDefault(ORE_DENSITY) + 0.1;
        Set<Face> wallFaces = map.getProperty(FACES).stream().filter(f -> f.getProperty(FACE_WALL_KEY).value).collect(Collectors.toSet());
        Random random = new Random(map.getProperty(SEED));
        for (Face face : wallFaces) {
            for (Ore ore : Ore.values()) {
                if (random.nextDouble() < ore.getRarity() * oreDensity) {
                    placeandSpreadOre(face, ore.getId(), random, ore.getSpreading());
                    break;
                }
            }
        }
    }

    private void placeandSpreadOre(Face face, String oreName, Random random, double spreading) {
        for (Face neighbor : face.getNeighbors()) {
            if (random.nextDouble() < spreading) {
                placeandSpreadOre(neighbor, oreName, random, spreading / 2);
            }
        }
        face.putProperty(FACE_ORE_KEY, new StringType(oreName));
    }

}
