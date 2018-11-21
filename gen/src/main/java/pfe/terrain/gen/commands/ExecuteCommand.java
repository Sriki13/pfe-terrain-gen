package pfe.terrain.gen.commands;

import pfe.terrain.gen.algo.CommandConstants;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.gen.export.JSONExporter;

public class ExecuteCommand implements Command{
    private Generator gen;

    public ExecuteCommand(Generator gen){
        this.gen = gen;
    }

    @Override
    public String getDescription() {
        return "run the generator and display the output";
    }

    @Override
    public String getName() {
        return CommandConstants.exec;
    }

    @Override
    public String execute() {
        return gen.generate();
    }
}
