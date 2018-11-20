package pfe.terrain.gen.algo.gridcreator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.algorithms.PointsGenerator;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.CoordSet;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class RelaxedPoints extends PointsGenerator {

    @Override
    public void execute(IslandMap islandMap) throws DuplicateKeyException {
        int numberOfPoints = this.getDefaultNbPoint();
        CoordSet points = new CoordSet();
        Random random = new Random();
        for (int i = 0; i < numberOfPoints; i++) {
            points.add(new Coord(random.nextDouble() * islandMap.getSize(), random.nextDouble() * islandMap.getSize()));
        }
        int relaxationIterations = 3;
        for (int i = 0; i < relaxationIterations; i++) {
            VoronoiDiagramBuilder voronoiBuilder = new VoronoiDiagramBuilder();
            voronoiBuilder.setSites(points);
            GeometryCollection voronoiDiagram = (GeometryCollection) voronoiBuilder.getDiagram(new GeometryFactory());
            CoordinateFilter stayInbounds = coordinate -> coordinate.setCoordinate(
                    new Coordinate(
                            insideValue(coordinate.x, islandMap.getSize()),
                            insideValue(coordinate.y, islandMap.getSize())));
            voronoiDiagram.apply(stayInbounds);
            Set<Coordinate> centroids = new HashSet<>();
            for (int j = 0; j < voronoiDiagram.getNumGeometries(); j++) {
                centroids.add(voronoiDiagram.getGeometryN(j).getCentroid().getCoordinate());
            }
            points = (CoordSet) centroids.stream()
                    .map(c -> new Coord(insideValue(c.x, islandMap.getSize()), insideValue(c.y, islandMap.getSize())))
                    .collect(Collectors.toSet());
        }
        islandMap.putProperty(new Key<>("POINTS", CoordSet.class), points);
    }

    private double insideValue(double val, int maxSize) {
        if (val < 0) {
            return 0.0;
        } else if (val > maxSize) {
            return (double) maxSize;
        } else {
            return val;
        }
    }

}
