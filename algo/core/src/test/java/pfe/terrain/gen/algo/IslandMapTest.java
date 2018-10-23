package pfe.terrain.gen.algo;

import com.vividsolutions.jts.geom.Coordinate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.geometry.Face;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class IslandMapTest {
    private IslandMap map;

    @Before
    public void init(){
        map = new IslandMap();
        map.setSize(16);
    }

    @Test
    public void getFaceByCenterTest(){
        int x=8;
        int y = 9;

        
        Face face = new Face(new Coordinate(x,y),new ArrayList<>());

        Set Faces = new HashSet();
        Faces.add(face);

        map.setFaces(Faces);

        Assert.assertEquals(face,map.getFaceFromCenter(new Coordinate(x,y)));
    }
}
