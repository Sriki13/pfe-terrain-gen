package pfe.terrain.gen;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.*;

import java.util.*;

public class MeshBuilder extends Contract {

    private CoordSet allCoords;
    private Map<Edge, Edge> allEdgesMap;

    @Override
    public Constraints getContract() {
        return new Constraints(
                asSet(new Key<>("POINTS", CoordSet.class), size),
                asSet(vertices, edges, faces)
        );
    }

    @Override
    public void execute(IslandMap map, Context context) throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        List<Polygon> polygons = genPolygons(map);
        this.allCoords = new CoordSet(genVertex(polygons));
        this.allEdgesMap = new HashMap<>();
        FaceSet allFaces = new FaceSet(genFaces(polygons));
        map.putProperty(Contract.vertices, allCoords);
        map.putProperty(Contract.faces, allFaces);
        EdgeSet allEdges = new EdgeSet(new HashSet<>());
        for (Face face : allFaces) {
            allEdges.addAll(face.getEdges());
        }
        map.putProperty(Contract.edges, allEdges);
        genNeighbor(map);
    }

    private List<Polygon> genPolygons(IslandMap map) throws NoSuchKeyException, KeyTypeMismatch {
        List<Polygon> res = new ArrayList<>();

        VoronoiDiagramBuilder builder = new VoronoiDiagramBuilder();

        Set<Coordinate> coordinateSet = map.getProperty(new Key<>("POINTS", CoordSet.class)).convertToCoordinateSet();
        builder.setSites(coordinateSet);

        Coordinate[] boundaries = {new Coordinate(0, 0),
                new Coordinate(0, map.getSize()),
                new Coordinate(map.getSize(), map.getSize()),
                new Coordinate(map.getSize(), 0),
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

    private Set<Coord> genVertex(List<Polygon> polygons) {
        Set<Coord> coordinates = new HashSet<>();

        for (Polygon polygon : polygons) {
            Coordinate[] vertices = polygon.getCoordinates();
            for (Coordinate vertex : vertices) {
                coordinates.add(new Coord(vertex));
            }
            Point centroid = polygon.getCentroid();
            coordinates.add(new Coord(centroid.getX(), centroid.getY()));
        }
        return coordinates;
    }

    private Set<Face> genFaces(List<Polygon> polygons) {
        Set<Face> faces = new HashSet<>();

        for (Polygon polygon : polygons) {
            Set<Edge> edges = extractEdges(polygon);
            faces.add(new Face(new Coord(polygon.getCentroid().getCoordinate()), edges));
        }

        return faces;
    }

    private Coord findInAllCoords(Coordinate coordinate) {
        for (Coord coord : allCoords) {
            if (coord.matches(coordinate)) {
                return coord;
            }
        }
        throw new RuntimeException("Coord for edge was not found in vertices!");
    }

    private Edge findOrAddEdgeToMap(Edge search) {
        Edge found = allEdgesMap.get(search);
        if (found == null) {
            allEdgesMap.put(search, search);
            return search;
        }
        return found;
    }

    private Set<Edge> extractEdges(Polygon polygon) {
        Set<Edge> edges = new HashSet<>();
        Coordinate[] coordinates = polygon.getBoundary().getCoordinates();
        for (int i = 0; i < coordinates.length; i++) {
            if (i == coordinates.length - 1) {
                Coord start = findInAllCoords(coordinates[i]);
                Coord end = findInAllCoords(coordinates[0]);
                addNewEdgeToPolygonSet(edges, start, end);
            } else {
                Coord start = findInAllCoords(coordinates[i]);
                Coord end = findInAllCoords(coordinates[i + 1]);
                addNewEdgeToPolygonSet(edges, start, end);
            }
        }
        return edges;
    }

    private void addNewEdgeToPolygonSet(Set<Edge> edges, Coord start, Coord end) {
        if (!start.equals(end)) {
            Edge edge = findOrAddEdgeToMap(new Edge(start, end));
            edges.add(edge);
        }
    }


    private void genNeighbor(IslandMap map) throws NoSuchKeyException, KeyTypeMismatch {
        Key<FaceSet> key = new Key<>("FACES", FaceSet.class);
        CoordSet centers = getFacesCenters(map.getProperty(key));

        DelaunayTriangulationBuilder builder = new DelaunayTriangulationBuilder();
        builder.setSites(centers.convertToCoordinateSet());

        Geometry geo = builder.getTriangles(new GeometryFactory());

        List<Polygon> polygons = genPolygons(geo);

        for (Polygon polygon : polygons) {
            Set<Face> faces = new HashSet<>();
            for (Coordinate coordinate : polygon.getCoordinates()) {
                faces.add(getFaceFromCenter(map.getProperty(key), new Coord(coordinate)));
            }

            for (Face face : faces) {
                face.addNeighbor(faces);
            }
        }
    }

    private Face getFaceFromCenter(Set<Face> faces, Coord center) {
        for (Face face : faces) {
            if (face.getCenter().equals(center)) {
                return face;
            }
        }
        return null;

    }

    private CoordSet getFacesCenters(Set<Face> faces) {
        CoordSet coords = new CoordSet();

        for (Face face : faces) {
            coords.add(face.getCenter());
        }

        return coords;
    }
}
