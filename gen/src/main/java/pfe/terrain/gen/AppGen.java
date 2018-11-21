package pfe.terrain.gen;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.parsing.OrderParser;
import pfe.terrain.gen.algo.parsing.OrderedContract;
import pfe.terrain.gen.commands.Command;

import java.io.InputStream;
import java.util.*;

/**
 * Hello world!
 */
public class AppGen {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        Generator gen = new MapGenerator();

        CommandParser parser = new CommandParser(gen);

        System.out.print(parser.execute(args));
    }

}

