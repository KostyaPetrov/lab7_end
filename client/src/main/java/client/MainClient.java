package client;

import exeption.IncorrectFieldException;
import manager.ClientCollectionManager;
import manager.CommandManager;
import manager.ConsoleManager;
import manager.FileManager;

public class MainClient {
    public static void main(String[] args){
        System.out.println("Hellow, dear user!\n" +
                "Welcome to the product collection manager\n");
        if (args.length == 2) {
            String host = args[0];
            Integer port = Integer.parseInt(args[1]);
            ClientCollectionManager collectionManager=new ClientCollectionManager();
            ConsoleManager consoleManager=new ConsoleManager();
            Client client=new Client(host, port,consoleManager);
            consoleManager.getClientObject(client, collectionManager);
            FileManager fileManager=new FileManager(consoleManager);
            CommandManager commandManager=new CommandManager(consoleManager, fileManager, client);
            client.start(commandManager);
        }else{
            throw new IncorrectFieldException("You must enter host and port for connection with server");
        }
    }

}
