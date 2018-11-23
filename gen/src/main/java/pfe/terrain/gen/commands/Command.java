package pfe.terrain.gen.commands;

import pfe.terrain.gen.exception.WrongArgsException;

public interface Command {
    String getDescription();
    String getName();

    String execute(String... params) throws WrongArgsException;
}
