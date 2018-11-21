package pfe.terrain.gen.algo;

import org.junit.Assert;
import org.junit.Test;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Edge;

public class EdgeTest {


    @Test
    public void equalTest(){
        Edge edge = new Edge(new Coord(2,5),new Coord(15,10));

        Edge copy = new Edge(new Coord(15,10),new Coord(2,5));

        Assert.assertEquals(edge,copy);
        Assert.assertEquals(edge.hashCode(),copy.hashCode());
    }

    @Test
    public void unequalTest(){
        Edge edge = new Edge(new Coord(3,5),new Coord(15,10));

        Edge copy = new Edge(new Coord(15,10),new Coord(2,5));

        Assert.assertNotEquals(edge,copy);
    }
}
