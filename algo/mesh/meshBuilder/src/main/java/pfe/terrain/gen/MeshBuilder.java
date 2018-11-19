package pfe.terrain.gen;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;
import pfe.terrain.gen.algo.*;
import pfe.terrain.gen.algo.algorithms.MeshGenerator;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.*;

import java.util.*;


public class MeshBuilder implements MeshGenerator {
    @Override
    public void execute(IslandMap map) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {


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

        for (int i = 0; i < polygons.size(); i++) {
            res.add((Polygon) polygons.get(i).intersection(rect));
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

    private List<Coordinate> genVertex(List<Polygon> polygons) {
        List<Coordinate> coordinates = new ArrayList<>();

        for (int i = 0; i < polygons.size(); i++) {
            coordinates.addAll(Arrays.asList(polygons.get(i).getCoordinates()));
            Point centroid = polygons.get(i).getCentroid();
            coordinates.add(new Coordinate(centroid.getX(), centroid.getY()));
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
            faces.add(new Face(polygon.getCentroid().getCoordinate(),
                    edges));
        }

        return faces;
    }


    private List<Edge> extractEdges(Polygon polygon) {
        List<Edge> edges = new ArrayList<>();
        Coordinate[] coordinates = polygon.getBoundary().getCoordinates();
        for (int i = 0; i < coordinates.length; i++) {
            if (i == coordinates.length - 1) {
                edges.add(new Edge(coordinates[i], coordinates[0]));
            } else {
                edges.add(new Edge(coordinates[i], coordinates[i + 1]));
            }
        }
        return edges;
    }

    private void genNeighbor(IslandMap map) throws NoSuchKeyException, KeyTypeMismatch {
        Key<FaceSet> key = new Key<>("FACES", FaceSet.class);
        Set<Coordinate> centers = getFacesCenters(map.getProperty(key));

        DelaunayTriangulationBuilder builder = new DelaunayTriangulationBuilder();
        builder.setSites(centers);

        Geometry geo = builder.getTriangles(new GeometryFactory());

        List<Polygon> polygons = genPolygons(geo);

        for (Polygon polygon : polygons) {
            Set<Face> faces = new HashSet<>();
            for (Coordinate coordinate : polygon.getCoordinates()) {
                faces.add(getFaceFromCenter(map.getProperty(key), coordinate));
            }

            for (Face face : faces) {
                face.addNeighbor(faces);
            }
        }
    }

    private Face getFaceFromCenter(Set<Face> faces, Coordinate center) {
        for (Face face : faces) {
            if (face.getCenter().equals(center)) {
                return face;
            }
        }
        return null;

    }

    private Set<Coordinate> getFacesCenters(Set<Face> faces) {
        Set<Coordinate> coords = new HashSet<>();

        for (Face face : faces) {
            coords.add(face.getCenter());
        }

        return coords;
    }

    @Override
    public String getName() {
        return "MeshBuilder";
    }
}
