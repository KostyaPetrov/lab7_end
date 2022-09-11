package server;

import exeption.ConnectionExeption;
import exeption.DatabaseExeption;
import exeption.InvalidPortException;
import manager.*;

import java.io.IOException;

public class MainServer {


    public static void main(String[] args) throws IOException, InvalidPortException {


        int port = 0;
        String strPort = "5000";
        //String dbHost = "pg";
        String dbHost = "localhost";
        String user = "s339742";
        String password = "bwt369";
        String url = "jdbc:postgresql://" + dbHost + ":5432/studs";

        try {
            if (args.length == 4) {
                strPort = args[0];
                dbHost = args[1];
                user = args[2];
                password = args[3];
                url = "jdbc:postgresql://" + dbHost + ":5432/studs";
            }
            if (args.length == 1) strPort = args[0];
            if (args.length == 0) System.out.println("No port passed by argument, hosted on " + strPort);
            try {
                port = Integer.parseInt(strPort);
            } catch (NumberFormatException e) {
                throw new InvalidPortException();

            }
//        Properties settings = new Properties();
//        settings.setProperty("url", url);
//        settings.setProperty("user", user);
//        settings.setProperty("password", password);


            Serializer serializer = new Serializer();
            ConsoleManager consoleManager = new ConsoleManager();
            ServerCollectionManager collectionManager = new ServerCollectionManager();
            DatabaseManager databaseManager = new DatabaseManager(collectionManager, url, user, password);
            CommandHandler commandHandler = new CommandHandler(collectionManager, consoleManager, databaseManager);
            CommandManager commandManager = new CommandManager(collectionManager, consoleManager, serializer, commandHandler, databaseManager);




            Server server = new Server(consoleManager, collectionManager, serializer, commandHandler, commandManager, databaseManager, port);
            server.start();
        } catch (ConnectionExeption | DatabaseExeption e) {
            e.printStackTrace();
        }


    }
}
