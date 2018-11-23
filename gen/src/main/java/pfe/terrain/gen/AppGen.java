package pfe.terrain.gen;

import pfe.terrain.gen.algo.generator.Generator;

public class AppGen {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        Generator gen = new MapGenerator();

        CommandParser parser = new CommandParser(gen);

        System.out.println(parser.execute(args));
    }

}

