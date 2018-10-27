package pfe.terrain.gen.algo;

import com.vividsolutions.jts.geom.Coordinate;
import pfe.terrain.gen.algo.geometry.Edge;

import java.util.Set;

public enum Property {
    POINTS("Points", Set.class, Coordinate.class), VERTICES("Vertices", Set.class, Coordinate.class), EDGES("Edges", Set.class, Edge.class), FACES("Faces", Set.class, Edge.class);

    private final Class<?> type;
    private final String name;
    private final Class<?> subType;

    Property(String name, Class<?> type, Class<?> subtype) {
        this.name = name;
        this.type = type;
        this.subType = subtype;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> getType() {
        return (Class<T>) type;
    }

}
