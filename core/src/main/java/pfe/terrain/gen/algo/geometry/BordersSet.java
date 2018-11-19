package pfe.terrain.gen.algo.geometry;

import com.vividsolutions.jts.geom.Coordinate;

import java.util.Set;

public class BordersSet {

    private Set<Coordinate> borderVertices;
    private Set<Face> borderFaces;

    public BordersSet(Set<Coordinate> borderVertices, Set<Face> borderFaces) {
        this.borderVertices = borderVertices;
        this.borderFaces = borderFaces;
    }

    public Set<Coordinate> getBorderVertices() {
        return borderVertices;
    }

    public Set<Face> getBorderFaces() {
        return borderFaces;
    }
}
