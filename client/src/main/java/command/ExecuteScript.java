package command;



import client.Client;
import collection.Coordinates;
import collection.Person;
import collection.ProductClient;
import collection.UnitOfMeasure;
import manager.ConsoleManager;
import manager.FileManager;

import java.time.LocalDateTime;


public class ExecuteScript implements Commandable{
    private ConsoleManager consoleManager;
    private FileManager fileManager;
    private Client client;

    public ExecuteScript(ConsoleManager consoleManager, FileManager fileManager, Client client){
        this.consoleManager=consoleManager;
        this.fileManager=fileManager;
        this.client=client;

    }
    @Override
    public String execute(String s){
        String[] scriptFile= fileManager.getFileScript();
        /**
         * Reading every command from line to line
         */
        for(int i=0; i<scriptFile.length;i++){
            if(scriptFile[i].trim().equals("add")){

                client.send("add", new ProductClient(scriptFile[i+1],
                        new Coordinates(Long.parseLong(scriptFile[i + 2].trim()), Float.parseFloat(scriptFile[i + 3].trim())),
                        consoleManager.getDate(), Integer.parseInt(scriptFile[i+4].trim()), scriptFile[i+5],
                        Float.valueOf(scriptFile[i+6].trim()), UnitOfMeasure.valueOf(scriptFile[i+7].trim()), new Person(scriptFile[i+8],
                        LocalDateTime.parse(scriptFile[i+9].trim()), Double.valueOf(scriptFile[i+10].trim()))));
                i+=10;

            }else if(scriptFile[i].contains(" ") && scriptFile[i].split(" ")[0].equals("update_id")){

                client.send(scriptFile[i],new ProductClient(scriptFile[i+1],
                        new Coordinates(Long.parseLong(scriptFile[i +2].trim()), Float.parseFloat(scriptFile[i + 3].trim())),
                        consoleManager.getDate(), Integer.parseInt(scriptFile[i+4].trim()), scriptFile[i+5],
                        Float.valueOf(scriptFile[i+6].trim()), UnitOfMeasure.valueOf(scriptFile[i+7].trim()), new Person(scriptFile[i+8],
                        LocalDateTime.parse(scriptFile[i+9].trim()), Double.valueOf(scriptFile[i+10].trim()))));

                i+=10;
                //write new colllection in collection storage

            }else{
                if(scriptFile[i].equals("exit")){
                    consoleManager.setEndScriptCommand("exit");
                    break;
                }

                consoleManager.getCommand(scriptFile[i]);
                if(consoleManager.getExeptionInfo()){
                    consoleManager.setExeptionInfo(false);
                    break;
                }


            }
        }
        return null;
    }

}
