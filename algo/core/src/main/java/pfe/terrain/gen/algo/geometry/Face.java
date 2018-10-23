package pfe.terrain.gen.algo.geometry;

import com.vividsolutions.jts.geom.Coordinate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Face {
    private Coordinate center;

    private List<Edge> edges;

    private Set<Face> neighbors;

    public Face(Coordinate center, List<Edge> edges) {
        this.center = center;
        this.edges = edges;
        this.neighbors = new HashSet<>();
    }

    public void addNeighbor(Face face){
        neighbors.add(face);
        this.neighbors.remove(this);
    }

    public void setNeighbors(Set<Face> neighbors){
        this.neighbors = new HashSet<>(neighbors);
        this.neighbors.remove(this);
    }

    public Coordinate getCenter() {
        return center;
    }
}
