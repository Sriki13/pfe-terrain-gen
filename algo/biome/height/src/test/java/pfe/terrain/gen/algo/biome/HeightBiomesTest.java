package pfe.terrain.gen.algo.biome;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.island.Biome;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.island.geometry.FaceSet;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class HeightBiomesTest {

    private IslandMap islandMap;
    private HeightBiomes biomes;

    private Face highFace;
    private Face lowFace;
    private Face lowFaceOnAverage;
    private Face oceanFace;
    private Face lakeFace;

    @Before
    public void setUp() throws Exception {
        biomes = new HeightBiomes();
        islandMap = new IslandMap();
        FaceSet allFaces = new FaceSet(new HashSet<>());
        highFace = new Face(new Coord(1, 2), new HashSet<>(Collections.singleton(
                new Edge(generateCoord(10, 0), generateCoord(10, 1))
        )));
        setAsLand(highFace);
        lowFace = new Face(new Coord(1, 3), new HashSet<>(Collections.singleton(
                new Edge(generateCoord(5, 0), generateCoord(5, 1))
        )));
        setAsLand(lowFace);
        lowFaceOnAverage = new Face(new Coord(1, 4), new HashSet<>(Arrays.asList(
                new Edge(generateCoord(0, 0), generateCoord(0, 1)),
                new Edge(generateCoord(10, 2), generateCoord(10, 3))
        )));
        setAsLand(lowFaceOnAverage);
        oceanFace = generateWaterFace(5, WaterKind.OCEAN);
        lakeFace = generateWaterFace(6, WaterKind.LAKE);
        allFaces.addAll(Arrays.asList(highFace, lowFace, lowFaceOnAverage, oceanFace, lakeFace));
        islandMap.putProperty(Contract.FACES, allFaces);
    }

    private Coord generateCoord(int z, int seed) throws Exception {
        Coord result = new Coord(seed, 0);
        result.putProperty(HeightBiomes.HEIGHT_KEY, new DoubleType(z));
        return result;
    }

    private void setAsLand(Face face) throws Exception {
        face.putProperty(HeightBiomes.WATER_KIND_KEY, WaterKind.NONE);
        face.putProperty(HeightBiomes.FACE_WATER_KEY, new BooleanType(false));
    }

    private Face generateWaterFace(int seed, WaterKind kind) throws Exception {
        Face result = new Face(new Coord(1, seed), new HashSet<>(Collections.singleton(
                new Edge(generateCoord(0, seed), generateCoord(0, seed))
        )));
        result.putProperty(HeightBiomes.WATER_KIND_KEY, kind);
        result.putProperty(HeightBiomes.FACE_WATER_KEY, new BooleanType(true));
        return result;
    }

    @Test
    public void biomeRepartitionTest() throws Exception {
        biomes.execute(islandMap, new Context());
        for (Face face : islandMap.getFaces()) {
            assertThat(getBiome(face), is(notNullValue()));
        }
        assertThat(getBiome(highFace), is(not(getBiome(lowFace))));
        assertThat(getBiome(lowFaceOnAverage), is(getBiome(lowFace)));
        assertThat(getBiome(oceanFace), is(Biome.OCEAN));
        assertThat(getBiome(lakeFace), is(Biome.LAKE));
    }

    private Biome getBiome(Face face) throws Exception {
        return face.getProperty(HeightBiomes.FACE_BIOME_KEY);
    }


}
