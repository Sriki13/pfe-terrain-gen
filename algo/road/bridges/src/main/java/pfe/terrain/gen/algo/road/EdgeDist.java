package pfe.terrain.gen.algo.road;

import pfe.terrain.gen.algo.island.geometry.Edge;

public class EdgeDist {

    private Edge edge;
    private Double length;

    EdgeDist(Edge edge, Double length) {
        this.edge = edge;
        this.length = length;
    }

    Edge getEdge() {
        return edge;
    }

    Double getLength() {
        return length;
    }
}
