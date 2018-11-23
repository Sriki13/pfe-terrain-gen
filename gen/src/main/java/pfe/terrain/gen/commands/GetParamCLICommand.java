package pfe.terrain.gen.commands;

import pfe.terrain.gen.algo.exception.WrongArgsException;
import pfe.terrain.gen.contextParser.Utils;

import java.io.FileInputStream;
import java.io.InputStream;

public class GetParamCLICommand implements Command {
    @Override
    public String getDescription() {
        return "read the param from the command line";
    }

    @Override
    public String getName() {
        return "-c";
    }

    @Override
    public String execute(String... params) throws WrongArgsException {
        if(params.length < 2){
            throw new WrongArgsException();
        }
        return params[1];

    }
}
