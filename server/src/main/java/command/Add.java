package command;


import collection.Product;
import manager.CommandHandler;
import manager.ConsoleManager;
import manager.DatabaseManager;
import manager.ServerCollectionManager;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Add implements Commandable {
    private ConsoleManager consoleManager;
    private ServerCollectionManager collectionManager;
    private CommandHandler commandHandler;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    /**
     * Add Product to database
     */
    public Add(ServerCollectionManager collectionManager, ConsoleManager consoleManager, CommandHandler commandHandler) {
        this.collectionManager = collectionManager;
        this.consoleManager = consoleManager;
        this.commandHandler = commandHandler;
    }

    @Override
    public String execute(String s, InetSocketAddress address) {
        writeLock.lock();
        readLock.lock();

        try {
            Product product = commandHandler.getProduct(address);
            Connection connection = DatabaseManager.getConnectionDataBase();
            //Statement statement=connection.createStatement();
            String selectSQL = "insert into collections (\"id\", \"name\", \"coordinatex\", \"coordinatey\", \"creationdate\", \"price\", \"partnumber\", \"manufacturecost\", \"unitofmesure\", \"ownername\", \"ownerbirthday\", \"ownerweight\", \"username\") values (?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setInt(1, collectionManager.getUniqueId("collections"));
            preparedStatement.setString(2, product.getName());
            preparedStatement.setLong(3, product.getCoordinates().getCoordinateX());
            preparedStatement.setFloat(4, product.getCoordinates().getCoordinateY());
            preparedStatement.setDate(5, Date.valueOf(product.getCreationDate()));
            preparedStatement.setInt(6, product.getPrice());
            preparedStatement.setString(7, product.getPartNumber());
            preparedStatement.setFloat(8, product.getManufactureCost());
            preparedStatement.setString(9, product.getUnitOfMeasure().getWord());
            preparedStatement.setString(10, product.getOwner().getNamePerson());
            preparedStatement.setObject(11, product.getOwner().getBirthday());
            preparedStatement.setDouble(12,product.getOwner().getWeight());
            preparedStatement.setString(13, product.getLogin());
            preparedStatement.executeUpdate();

            // reset.close();
            //statement.close();
            preparedStatement.close();
            LinkedList<Product> list = collectionManager.getCollection();

            list.add(product);
            collectionManager.setCollection(list);
            return "Product added";
        }catch (SQLException e){
            e.printStackTrace();
            return "Failed to save product. Try again";
        }finally {
            writeLock.lock();
            readLock.unlock();
        }
    }


}