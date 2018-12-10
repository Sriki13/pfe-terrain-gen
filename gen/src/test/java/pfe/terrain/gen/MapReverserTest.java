package pfe.terrain.gen;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.island.geometry.FaceSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static pfe.terrain.gen.algo.constraints.Contract.FACES;
import static pfe.terrain.gen.algo.constraints.Contract.FACES_PREFIX;

public class MapReverserTest {

    private MapReverser mapReverser;
    private TerrainMap terrainMap;
    private FaceSet faces;

    private Key<Boolean> removedInMap = new Key<>("removedInMap", Boolean.class);
    private Key<Boolean> removedInFaces = new Key<>(FACES_PREFIX + "removedInFaces", Boolean.class);
    private Key<Boolean> unaffected = new Key<>("unaffected", Boolean.class);

    private Face generateFace(int seed) {
        Face face = new Face(new Coord(seed, 0), new HashSet<>());
        face.putProperty(removedInFaces, false);
        face.putProperty(unaffected, false);
        return face;
    }

    @Before
    public void setUp() {
        TestContract first = new TestContract("first", Collections.singletonList(removedInMap),
                new ArrayList<>());
        TestContract second = new TestContract("second", Collections.singletonList(removedInFaces),
                Collections.singletonList(removedInMap));
        terrainMap = new TerrainMap();
        terrainMap.putProperty(removedInMap, true);
        terrainMap.putProperty(unaffected, true);
        faces = new FaceSet(new HashSet<>(asList(generateFace(0), generateFace(1), generateFace(2))));
        terrainMap.putProperty(FACES, faces);
        mapReverser = new MapReverser(terrainMap, asList(first, second));
    }

    @Test
    public void removeKeysPropsTest() {
        mapReverser.reverseContracts();
        assertThat(terrainMap.hasProperty(removedInMap), is(false));
        assertThat(terrainMap.hasProperty(unaffected), is(true));
        for (Face face : faces) {
            assertThat(face.hasProperty(removedInFaces), is(false));
            assertThat(face.hasProperty(unaffected), is(true));
        }
    }

}
