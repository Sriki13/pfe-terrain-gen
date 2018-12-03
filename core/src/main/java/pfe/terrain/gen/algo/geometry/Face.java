package pfe.terrain.gen.algo.geometry;

import pfe.terrain.gen.algo.Mappable;

import java.util.*;

public class Face extends Mappable {

    private Coord center;

    private Set<Edge> edges;

    private Set<Face> neighbors;

    public Face(Coord center, Set<Edge> edges) {
        super();
        this.center = center;
        this.edges = edges;
        this.neighbors = new HashSet<>();
    }

    public void addNeighbor(Face face) {
        neighbors.add(face);
        this.neighbors.remove(this);
    }

    public void addNeighbor(Collection<Face> face) {
        neighbors.addAll(face);
        this.neighbors.remove(this);
    }

    public Coord getCenter() {
        return center;
    }

    public Set<Coord> getBorderVertices() {
        Set<Coord> result = new HashSet<>();
        for (Edge edge : edges) {
            result.add(edge.getStart());
            result.add(edge.getEnd());
        }
        return result;
    }

    public Set<Face> getNeighbors() {
        return neighbors;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Face face = (Face) o;
        return Objects.equals(center, face.center) &&
                Objects.equals(edges, face.edges);
    }

    public Set<Coord> getRandomPointsInside(int number, Random random) {
        List<Coord> coords = new ArrayList<>(getBorderVertices());
        Set<Coord> points = new HashSet<>();
        double maxX = coords.stream().mapToDouble(coord -> coord.x).max().getAsDouble();
        double maxY = coords.stream().mapToDouble(coord -> coord.y).max().getAsDouble();
        double minX = coords.stream().mapToDouble(coord -> coord.y).min().getAsDouble();
        double minY = coords.stream().mapToDouble(coord -> coord.y).min().getAsDouble();
        double rangeX = minX + (maxX - minX);
        double rangeY = minY + (maxY - minY);
        double x;
        double y;
        int cpt = 0;
        while (cpt < number) {
            x = random.nextDouble() * rangeX;
            y = random.nextDouble() * rangeY;
            if (this.containsPoint(x, y)) {
                points.add(new Coord(x, y));
                cpt++;
            }
        }
        return points;
    }

    // Stolen from java.awt.Polygon
    private boolean containsPoint(double x, double y) {
        int hits = 0;

        Set<Coord> borders = getBorderVertices();
        int npoints = borders.size();
        double[] xpoints = borders.stream().mapToDouble(c -> c.x).toArray();
        double[] ypoints = borders.stream().mapToDouble(c -> c.y).toArray();

        double lastx = xpoints[npoints - 1];
        double lasty = ypoints[npoints - 1];
        double curx, cury;

        for (int i = 0; i < npoints; lastx = curx, lasty = cury, i++) {
            curx = xpoints[i];
            cury = ypoints[i];

            if (cury == lasty) {
                continue;
            }

            double leftx;
            if (curx < lastx) {
                if (x >= lastx) {
                    continue;
                }
                leftx = curx;
            } else {
                if (x >= curx) {
                    continue;
                }
                leftx = lastx;
            }

            double test1, test2;
            if (cury < lasty) {
                if (y < cury || y >= lasty) {
                    continue;
                }
                if (x < leftx) {
                    hits++;
                    continue;
                }
                test1 = x - curx;
                test2 = y - cury;
            } else {
                if (y < lasty || y >= cury) {
                    continue;
                }
                if (x < leftx) {
                    hits++;
                    continue;
                }
                test1 = x - lastx;
                test2 = y - lasty;
            }

            if (test1 < (test2 / (lasty - cury) * (lastx - curx))) {
                hits++;
            }
        }

        return ((hits & 1) != 0);
    }

    @Override
    public int hashCode() {
        return Objects.hash(center, edges);
    }

}
