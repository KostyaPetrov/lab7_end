package command;


import collection.Product;
import exeption.CommandExeption;
import exeption.FieldProductExeption;
import manager.CommandHandler;
import manager.ConsoleManager;
import manager.DatabaseManager;
import manager.ServerCollectionManager;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class RemoveId implements Commandable {
    private ConsoleManager consoleManager;
    private ServerCollectionManager collectionManager;
    private Integer indexElementCollection;
    private LinkedList<Product> collectionElements;
    private ArrayList<Integer> collectionId;
    private CommandHandler commandHandler;
    private DatabaseManager databaseManager;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public RemoveId(ConsoleManager consoleManager, ServerCollectionManager collectionManager, CommandHandler commandHandler, DatabaseManager databaseManager) {
        this.consoleManager = consoleManager;
        this.collectionManager = collectionManager;
        this.commandHandler = commandHandler;
        this.databaseManager = databaseManager;
    }

    @Override
    public String execute(String s, InetSocketAddress address) {
        writeLock.lock();
        readLock.lock();

        try {
            Integer removeId = commandHandler.getRemoveId(Thread.currentThread().getName());

            Boolean checkException=consoleManager.getExeptionInfo(Thread.currentThread().getName());

            if (checkException) {
                consoleManager.setExeptionInfo(false);


                return "Error";
            } else {

                collectionElements = collectionManager.getCollection();

                collectionId = collectionManager.getListUniqueId();
                //get position element in collection and remove themco
                if (!collectionId.contains(removeId)) {
                    throw new FieldProductExeption("This element collection does not exist");
                }

                Product product=collectionElements.get(collectionId.indexOf(removeId));
                String login=product.getLogin();
                String login2=databaseManager.getLogin(address);
                if (login.equals(login2)) {
                    Connection connection = DatabaseManager.getConnectionDataBase();
                    String selectSQL = "delete from collections where \"id\"= ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
                    preparedStatement.setInt(1, removeId);
                    preparedStatement.executeUpdate();

                    indexElementCollection = collectionId.indexOf(removeId);

                    int i = indexElementCollection;
                    collectionElements.remove(i);
                    collectionId.remove(i);

                    //write new colllection in collection storage
                    collectionManager.setCollection(collectionElements);
                    collectionManager.setListUniqueId(collectionId);
                    preparedStatement.close();


                    return "Data deleted successfully";
                }else{

                    return "Permission denied. You can remove only your notation";
                }

            }


        } catch (SQLException e) {
            e.printStackTrace();
            return "Unable to remove product, please try again";
        } catch (CommandExeption e) {
            System.err.println(e.getMessage());


            return "This element collection does not exist";
        }finally {
            writeLock.unlock();
            readLock.unlock();
        }
    }

}
