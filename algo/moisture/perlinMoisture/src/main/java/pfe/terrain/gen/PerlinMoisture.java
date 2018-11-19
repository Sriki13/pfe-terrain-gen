package pfe.terrain.gen;

import com.flowpowered.noise.module.source.Perlin;
import com.vividsolutions.jts.geom.Coordinate;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.algorithms.MoistureGenerator;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.geometry.FaceSet;

public class PerlinMoisture implements MoistureGenerator {

    @Override
    public void execute(IslandMap map) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        FaceSet faces = map.getProperty(new Key<>("FACES", FaceSet.class));
        Perlin perlin = new Perlin();
        perlin.setPersistence(0.714);
        perlin.setOctaveCount(8);
        for (Face face : faces) {
            Coordinate c = face.getCenter();
            System.out.println(perlin.getValue(c.x, c.y, 0));
        }
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
}
