package pfe.terrain.gen.algo.gridcreator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.geometry.CoordSet;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class RelaxedPoints extends Contract {

    static final Param<Integer> NB_POINTS = new Param<>("nbPoints", Integer.class, 64, 100000,
            "Number of points in the map (=tiles)", 1024, "Number of points");
    static final Param<Integer> NB_ITER = new Param<>("nbIterations", Integer.class, 1, 10,
            "Number of iterations to smooth grid repartition", 3, "Number of iterations");

    @Override
    public Constraints getContract() {
        return new Constraints(asKeySet(SIZE, SEED), asKeySet(new Key<>("POINTS", CoordSet.class)));
    }

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(NB_ITER, NB_POINTS);
    }

    @Override
    public void execute(IslandMap islandMap, Context context) {
        int numberOfPoints = context.getParamOrDefault(NB_POINTS);
        int relaxationIterations = context.getParamOrDefault(NB_ITER);
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
