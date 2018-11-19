package pfe.terrain.gen.algo.biome;

import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.geometry.BordersSet;
import pfe.terrain.gen.algo.geometry.Edge;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.geometry.FaceSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BasicSquareBiomesTest {

    private BasicSquareBiomes biomeMapper;
    private IslandMap islandMap;

    private Face land = buildTestFace();
    private Face ocean = buildTestFace();

    private static Face buildTestFace() {
        return new Face(new Coordinate(0, 0),
                Collections.singletonList(
                        new Edge(new Coordinate(0, 0), new Coordinate(0, 0))
                ));
    }

    @Before
    public void setUp() throws Exception {
        biomeMapper = new BasicSquareBiomes();
        islandMap = new IslandMap();
        islandMap.putProperty(new Key<>("FACES", FaceSet.class), new FaceSet(Arrays.asList(land, ocean)));
        Set<Face> borderFaces = new HashSet<>();
        borderFaces.add(ocean);
        islandMap.putProperty(new Key<>("BORDERS", BordersSet.class), new BordersSet(new HashSet<>(), borderFaces));
    }

    @Test
    public void dumbSquareBiomesTest() throws Exception {
        biomeMapper.execute(islandMap);
        BiomeMap biomeMap = islandMap.getProperty(new Key<>("BIOMES", BiomeMap.class));
        assertThat(biomeMap.size(), is(2));
        assertTrue(biomeMap.containsKey(land));
        assertTrue(biomeMap.containsKey(ocean));
        assertThat(biomeMap.get(land), instanceOf(Desert.class));
        assertThat(biomeMap.get(ocean), instanceOf(Ocean.class));
    }


}
