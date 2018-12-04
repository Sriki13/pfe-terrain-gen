package pfe.terrain.gen.algo;

import org.junit.Assert;
import org.junit.Test;
import pfe.terrain.gen.algo.island.geometry.Coord;

public class CoordTest {

    @Test
    public void equalCoord(){
        Coord coord = new Coord(4,3);

        Coord second = new Coord(4,3);

        Assert.assertEquals(coord,second);
        Assert.assertEquals(coord.hashCode(),coord.hashCode());
    }

    @Test
    public void unequalCoord(){
        Coord coord = new Coord(8,3);

        Coord second = new Coord(4,3);

        Assert.assertNotEquals(coord,second);
    }
}
