package pfe.terrain.gen;

import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.gen.commands.Command;
import pfe.terrain.gen.commands.DescriptionCommand;
import pfe.terrain.gen.commands.ExecuteCommand;
import pfe.terrain.gen.commands.ListCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandParser {

    private List<Command> commandList;
    private Command help;

    public CommandParser(Generator gen){


        commandList = new ArrayList<>();
        commandList.add(new ExecuteCommand(gen));
        commandList.add(new ListCommand(gen));

        this.help = new DescriptionCommand(commandList);

        commandList.add(help);
    }

    public String execute(String... param){
        if(param.length == 0){
            return wrongCommand();
        }

        for(Command command : commandList){
            if(command.getName().equals(param[0])){
                return command.execute();
            }
        }

        return wrongCommand();
    }

    private String wrongCommand(){
        String builder = "No such command\n";
        builder += help.execute();

        return builder;
    }



}
