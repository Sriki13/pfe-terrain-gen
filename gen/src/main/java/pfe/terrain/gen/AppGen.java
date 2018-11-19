package pfe.terrain.gen;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import pfe.terrain.gen.algo.*;
import pfe.terrain.gen.algo.algorithms.PointsGenerator;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.CoordSet;

import java.util.Set;

/**
 * Hello world!
 */
public class AppGen {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        IslandMap islandMap = new IslandMap();
        islandMap.setSize(8);
        //PointsGenerator generator = new GridPoints();
        Reflections reflections = new Reflections("pfe.terrain.gen", new SubTypesScanner(false));
        System.out.println(reflections.getAllTypes());
        Set<Class<? extends PointsGenerator>> subTypes = reflections.getSubTypesOf(PointsGenerator.class);
        if (subTypes.isEmpty()) {
            System.out.println("No points generator found");
            return;
        } else if (subTypes.size() > 1) {
            System.out.println("Too many points generators");
            return;
        }
        System.out.println(subTypes);
        PointsGenerator g = null;
        //noinspection LoopStatementThatDoesntLoop
        for (Class cl : subTypes) {
            g = (PointsGenerator) cl.newInstance();
            break;
        }
        try {
            g.execute(islandMap);
            System.out.println(islandMap.getProperty(new Key<>("POINTS", CoordSet.class)));
        } catch (InvalidAlgorithmParameters | KeyTypeMismatch | NoSuchKeyException | DuplicateKeyException invalidAlgorithmParameters) {
            invalidAlgorithmParameters.printStackTrace();
        }
    }

}

