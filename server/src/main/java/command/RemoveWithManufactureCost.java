package command;

import collection.Product;
import manager.CommandHandler;
import manager.ConsoleManager;
import manager.DatabaseManager;
import manager.ServerCollectionManager;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class RemoveWithManufactureCost implements Commandable{
    private ConsoleManager consoleManager;
    private ServerCollectionManager collectionManager;
    CommandHandler commandHandler;
    private Float removeCost;
    private DatabaseManager databaseManager;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public RemoveWithManufactureCost(ConsoleManager consoleManager, ServerCollectionManager collectionManager, CommandHandler commandHandler, DatabaseManager databaseManager){
        this.consoleManager=consoleManager;
        this.collectionManager=collectionManager;
        this.commandHandler=commandHandler;
        this.databaseManager=databaseManager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product that = (Product) o;
        return  this.removeCost== that.getManufactureCost();
    }

    @Override
    public int hashCode() {
        return this.removeCost.hashCode();
    }

    @Override
    public String execute(String arg, InetSocketAddress address) {
        writeLock.lock();
        readLock.lock();


        try {
            LinkedList<Product> collectionElements = collectionManager.getCollection();
            int count1=0;
            int count2=0;
            removeCost = commandHandler.getRemoveManufactureCost(Thread.currentThread().getName());
            if (consoleManager.getExeptionInfo(Thread.currentThread().getName())) {
                consoleManager.setExeptionInfo(false);

                return null;
            } else {

                for (int i = 0; i < collectionElements.size(); i++) {
                    if (collectionElements.get(i).getLogin() == databaseManager.getLogin(address)) {
                        count1++;
                        if (removeCost.equals(collectionElements.get(i).getManufactureCost())) {
                            count2++;
                            Connection connection = DatabaseManager.getConnectionDataBase();
                            String selectSQL = "delete from collections where manufactureCost= ?";
                            PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
                            preparedStatement.setString(1, String.valueOf(removeCost));
                            preparedStatement.executeUpdate();

                            collectionElements.remove(i);
                            //for the correct output of the final collection
                            i = i - 1;

                            preparedStatement.close();
                            collectionManager.setCollection(collectionElements);


                            return "Data deleted successfully";
                        }
                    }

                }
                if(count1==0){

                    return "Permission denied. You can remove only your notation";
                }else if(count2==0){

                    return "no such products";
                }
            }

            return null;

        }catch (SQLException e){
            return "failed to run this command. Try again";
        }finally {
            writeLock.unlock();
            readLock.unlock();
        }
    }
}
