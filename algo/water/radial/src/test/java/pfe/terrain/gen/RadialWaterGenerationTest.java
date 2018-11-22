package pfe.terrain.gen;

import org.junit.Test;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.SerializableKey;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.geometry.FaceSet;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.HashSet;

import static pfe.terrain.gen.algo.constraints.Contract.facesPrefix;

public class RadialWaterGenerationTest {

    private Key<BooleanType> faceWaterKey = new SerializableKey<>(facesPrefix + "IS_WATER", "isWater", BooleanType.class);

    @Test
    public void testo() throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        RadialWaterGeneration waterGen = new RadialWaterGeneration();
        IslandMap map = new IslandMap();
        FaceSet faces = new FaceSet();
        int mapSize = 1024;
        for (float i = 1; i < mapSize; i += 10) {
            for (float j = 1; j < mapSize; j += 10) {
                Face face = new Face(new Coord(i, j), new HashSet<>());
                faces.add(face);
            }
        }
        map.putProperty(new Key<>("FACES", FaceSet.class), faces);
        map.putProperty(new Key<>("SIZE", Integer.class), mapSize);
        map.putProperty(new Key<>("SEED", Integer.class), 0);
        waterGen.execute(map, new Context());
        faces = map.getFaces();
        for (Face face : faces) {
            System.out.println(face.getProperty(faceWaterKey).value);
        }
    }
}
