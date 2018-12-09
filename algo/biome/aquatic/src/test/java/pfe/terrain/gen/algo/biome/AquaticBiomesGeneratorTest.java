package pfe.terrain.gen.algo.biome;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.island.AquaticBiome;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.island.geometry.FaceSet;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static pfe.terrain.gen.algo.constraints.Contract.FACES;
import static pfe.terrain.gen.algo.constraints.Contract.SEED;
import static pfe.terrain.gen.algo.island.AquaticBiome.*;

public class AquaticBiomesGeneratorTest {

    private AquaticBiomesGenerator biomes;
    private TerrainMap terrainMap;

    private Face land;
    private Face lake;

    private Face deepFace;
    private Face shallowFace;
    private Face middleFace;

    @Before
    public void setUp() {
        biomes = new AquaticBiomesGenerator();
        terrainMap = new TerrainMap();
        land = generateFace(0, 10, WaterKind.NONE);
        lake = generateFace(1, 5, WaterKind.LAKE);
        deepFace = generateFace(2, -1, WaterKind.OCEAN);
        shallowFace = generateFace(3, -0.05, WaterKind.OCEAN);
        middleFace = generateFace(4, -0.4, WaterKind.OCEAN);
        terrainMap.putProperty(SEED, 0);
        terrainMap.putProperty(FACES, new FaceSet(new HashSet<>(Arrays.asList(
                land, lake, deepFace, shallowFace, middleFace
        ))));
    }

    private Face generateFace(int seed, double depth, WaterKind kind) {
        Coord center = new Coord(seed, 0);
        center.putProperty(AquaticBiomesGenerator.HEIGHT_KEY, new DoubleType(depth));
        Face face = new Face(center, new HashSet<>());
        face.putProperty(AquaticBiomesGenerator.WATER_KIND_KEY, kind);
        face.putProperty(AquaticBiomesGenerator.IS_WATER_KEY, new BooleanType(kind != WaterKind.NONE));
        return face;
    }

    private AquaticBiome getBiome(Face face) {
        return face.getProperty(AquaticBiomesGenerator.AQUATIC_BIOME_KEY);
    }

    @Test
    public void assignBiomesCorrectly() {
        Context context = new Context();
        context.putParam(AquaticBiomesGenerator.NB_REEFS, 0);
        biomes.execute(terrainMap, context);
        assertThat(land.hasProperty(AquaticBiomesGenerator.AQUATIC_BIOME_KEY), is(false));
        assertThat(lake.hasProperty(AquaticBiomesGenerator.AQUATIC_BIOME_KEY), is(false));
        assertThat(getBiome(deepFace), is(DEEP_OCEAN));
        assertThat(getBiome(middleFace), is(OCEAN));
        assertThat(getBiome(shallowFace), is(SHALLOW_WATER));
    }

    private Context setupReef(int amount, int size) {
        List<Face> faces = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            faces.add(generateFace(i, -100, WaterKind.OCEAN));
        }
        for (int i = 0; i < 30; i++) {
            Face face = generateFace(1000 + i, -0.1, WaterKind.OCEAN);
            faces.add(face);
            if (i != 0) {
                faces.get(1000 + i - 1).addNeighbor(face);
                face.addNeighbor(faces.get(1000 + i - 1));
            }
        }
        terrainMap.putProperty(FACES, new FaceSet(new HashSet<>(faces)));
        Context context = new Context();
        context.putParam(AquaticBiomesGenerator.NB_REEFS, amount);
        context.putParam(AquaticBiomesGenerator.MAX_REEF_SIZE, size);
        return context;
    }

    private Set<Face> findReefFaces() {
        return terrainMap.getProperty(FACES).stream()
                .filter(face -> face.getProperty(AquaticBiomesGenerator.AQUATIC_BIOME_KEY) == CORAL_REEF)
                .collect(Collectors.toSet());
    }

    @Test
    public void smallReefGeneration() {
        biomes.execute(terrainMap, setupReef(10, 1));
        Set<Face> coralReefs = findReefFaces();
        assertThat(coralReefs.size(), is(10));
    }

    @Test
    public void oneLargeReefGeneration() {
        biomes.execute(terrainMap, setupReef(1, 10));
        Set<Face> coralReefs = findReefFaces();
        assertThat(coralReefs.size(), is(10));
        for (Face face : coralReefs) {
            Set<Face> neighborCoral = face.getNeighbors().stream()
                    .filter(n -> n.getProperty(AquaticBiomesGenerator.AQUATIC_BIOME_KEY) == CORAL_REEF)
                    .collect(Collectors.toSet());
            assertThat(neighborCoral.size(), greaterThan(0));
        }
    }


}
