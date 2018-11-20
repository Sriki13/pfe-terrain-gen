package pfe.terrain.gen;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.algorithms.MeshGenerator;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MeshBuilder extends MeshGenerator {

    @Override
    public void execute(IslandMap map, Context context) throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        List<Polygon> polygons = genPolygons(map);
        map.putProperty(new Key<>("VERTICES", CoordSet.class), new CoordSet(genVertex(polygons)));
        map.putProperty(new Key<>("EDGES", EdgeSet.class), new EdgeSet(genEdges(polygons)));
        map.putProperty(new Key<>("FACES", FaceSet.class), new FaceSet(genFaces(polygons)));
        genNeighbor(map);
    }

    private List<Polygon> genPolygons(IslandMap map) throws NoSuchKeyException, KeyTypeMismatch {
        List<Polygon> res = new ArrayList<>();

        VoronoiDiagramBuilder builder = new VoronoiDiagramBuilder();

        builder.setSites(map.getProperty(new Key<>("POINTS", CoordSet.class)));

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

    private List<Coord> genVertex(List<Polygon> polygons) {
        List<Coord> coordinates = new ArrayList<>();

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

    private List<Edge> genEdges(List<Polygon> polygons) {
        List<Edge> edges = new ArrayList<>();
        for (Polygon polygon : polygons) {
            edges.addAll(extractEdges(polygon));
        }

        return edges;
    }

    private List<Face> genFaces(List<Polygon> polygons) {
        List<Face> faces = new ArrayList<>();

        for (Polygon polygon : polygons) {
            List<Edge> edges = extractEdges(polygon);
            faces.add(new Face(new Coord(polygon.getCentroid().getCoordinate()), edges));
        }

        return faces;
    }


    private List<Edge> extractEdges(Polygon polygon) {
        List<Edge> edges = new ArrayList<>();
        Coordinate[] coordinates = polygon.getBoundary().getCoordinates();
        for (int i = 0; i < coordinates.length; i++) {
            if (i == coordinates.length - 1) {
                edges.add(new Edge(new Coord(coordinates[i]), new Coord(coordinates[0])));
            } else {
                edges.add(new Edge(new Coord(coordinates[i]), new Coord(coordinates[i + 1])));
            }
        }
        return edges;
    }

    private void genNeighbor(IslandMap map) throws NoSuchKeyException, KeyTypeMismatch {
        Key<FaceSet> key = new Key<>("FACES", FaceSet.class);
        Set<Coord> centers = getFacesCenters(map.getProperty(key));

        DelaunayTriangulationBuilder builder = new DelaunayTriangulationBuilder();
        builder.setSites(centers);

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

    private Set<Coord> getFacesCenters(Set<Face> faces) {
        Set<Coord> coords = new HashSet<>();

        for (Face face : faces) {
            coords.add(face.getCenter());
        }

        return coords;
    }

}
