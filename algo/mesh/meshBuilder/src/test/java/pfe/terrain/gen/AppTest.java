package pfe.terrain.gen;

import org.junit.Test;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.CoordSet;

/**
 * Unit test for simple App.
 */
public class AppTest {
//    @Test
//    public void getFaceByCenterTest(){
//        int x=8;
//        int y = 9;
//
//
//        Face face = new Face(new Coordinate(x,y),new ArrayList<>());
//
//        Set Faces = new HashSet();
//        Faces.add(face);
//
//        map.setFaces(Faces);
//
//        Assert.assertEquals(face,map.getFaceFromCenter(new Coordinate(x,y)));
//    }

//    @Test
//    public void getCenterTest(){
//        Set<Face> faces = new HashSet<>();
//        Set<Coordinate> coords = new HashSet();
//
//        for(int i = 0;i<10;i++){
//            coords.add(new Coordinate(i,i));
//        }
//
//        for(Coordinate coord : coords){
//            faces.add(new Face(coord,new ArrayList<>()));
//        }
//
//        map.setFaces(faces);
//
//        for(Coordinate coord : map.getFacesCenters()){
//            Assert.assertTrue(coords.contains(coord));
//        }
//    }

    @Test
    public void meshTest() throws Exception {
        IslandMap map = new IslandMap();

        CoordSet points = new CoordSet();

        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                points.add(new Coord(i, j));
            }
        }
        map.putProperty(new Key<>("SIZE", Integer.class), 32);
        map.putProperty(new Key<>("POINTS", CoordSet.class), points);

        MeshBuilder builder = new MeshBuilder();

        builder.execute(map, new Context());
    }
}
