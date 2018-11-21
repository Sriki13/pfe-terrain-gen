package pfe.terrain.gen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.geometry.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private IslandMap map;

    @Before
    public void initMapWithGrid() throws Exception {
        this.map = new IslandMap();

        CoordSet points = new CoordSet();

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                points.add(new Coord(i, j));
            }
        }
        map.putProperty(new Key<>("SIZE", Integer.class), 20);
        map.putProperty(new Key<>("POINTS", CoordSet.class), points);
    }

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
    public void EdgeVerticesTest() throws Exception {


        MeshBuilder builder = new MeshBuilder();
        builder.execute(map, new Context());

        CoordSet vertices = map.getVertices();
        EdgeSet edges = map.getEdges();

        CoordSet verticesEdge = new CoordSet();

        for (Edge edge : edges) {
            verticesEdge.add(edge.getEnd());
            verticesEdge.add(edge.getStart());
        }

        assertTrue(vertices.containsAll(verticesEdge));
    }

    @Test
    public void FaceVerticesTest() throws Exception {
        MeshBuilder builder = new MeshBuilder();
        builder.execute(map, new Context());

        FaceSet faces = map.getFaces();

        CoordSet verticesFace = new CoordSet();
        EdgeSet edgesFace = new EdgeSet();

        for (Face face : faces) {
            verticesFace.add(face.getCenter());
            verticesFace.addAll(face.getVertices());
            edgesFace.addAll(face.getEdges());
        }

        assertTrue(map.getVertices().containsAll(verticesFace));
        assertTrue(map.getEdges().containsAll(edgesFace));
    }

    @Test
    public void samePointsEdgeTest() throws Exception {
        MeshBuilder builder = new MeshBuilder();
        builder.execute(map, new Context());

        EdgeSet edges = map.getEdges();

        for (Edge edge : edges) {
            Assert.assertNotEquals("Should be different " + edge.getEnd() + " " + edge.getStart(), edge.getEnd(), edge.getStart());
        }
    }

    @Test
    public void sameEdgeDifferentWay() throws Exception {
        MeshBuilder builder = new MeshBuilder();
        builder.execute(map, new Context());

        EdgeSet edges = map.getEdges();

        for (Edge edge1 : edges) {
            for (Edge edge2 : edges) {
                if (!(edge1.equals(edge2))) {
                    assertFalse((edge1.getEnd().equals(edge2.getStart()))
                            && (edge1.getStart().equals(edge2.getEnd())));
                }
            }
        }
    }


}
