package pfe.terrain.gen;

import com.flowpowered.noise.module.source.Perlin;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.algorithms.MoistureGenerator;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.geometry.FaceSet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PerlinMoisture extends MoistureGenerator {

    @Override
    public void execute(IslandMap map) throws DuplicateKeyException {
        FaceSet faces = map.getFaces();
        int mapSize = map.getSize();
        double frequency = 0.2; // should be a parameter
        Map<Face, Double> noiseValues = computeNoise(map.getSeed(), faces, mapSize, frequency);
        for (Face face : faces) {
            face.putProperty(faceMoisture, noiseValues.get(face));
        }
    }

    public Map<Face, Double> computeNoise(int seed, FaceSet faces, int mapSize, double frequency) {
        Perlin perlin = new Perlin();
        perlin.setSeed(seed);
        perlin.setFrequency(frequency * 9 + 1);
        //List<Face> faces = new ArrayList<>(map.getFaces());
        Map<Face, Double> noiseValue = new HashMap<>();
        for (Face face : faces) {
            Coord c = face.getCenter();
            noiseValue.put(face, perlin.getValue(c.x / mapSize, c.y / mapSize, 0));
        }
        double max = Collections.max(noiseValue.values());
        double min = Collections.min(noiseValue.values());
        noiseValue.replaceAll((key, val) -> (val - min) / (max - min));
        return noiseValue;
    }
}
