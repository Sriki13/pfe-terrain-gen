package pfe.terrain.gen.algo.biome;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.island.Biome;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.island.geometry.FaceSet;
import pfe.terrain.gen.algo.types.MarkerType;

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
    public void setUp() {
        biomeMapper = new BasicSquareBiomes();
        islandMap = new IslandMap();
        islandMap.putProperty(new Key<>("FACES", FaceSet.class), new FaceSet(Arrays.asList(land, ocean)));
        ocean.putProperty(biomeMapper.FACE_BORDER_KEY, new MarkerType());
    }

    @Test
    public void dumbSquareBiomesTest() {
        biomeMapper.execute(islandMap, new Context());
        assertThat(land.getProperty(biomeMapper.FACE_BIOME_KEY), is(Biome.SUB_TROPICAL_DESERT));
        assertThat(ocean.getProperty(biomeMapper.FACE_BIOME_KEY), is(Biome.OCEAN));
    }


}
