package pfe.terrain.gen.algo.geometry;

import pfe.terrain.gen.algo.Mappable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Face extends Mappable {

    private Coord center;

    private List<Edge> edges;

    private Set<Face> neighbors;

    public Face(Coord center, List<Edge> edges) {
        super();
        this.center = center;
        this.edges = edges;
        this.neighbors = new HashSet<>();
    }

    public void addNeighbor(Face face){
        neighbors.add(face);
        this.neighbors.remove(this);
    }

    public void addNeighbor(Collection<Face> face){
        neighbors.addAll(face);
        this.neighbors.remove(this);
    }

    public void setNeighbors(Set<Face> neighbors){
        this.neighbors = new HashSet<>(neighbors);
        this.neighbors.remove(this);
    }

    public Coord getCenter() {
        return center;
    }

    public Set<Coord> getVertices() {
        Set<Coord> result = new HashSet<>();
        for (Edge edge : edges) {
            result.add(edge.getStart());
        }
        return result;
    }

    public Set<Face> getNeighbors() {
        return neighbors;
    }

    public List<Edge> getEdges() {
        return edges;
    }

}
