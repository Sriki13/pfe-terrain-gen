package pfe.terrain.gen.algo;

import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class IslandMapTest {
    private IslandMap map;

    @Before
    public void init() {
        map = new IslandMap();
        map.setSize(16);
    }

    @Test
    @SuppressWarnings("Incompatible types.")
    public void testo() {
        map.putProperty(Property.POINTS, new HashSet<Coordinate>(), Set.class);
        Set<Coordinate> coordinates = map.getProperty(Property.POINTS, Set.class);
        coordinates.add(new Coordinate());
        map.putProperty(Property.POINTS, coordinates, Set.class);
        assertThat(map.getProperty(Property.POINTS, Set.class).size(), equalTo(1));
    }
}
