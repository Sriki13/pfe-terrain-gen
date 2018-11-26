package pfe.terrain.gen;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
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

    private Map<Coord, Coord> allCoords;
    private Map<Edge, Edge> allEdgesMap;
    private CoordSet verticesSet;
    private FaceSet facesSet;

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
        this.allCoords = new HashMap<>();
        this.allEdgesMap = new HashMap<>();
        this.verticesSet = new CoordSet();
        this.facesSet = new FaceSet();
        genStuff(polygons);
        genNeighbor();
        map.putProperty(faces, facesSet);
        verticesSet.addAll(new CoordSet(allCoords.keySet()));
        map.putProperty(vertices, verticesSet);
        map.putProperty(edges, new EdgeSet(allEdgesMap.keySet()));
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

    private void genStuff(List<Polygon> polygons) {
        for (Polygon polygon : polygons) {
            Coord center = new Coord(polygon.getCentroid().getCoordinate());
            verticesSet.add(center);
            Coordinate[] coordinates = polygon.getBoundary().getCoordinates();
            Coord[] coords = new Coord[coordinates.length];
            for (int i = 0; i < coordinates.length; i++) {
                coords[i] = new Coord(coordinates[i]);
                if (allCoords.containsKey(coords[i])) {
                    coords[i] = allCoords.get(coords[i]);
                }
                else {
                    allCoords.put(coords[i],coords[i]);
                }
            }
            List<Edge> borders = new ArrayList<>(coords.length);
            for (int i = 0; i < coords.length; i++) {
                if (i == coords.length - 1) {
                    if (coords[i].equals(coords[0])) continue;
                    borders.add(new Edge(coords[i], coords[0]));
                } else {
                    if (coords[i].equals(coords[i+1])) continue;
                    borders.add(new Edge(coords[i], coords[i + 1]));
                }
                if (allEdgesMap.containsKey(borders.get(i))) {
                    borders.set(i, allEdgesMap.get(borders.get(i)));
                }
                else {
                    allEdgesMap.put(borders.get(i),borders.get(i));
                }
            }
            facesSet.add(new Face(center, new EdgeSet(borders)));
        }
    }

    private void genNeighbor() {
        CoordSet centers = getFacesCenters(facesSet);

        DelaunayTriangulationBuilder builder = new DelaunayTriangulationBuilder();
        builder.setSites(centers.convertToCoordinateSet());

        Geometry geo = builder.getTriangles(new GeometryFactory());

        List<Polygon> polygons = genPolygons(geo);

        for (Polygon polygon : polygons) {
            Set<Face> faces = new HashSet<>();
            for (Coordinate coordinate : polygon.getCoordinates()) {
                faces.add(getFaceFromCenter(facesSet, new Coord(coordinate)));
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
