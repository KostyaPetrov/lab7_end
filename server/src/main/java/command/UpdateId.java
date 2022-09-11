package command;


import collection.Coordinates;
import collection.Person;
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

public class UpdateId implements Commandable{
    private ServerCollectionManager collectionManager;
    private ConsoleManager consoleManager;
    private Integer updateId, indexElementCollection;
    private CommandHandler commandHandler;
    private DatabaseManager databaseManager;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public UpdateId(ServerCollectionManager collectionManager, ConsoleManager consoleManager, CommandHandler commandHandler, DatabaseManager databaseManager){
        this.collectionManager=collectionManager;
        this.consoleManager=consoleManager;
        this.commandHandler=commandHandler;
        this.databaseManager=databaseManager;
    }
    @Override
    public String execute(String s, InetSocketAddress address){
        writeLock.lock();
        readLock.lock();

        ArrayList<Integer> collectionId = collectionManager.getUniqueId();
        LinkedList<Product> collectionElements = collectionManager.getCollection();

        updateId=commandHandler.getUpdateId(Thread.currentThread().getName());

        if(consoleManager.getExeptionInfo(Thread.currentThread().getName())){
            consoleManager.setExeptionInfo(false);
            writeLock.unlock();
            readLock.unlock();

            return null;
        }else {

            try {
                if(!collectionId.contains(updateId)){
                    throw new FieldProductExeption("This element collection does not exist");

                }
                String login=databaseManager.getLogin(address);
                if (collectionElements.get(updateId).getLogin().equals(login)) {
                    Product product = commandHandler.getProduct(address);
                    Connection connection = DatabaseManager.getConnectionDataBase();
                    String selectSQL = "update collections set id=?, \"name\"=?, coordinatesX=?, coordinatesY=?, creationDate=?, price=?, partNumber=?, manufactureCost=?, unitOfMesure=?, ownerName=?, ownerBirthday=?, ownerWeight=?";
                    PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
                    preparedStatement.setString(1, String.valueOf(updateId));
                    preparedStatement.setString(2, product.getName());
                    preparedStatement.setString(3, String.valueOf(product.getCoordinates().getCoordinateX()));
                    preparedStatement.setString(4, String.valueOf(product.getCoordinates().getCoordinateY()));
                    preparedStatement.setString(5, String.valueOf(product.getPrice()));
                    preparedStatement.setString(6, product.getPartNumber());
                    preparedStatement.setString(7, String.valueOf(product.getCreationDate()));
                    preparedStatement.setString(8, String.valueOf(product.getManufactureCost()));
                    preparedStatement.setString(9, String.valueOf(product.getUnitOfMeasure()));
                    preparedStatement.setString(10, String.valueOf(product.getOwner().getNamePerson()));
                    preparedStatement.setString(11, String.valueOf(product.getOwner().getBirthday()));
                    preparedStatement.setString(12, String.valueOf(product.getOwner().getWeight()));
                    preparedStatement.executeUpdate();

                    //find position needed element collection if in collection id elements and replace element new product with old id and creation date, but new else field
                    indexElementCollection = collectionId.indexOf(updateId);
                    collectionElements.set(indexElementCollection, new Product(updateId, product.getName(), new Coordinates(product.getCoordinates().getCoordinateX(), product.getCoordinates().getCoordinateY()),
                            collectionElements.get(indexElementCollection).getCreationDate(), product.getPrice(), product.getPartNumber(), product.getManufactureCost(),
                            product.getUnitOfMeasure(), new Person(product.getOwner().getNamePerson(), product.getOwner().getBirthday(), product.getOwner().getWeight()),login));


                    //write new colllection in collection storage
                    collectionManager.setCollection(collectionElements);
                    preparedStatement.close();

                    return "Product updated successfully";
                }else{

                    return "Permission denied. You can update only your notation";
                }

            }catch(CommandExeption e){

                System.err.println(e.getMessage());
                return null;
            }catch (SQLException e){


                return "Unable to update product, please try again";
            }finally {
                writeLock.unlock();
                readLock.unlock();
            }

        }


    }
}
