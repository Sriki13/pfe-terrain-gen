package pfe.terrain.gen;

import pfe.terrain.gen.algo.generator.Generator;

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

