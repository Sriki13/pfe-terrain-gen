package pfe.terrain.gen.algo;

import com.vividsolutions.jts.geom.Coordinate;

import java.util.Set;

public class IslandMap {

    private int size;
    private Set<Coordinate> points;

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
}
