package pfe.terrain.gen.cave;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CaveFlooding extends Contract {

    static final Param<Double> FLOOD_PARAM = Param.generateDefaultDoubleParam(
            "floorFloodLevel", "How much water should be in the cave", 0.2, "Cave flood level"
    );

    private static final Key<BooleanType> VERTEX_WALL_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    private static final Key<MarkerType> FLOOD_KEY = new Key<>("hasFlood", MarkerType.class);

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(FLOOD_PARAM);
    }

    private static final Key<DoubleType> HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "CAVE_HEIGHT", "height", DoubleType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(VERTICES, VERTEX_WALL_KEY),
                asKeySet(FLOOD_KEY),
                asKeySet(HEIGHT_KEY)
        );
    }

    @Override
    public String getDescription() {
        return "Creates a random floor elevation";
    }

    private static final Comparator<Coord> HEIGHT_COMPARATOR =
            (a, b) -> (int) (1000 * (a.getProperty(HEIGHT_KEY).value - b.getProperty(HEIGHT_KEY).value));

    @Override
    public void execute(TerrainMap map, Context context) {
        map.putProperty(FLOOD_KEY, new MarkerType());
        List<Coord> allCoords = map.getProperty(VERTICES).stream()
                .filter(v -> !v.getProperty(VERTEX_WALL_KEY).value)
                .sorted(HEIGHT_COMPARATOR)
                .collect(Collectors.toList());
        double floodParam = context.getParamOrDefault(FLOOD_PARAM);
        double floodLevel = allCoords.get(
                (int) (floodParam * (allCoords.size() - 1)))
                .getProperty(HEIGHT_KEY).value;
        allCoords.forEach(coord ->
                coord.putProperty(HEIGHT_KEY, new DoubleType(
                        coord.getProperty(HEIGHT_KEY).value - floodLevel)));
    }

}
