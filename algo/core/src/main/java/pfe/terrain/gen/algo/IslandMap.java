package pfe.terrain.gen.algo;

import com.vividsolutions.jts.geom.Coordinate;
import pfe.terrain.gen.algo.geometry.Edge;
import pfe.terrain.gen.algo.geometry.Face;

import java.util.HashSet;
import java.util.Set;

public class IslandMap {

    private int size;
    private Set<Coordinate> points;

    private Set<Coordinate> vertices;
    private Set<Edge> edges;
    private Set<Face> faces;

    public IslandMap() {
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Set<Coordinate> getPoints() {
        return points;
    }

    public void setPoints(Set<Coordinate> points) {
        this.points = points;
    }

    public Set<Coordinate> getVertices() {
        return vertices;
    }

    public void setVertices(Set<Coordinate> vertices) {
        this.vertices = vertices;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public void setEdges(Set<Edge> edges) {
        this.edges = edges;
    }

    public Set<Face> getFaces() {
        return faces;
    }

    public void setFaces(Set<Face> faces) {
        this.faces = faces;
    }

    public Face getFaceFromCenter(Coordinate center){
        for(Face face : faces){
            if(face.getCenter().equals(center)){
                return face;
            }
        }
        return null;

    }

    public Set<Coordinate> getFacesCenters(){
        Set<Coordinate> coords = new HashSet<>();

        for(Face face : faces){
            coords.add(face.getCenter());
        }

        return coords;
    }
}
