package pfe.terrain.gen.commands;

import pfe.terrain.gen.contextParser.Utils;
import pfe.terrain.gen.algo.exception.WrongArgsException;

import java.io.FileInputStream;
import java.io.InputStream;

public class GetFileCommand implements Command {
    @Override
    public String getDescription() {
        return "read file";
    }

    @Override
    public String getName() {
        return "-f";
    }

    @Override
    public String execute(String... params) throws WrongArgsException {
        if(params.length < 1){
            throw new WrongArgsException();
        }
        try {
            String filename = params[1];
            InputStream stream = new FileInputStream(filename);

            return Utils.inputStreamToString(stream);
        } catch (Exception e){
            throw new WrongArgsException("wrong filename or format");
        }
    }
}
