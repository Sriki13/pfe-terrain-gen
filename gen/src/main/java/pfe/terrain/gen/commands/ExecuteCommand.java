package pfe.terrain.gen.commands;

import pfe.terrain.gen.CommandParser;
import pfe.terrain.gen.algo.CommandConstants;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.gen.algo.parsing.ContextParser;
import pfe.terrain.gen.algo.MapContext;

public class ExecuteCommand implements Command{
    private Generator gen;
    private CommandParser commandParser;

    public ExecuteCommand(Generator gen){
        this.gen = gen;

        this.commandParser = new CommandParser(new GetFileCommand(),new GetInputCommand());
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
    public String execute(String... params) {
        if (params.length > 1) {
            String[] fileParam = new String[params.length - 1];
            System.arraycopy(params, 1, fileParam, 0, params.length - 1);
            String contextString = this.commandParser.execute(fileParam);
            ContextParser parser = new ContextParser(contextString);
            Context context = new MapContext(parser.getMap(), gen.getContracts());
            this.gen.setParams(context);
        }
        return gen.generate();

    }
}
