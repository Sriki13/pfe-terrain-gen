package pfe.terrain.gen.cave;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class StalagmitePlacer extends Contract {

    private static final Param<Integer> NB_STALAGMITE = Param.generatePositiveIntegerParam("numberOfStalagmites",
            100, "How many stalagmites there are in the cave", 10, "Number of Stalagmites");

    private static final Param<Double> STALAGMITE_HEIGHT = Param.generateDefaultDoubleParam("heightOfStalagmites",
            "How tall are the stalagmites (0= short, 1=almost to the ceiling)", 0.5, "Height of the Stalagmites");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(NB_STALAGMITE, STALAGMITE_HEIGHT);
    }

    private static final Key<BooleanType> FACE_WALL_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    private static final Key<DoubleType> HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "CAVE_HEIGHT", "height", DoubleType.class);

    private static final Key<Double> WALL_HEIGHT_KEY = new Key<>("WALL_HEIGHT", Double.class);

    private static final Key<MarkerType> FLOOR_KEY = new Key<>("FLOOR_CAVE", MarkerType.class);


    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, SEED, FACE_WALL_KEY, WALL_HEIGHT_KEY, FLOOR_KEY),
                asKeySet(),
                asKeySet(HEIGHT_KEY));
    }

    @Override
    public String getDescription() {
        return "Creates stalagmites at random in the tunnels of the cave";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        int nbStalagmites = context.getParamOrDefault(NB_STALAGMITE);
        double heightStalagmite = context.getParamOrDefault(STALAGMITE_HEIGHT);
        Random random = new Random(map.getProperty(SEED));
        double wHeight = map.getProperty(WALL_HEIGHT_KEY);
        List<Face> tunnelFaces = map.getProperty(FACES).stream().filter(
                f -> !f.getProperty(FACE_WALL_KEY).value && f.getNeighbors().stream().noneMatch(n -> n.getProperty(FACE_WALL_KEY).value)).collect(Collectors.toList()
        );
        Set<Integer> ids = random.ints(nbStalagmites, 0, tunnelFaces.size() - 1).boxed().collect(Collectors.toSet());
        ids.forEach(id -> stalagmitify(tunnelFaces.get(id), random, wHeight, heightStalagmite / 2));
    }

    private void stalagmitify(Face face, Random random, double wHeight, double sHeight) {
        double cHeight = face.getCenter().getProperty(HEIGHT_KEY).value;
        double dHeight = wHeight - cHeight;
        cHeight += dHeight * (random.nextDouble() * (sHeight + 0.7));
        face.getCenter().putProperty(HEIGHT_KEY, new DoubleType(cHeight));
    }

}
