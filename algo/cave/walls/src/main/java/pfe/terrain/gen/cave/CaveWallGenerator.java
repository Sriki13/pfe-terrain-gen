package pfe.terrain.gen.cave;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Set;

public class CaveWallGenerator extends Contract {

    static final Param<Integer> WALL_HEIGHT_PARAM = new Param<>(
            "caveWallHeight", Integer.class, -100, 100, "The height of the cave walls", 30, "Cave wall height"
    );

    static final Param<Integer> FLOOR_HEIGHT_PARAM = new Param<>(
            "caveFloorHeight", Integer.class, -100, 100, "The height of the cave floor", 10, "Cave floor height"
    );

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(WALL_HEIGHT_PARAM, FLOOR_HEIGHT_PARAM);
    }

    static final Key<BooleanType> FACE_WALL_KEY =
            new Key<>(FACES_PREFIX + "IS_WALL", BooleanType.class);

    static final Key<BooleanType> VERTEX_WALL_KEY =
            new Key<>(VERTICES_PREFIX + "IS_WALL", BooleanType.class);

    static final Key<DoubleType> HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, FACE_WALL_KEY, VERTEX_WALL_KEY),
                asKeySet(HEIGHT_KEY)
        );
    }

    @Override
    public String getDescription() {
        return "Elevates cave walls";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        double wallHeight = context.getParamOrDefault(WALL_HEIGHT_PARAM);
        double floorHeight = context.getParamOrDefault(FLOOR_HEIGHT_PARAM);
        if (floorHeight >= wallHeight) {
            throw new InvalidAlgorithmParameters("Cave floor height (" + floorHeight + " ) " +
                    "cannot be above cave wall height (" + wallHeight + ")");
        }
        for (Face face : map.getProperty(FACES)) {
            double faceHeight = wallHeight;
            if (!face.getProperty(FACE_WALL_KEY).value) {
                long nbWalls = face.getAllVertices().stream()
                        .filter(coord -> coord.getProperty(VERTEX_WALL_KEY).value)
                        .count();
                if (nbWalls < face.getAllVertices().size() / 2) {
                    faceHeight = floorHeight;
                }
            }
            for (Coord vertex : face.getAllVertices()) {
                vertex.putProperty(HEIGHT_KEY, new DoubleType(faceHeight));
            }
        }
    }


}
