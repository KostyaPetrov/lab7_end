package command;


import collection.Product;
import manager.CommandHandler;
import manager.DatabaseManager;
import manager.ServerCollectionManager;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Add the product if it is the most expensive
 */
public class AddIfMax implements Commandable {
    private ServerCollectionManager collectionManager;
    private CommandHandler commandHandler;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public AddIfMax(ServerCollectionManager collectionManager, CommandHandler commandHandler) {
        this.collectionManager = collectionManager;
        this.commandHandler=commandHandler;
    }

    @Override
    public String execute(String s, InetSocketAddress address)  {
        writeLock.lock();
        readLock.lock();

        try {
            Product product = commandHandler.getProduct(address);
            Connection connection = DatabaseManager.getConnectionDataBase();
            String selectSQL = "insert into collections (\"id\", \"name\", coordinatesX, coordinatesY, creationDate, price, partNumber, manufactureCost, unitOfMesure, ownerName, ownerBirthday, ownerWeight, userName) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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


            LinkedList<Product> collectionElements = collectionManager.getCollection();
            ArrayList<Integer> arrPrice = new ArrayList<>();
            for (Product collectionElement : collectionElements) {
                arrPrice.add(collectionElement.getPrice());
            }
            if (product.getPrice() > Collections.max(arrPrice)) {
                preparedStatement.executeUpdate();
                collectionElements.add(product);
                collectionManager.setCollection(collectionElements);
                preparedStatement.close();
                return ("Product added");
            } else {
                preparedStatement.close();
                return ("Price of your product is not highest. Product not added.");
            }
        }catch (SQLException e){
            return "Failed to save product. Try again";
        }finally {
            writeLock.unlock();
            readLock.unlock();
        }

    }
}
