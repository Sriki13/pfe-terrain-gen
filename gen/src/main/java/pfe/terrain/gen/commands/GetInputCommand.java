package pfe.terrain.gen.commands;

import pfe.terrain.gen.contextParser.Utils;
import pfe.terrain.gen.algo.exception.WrongArgsException;

public class GetInputCommand implements Command {
    @Override
    public String getDescription() {
        return "get string from user input";
    }

    @Override
    public String getName() {
        return "-i";
    }

    @Override
    public String execute(String... params) throws WrongArgsException {

        try{
            return Utils.inputStreamToString(System.in);
        } catch (Exception e){
            throw new WrongArgsException("Wrong input given");
        }
    }
}
