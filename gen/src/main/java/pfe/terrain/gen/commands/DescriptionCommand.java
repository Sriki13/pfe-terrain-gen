package pfe.terrain.gen.commands;

import pfe.terrain.gen.algo.CommandConstants;

import java.util.List;

public class DescriptionCommand implements Command {
    private List<Command> commands;

    public DescriptionCommand(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public String getDescription() {
        return "give a description of all command";
    }

    @Override
    public String getName() {
        return CommandConstants.desc;
    }

    @Override
    public String execute() {
        StringBuilder builder = new StringBuilder();

        for(Command command : commands){
            builder.append(command.getName());
            builder.append(" : ");
            builder.append(command.getDescription());
            builder.append("\n");
        }

        return builder.toString();
    }
}
