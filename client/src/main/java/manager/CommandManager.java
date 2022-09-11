package manager;


import client.Client;
import command.Commandable;
import command.ExecuteScript;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {


    protected Map<String, Commandable> map = new HashMap<>();

    public void addCommand(String key, Commandable cmd) {

        map.put(key, cmd);

    }

    public String execCommand(String key) {
        return(map.get(key).execute(""));
    }





    public CommandManager(ConsoleManager consoleManager, FileManager fileManager, Client client) {
        addCommand("execute_script", new ExecuteScript(consoleManager, fileManager, client));//     : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.
    }

}