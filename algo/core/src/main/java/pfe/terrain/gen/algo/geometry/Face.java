package pfe.terrain.gen.algo.geometry;

import com.vividsolutions.jts.geom.Coordinate;
import pfe.terrain.gen.algo.geometry.Edge;

import java.util.List;

public class Face {
    private Coordinate center;

    private List<Edge> edges;

    public Face(Coordinate center, List<Edge> edges) {
        this.center = center;
        this.edges = edges;
    }
}
