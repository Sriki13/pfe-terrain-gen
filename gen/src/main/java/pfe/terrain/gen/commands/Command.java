package pfe.terrain.gen.commands;

public interface Command {
    String getDescription();
    String getName();

    String execute();
}
