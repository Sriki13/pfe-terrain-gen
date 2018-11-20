package pfe.terrain.gen.commands;

import pfe.terrain.gen.algo.CommandConstants;
import pfe.terrain.gen.algo.generator.Generator;

public class ListCommand implements Command {

    private Generator gen;

    public ListCommand(Generator gen){
        this.gen = gen;
    }

    @Override
    public String getDescription() {
        return "return the generator's id";
    }

    @Override
    public String getName() {
        return CommandConstants.getId;
    }

    @Override
    public String execute() {
        return String.valueOf(gen.getId());
    }
}
