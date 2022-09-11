package command;


import collection.Product;
import manager.CommandHandler;
import manager.DatabaseManager;
import manager.ServerCollectionManager;

import java.net.InetSocketAddress;
import java.sql.Connection;
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
            Product newProduct = commandHandler.getProduct(address);
            Connection connection = DatabaseManager.getConnectionDataBase();
            String selectSQL = "insert into collections (name, coordinatesX, coordinatesY, creationDate, price, partNumber, manufactureCost, unitOfMesure, ownerName, ownerBirthday, ownerWeight, userName) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setString(1, newProduct.getName());
            preparedStatement.setString(2, String.valueOf(newProduct.getCoordinates().getCoordinateX()));
            preparedStatement.setString(3, String.valueOf(newProduct.getCoordinates().getCoordinateY()));
            preparedStatement.setString(4, String.valueOf(newProduct.getPrice()));
            preparedStatement.setString(5, newProduct.getPartNumber());
            preparedStatement.setString(6, String.valueOf(newProduct.getCreationDate()));
            preparedStatement.setString(7, String.valueOf(newProduct.getManufactureCost()));
            preparedStatement.setString(8, String.valueOf(newProduct.getUnitOfMeasure()));
            preparedStatement.setString(9, String.valueOf(newProduct.getOwner().getNamePerson()));
            preparedStatement.setString(10, String.valueOf(newProduct.getOwner().getBirthday()));
            preparedStatement.setString(11, String.valueOf(newProduct.getOwner().getWeight()));
            preparedStatement.setString(12, newProduct.getLogin());


            LinkedList<Product> collectionElements = collectionManager.getCollection();
            ArrayList<Integer> arrPrice = new ArrayList<>();
            for (Product collectionElement : collectionElements) {
                arrPrice.add(collectionElement.getPrice());
            }
            if (newProduct.getPrice() > Collections.max(arrPrice)) {
                preparedStatement.executeUpdate();
                collectionElements.add(newProduct);
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
