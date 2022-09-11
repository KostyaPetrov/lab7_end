package command;


import collection.Product;
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

public class Clear implements Commandable {
    private DatabaseManager databaseManager;
    private ServerCollectionManager collectionManager;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public Clear(ServerCollectionManager collectionManager, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(String s, InetSocketAddress address) {
        writeLock.lock();
        readLock.lock();
        try {
            Connection connection = DatabaseManager.getConnectionDataBase();
            String login = databaseManager.getLogin(address);
            String selectSQL = "delete from collections where \"userName\"= ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setString(1, login);
            preparedStatement.executeUpdate();

            LinkedList<Product> listProduct = collectionManager.getCollection();
            ArrayList<Integer> listId = collectionManager.getUniqueId();
            for (int i = 0; i < listProduct.size(); i++) {
                if (listProduct.get(i).getLogin().equals(login)) {
                    listProduct.remove(i);
                    listId.remove(i);
                }
            }

            collectionManager.setCollection(listProduct);
            collectionManager.setUniqueId(listId);
            preparedStatement.close();



            return "Your entries have been successfully cleared";
        } catch (SQLException e) {


            return "Failed to clear records. Try again";
        }finally {
            writeLock.unlock();
            readLock.unlock();
        }
    }
}
