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

import java.util.HashSet;
import java.util.Set;

public class CaveWallGenerator extends Contract {

    static final Param<Integer> WALL_HEIGHT_PARAM = new Param<>(
            "caveWallHeight", Integer.class, 60, 200, "The height of the cave walls", 70, "Cave wall height"
    );

    static final Param<Integer> FLOOR_HEIGHT_PARAM = new Param<>(
            "caveFloorHeight", Integer.class, 0, 150, "The height of the cave floor", 10, "Cave floor height"
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
            new SerializableKey<>(VERTICES_PREFIX + "CAVE_HEIGHT", "height", DoubleType.class);

    static final Key<Double> WALL_HEIGHT_KEY = new Key<>("WALL_HEIGHT", Double.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, FACE_WALL_KEY, VERTEX_WALL_KEY),
                asKeySet(HEIGHT_KEY, WALL_HEIGHT_KEY)
        );
    }

    @Override
    public String getDescription() {
        return "Elevates cave walls";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        double wallHeight = context.getParamOrDefault(WALL_HEIGHT_PARAM);
        map.putProperty(WALL_HEIGHT_KEY, wallHeight);
        double floorHeight = context.getParamOrDefault(FLOOR_HEIGHT_PARAM);
        if (floorHeight >= wallHeight) {
            throw new InvalidAlgorithmParameters("Cave floor height (" + floorHeight + " ) " +
                    "cannot be above cave wall height (" + wallHeight + ")");
        } else if (wallHeight - floorHeight < 10) {
            throw new InvalidAlgorithmParameters("Cave floor height (" + floorHeight + " ) " +
                    "is too close to cave wall height (" + wallHeight + "): minimum difference is 10");
        }
        Set<Face> emptyFaces = new HashSet<>();
        Set<Face> fullFaces = new HashSet<>();
        for (Face face : map.getProperty(FACES)) {
            if (face.getProperty(FACE_WALL_KEY).value) {
                fullFaces.add(face);
            } else {
                emptyFaces.add(face);
            }
        }
        attributeHeight(wallHeight, floorHeight, emptyFaces);
        attributeHeight(wallHeight, floorHeight, fullFaces);
    }

    private void attributeHeight(double wallHeight, double floorHeight, Set<Face> faces) {
        for (Face face : faces) {
            for (Coord vertex : face.getAllVertices()) {
                vertex.putProperty(HEIGHT_KEY, new DoubleType(face.getProperty(FACE_WALL_KEY).value ?
                        wallHeight : floorHeight));
            }
        }
    }

}
