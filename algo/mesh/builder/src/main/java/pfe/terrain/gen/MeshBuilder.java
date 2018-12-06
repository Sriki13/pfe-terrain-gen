package pfe.terrain.gen;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.*;

import java.util.*;

public class MeshBuilder extends Contract {

    private Map<Coord, Coord> allCoordsA;
    private Map<Coord, Coord> allCoordsB;
    private Map<Edge, Edge> allEdgesMap;
    private Map<Coordinate, Face> centerFaces;
    private CoordSet verticesSet;

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(new Key<>("POINTS", CoordSet.class), SIZE),
                asKeySet(VERTICES, EDGES, FACES)
        );
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        int size = map.getProperty(SIZE);
        List<Polygon> polygons = genPolygons(map, size);
        this.allCoordsA = new HashMap<>();
        this.allCoordsB = new HashMap<>();
        this.allEdgesMap = new HashMap<>();
        this.verticesSet = new CoordSet();
        this.centerFaces = new HashMap<>();
        genStuff(polygons, size);
        genNeighbor();
        map.putProperty(FACES, new FaceSet(centerFaces.values()));
        verticesSet.addAll(new CoordSet(allCoordsA.keySet()));
        verticesSet.addAll(new CoordSet(allCoordsB.keySet()));
        map.putProperty(VERTICES, verticesSet);
        EdgeSet edges = new EdgeSet(allEdgesMap.keySet());
        makeEdgesDeterministic(edges);
        map.putProperty(EDGES, edges);
    }

    private void makeEdgesDeterministic(EdgeSet edges) {
        Coord start, end;
        for (Edge edge : edges) {
            start = edge.getStart();
            end = edge.getEnd();
            if (start.x + start.y > end.x + end.y) {
                edge.switchWay();
            }
        }
    }

    private List<Polygon> genPolygons(TerrainMap map, int size) throws NoSuchKeyException, KeyTypeMismatch {
        List<Polygon> res = new ArrayList<>();

        VoronoiDiagramBuilder builder = new VoronoiDiagramBuilder();

        Set<Coordinate> coordinateSet = map.getProperty(new Key<>("POINTS", CoordSet.class)).convertToCoordinateSet();
        builder.setSites(coordinateSet);

        Coordinate[] boundaries = {new Coordinate(0, 0),
                new Coordinate(0, size),
                new Coordinate(size, size),
                new Coordinate(size, 0),
                new Coordinate(0, 0)};

        Geometry geo = builder.getDiagram(new GeometryFactory());
        Polygon rect = geo.getFactory().createPolygon(boundaries);

        List<Polygon> polygons = genPolygons(geo);

        for (Polygon polygon : polygons) {
            res.add((Polygon) polygon.intersection(rect));
        }

        return res;
    }

    private List<Polygon> genPolygons(Geometry geo) {
        List<Polygon> res = new ArrayList<>();

        for (int i = 0; i < geo.getNumGeometries(); i++) {
            res.add((Polygon) geo.getGeometryN(i));
        }

        return res;
    }

    private void genStuff(List<Polygon> polygons, int size) {
        int midSize = size / 2;
        for (Polygon polygon : polygons) {
            Coord center = new Coord(polygon.getCentroid().getCoordinate());
            verticesSet.add(center);
            Coordinate[] coordinates = polygon.getBoundary().getCoordinates();
            Coord[] coords = new Coord[coordinates.length];
            for (int i = 0; i < coordinates.length; i++) {
                coords[i] = new Coord(coordinates[i]);
                if (coords[i].x < midSize) {
                    Coord tmp = allCoordsA.get(coords[i]);
                    if (tmp == null) {
                        allCoordsA.put(coords[i], coords[i]);
                    } else {
                        coords[i] = tmp;
                    }
                } else {
                    Coord tmp = allCoordsB.get(coords[i]);
                    if (tmp == null) {
                        allCoordsB.put(coords[i], coords[i]);
                    } else {
                        coords[i] = tmp;
                    }
                }
            }
            List<Edge> borders = new ArrayList<>(coords.length);
            for (int i = 0; i < coords.length; i++) {
                if (i == coords.length - 1) {
                    if (coords[i].equals(coords[0])) continue;
                    borders.add(new Edge(coords[i], coords[0]));
                } else {
                    if (coords[i].equals(coords[i + 1])) continue;
                    borders.add(new Edge(coords[i], coords[i + 1]));
                }
                Edge tmp = allEdgesMap.get(borders.get(i));
                if (tmp == null) {
                    allEdgesMap.put(borders.get(i), borders.get(i));
                } else {
                    borders.set(i, tmp);
                }
            }
            centerFaces.put(polygon.getCentroid().getCoordinate(), new Face(center, new EdgeSet(borders)));
        }
    }

    private void genNeighbor() {
        Set<Coordinate> centers = centerFaces.keySet();

        DelaunayTriangulationBuilder builder = new DelaunayTriangulationBuilder();
        builder.setSites(centers);

        Geometry geo = builder.getTriangles(new GeometryFactory());

        List<Polygon> polygons = genPolygons(geo);

        for (Polygon polygon : polygons) {
            Set<Face> faces = new HashSet<>();
            for (Coordinate coordinate : polygon.getCoordinates()) {
                faces.add(centerFaces.get(coordinate));
            }

            for (Face face : faces) {
                face.addNeighbor(faces);
            }
        }
    }
}
