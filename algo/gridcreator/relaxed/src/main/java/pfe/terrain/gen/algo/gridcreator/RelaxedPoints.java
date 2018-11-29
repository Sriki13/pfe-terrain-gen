package pfe.terrain.gen.algo.gridcreator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.Param;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.CoordSet;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class RelaxedPoints extends Contract {


     static final Param<Integer> nbPoints = new Param<>("nbPoints", Integer.class,
            "100-100000", "number of points in the map (=tiles)", 1024);
     static final Param<Integer> nbIter = new Param<>("nbIterations", Integer.class,
            "1-10", "number of iterations to smoothen grid repartition", 3);

    @Override
    public Constraints getContract() {
        return new Constraints(asKeySet(size, seed), asKeySet(new Key<>("POINTS", CoordSet.class)));
    }

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(nbIter, nbPoints);
    }

    @Override
    public void execute(IslandMap islandMap, Context context) {
        int numberOfPoints = context.getParamOrDefault(nbPoints);
        int relaxationIterations = context.getParamOrDefault(nbIter);
        Set<Coordinate> points = new HashSet<>();
        Random random = new Random(islandMap.getSeed());
        for (int i = 0; i < numberOfPoints; i++) {
            points.add(new Coordinate(random.nextDouble() * islandMap.getSize(), random.nextDouble() * islandMap.getSize()));
        }
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
            points = centroids.stream()
                    .map(c -> new Coordinate(insideValue(c.x, islandMap.getSize()), insideValue(c.y, islandMap.getSize())))
                    .collect(Collectors.toSet());
        }
        islandMap.putProperty(new Key<>("POINTS", CoordSet.class), CoordSet.buildFromCoordinates(points));
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
