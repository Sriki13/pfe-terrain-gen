package pfe.terrain.gen.algo.island.geometry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static pfe.terrain.gen.algo.constraints.Contract.EDGES;

public class EdgeSet extends HashSet<Edge> {
    public EdgeSet() {
    }

    public Set<Coord> getConnectedVertices(Coord start) {
        Set<Coord> result = new HashSet<>();
        for (Edge edge : this) {
            if (edge.getStart() == start) {
                result.add(edge.getEnd());
            } else if (edge.getEnd() == start) {
                result.add(edge.getStart());
            }
        }
        return result;
    }

    public EdgeSet(Collection<Edge> c) {
        super(c);
    }
}
