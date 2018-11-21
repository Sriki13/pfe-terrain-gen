package pfe.terrain.gen.algo.biome;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.Biome;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Edge;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.geometry.FaceSet;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BasicSquareBiomesTest {

    private BasicSquareBiomes biomeMapper;
    private IslandMap islandMap;

    private Face land = buildTestFace(0);
    private Face ocean = buildTestFace(1);

    private static Face buildTestFace(int seed) {
        return new Face(new Coord(seed, 0),
                Stream.of(
                        new Edge(new Coord(0, 0), new Coord(0, 0))
                ).collect(Collectors.toSet()));
    }

    @Before
    public void setUp() throws Exception {
        biomeMapper = new BasicSquareBiomes();
        islandMap = new IslandMap();
        islandMap.putProperty(new Key<>("FACES", FaceSet.class), new FaceSet(Arrays.asList(land, ocean)));
        ocean.putProperty(biomeMapper.faceBorderKey, new BooleanType(true));
        land.putProperty(biomeMapper.faceBorderKey, new BooleanType(false));
    }

    @Test
    public void dumbSquareBiomesTest() throws Exception {
        biomeMapper.execute(islandMap, new Context());
        assertThat(land.getProperty(biomeMapper.faceBiomeKey), is(Biome.SUB_TROPICAL_DESERT));
        assertThat(ocean.getProperty(biomeMapper.faceBiomeKey), is(Biome.OCEAN));
    }


}
