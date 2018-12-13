package pfe.terrain.gen.cave;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.OptionalKey;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.Comparator;

public class LootPlacer extends Contract {

    private static final Key<BooleanType> FACE_WALL_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    private static final Key<MarkerType> FACE_LOOT =
            new SerializableKey<>(new OptionalKey<>(FACES_PREFIX + "HAS_LOOT", MarkerType.class), "loot");

    private static final Key<DoubleType> HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "CAVE_HEIGHT", "height", DoubleType.class);


    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(SIZE, FACES, FACE_WALL_KEY, HEIGHT_KEY),
                asKeySet(FACE_LOOT));
    }

    @Override
    public String getDescription() {
        return "Places a chest in a far away spot above water";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        map.getProperty(FACES).stream()
                .filter(face -> !face.getProperty(FACE_WALL_KEY).value
                        && !(face.getCenter().getProperty(HEIGHT_KEY).value < 0)
                        && face.getNeighbors().stream().noneMatch(f -> f.getProperty(FACE_WALL_KEY).value))
                .max(Comparator.comparingDouble(f -> f.centerDist(map.getProperty(SIZE) / 2)))
                .ifPresent(f -> f.putProperty(FACE_LOOT, new MarkerType()));
    }

}
