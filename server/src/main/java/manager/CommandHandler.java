package manager;

import collection.Coordinates;
import collection.Person;
import collection.Product;
import collection.ProductClient;

import javax.sql.XADataSource;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CommandHandler {
    ServerCollectionManager collectionManager;
    ConsoleManager consoleManager;
    ProductClient productClient;
    DatabaseManager databaseManager;
    InetSocketAddress address;

    AnswerManager response;

    ArrayList<Integer> listUniqueId = new ArrayList<>();
    String argument;
    String[] arrCommandLine;


    Map<String, String> mapCommand = new HashMap<>();

    Map<String, Map> mapThread = new HashMap<>();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public CommandHandler(ServerCollectionManager collectionManager, ConsoleManager consoleManager, DatabaseManager databaseManager) {
        this.collectionManager = collectionManager;
        this.consoleManager = consoleManager;
        this.databaseManager = databaseManager;
    }

    /**
     * processes the submitted object by separating the product, command and command argument (if any)
     *
     * @param objectManager object with command and (if any) Product object
     * @return only command
     */
    public AnswerManager unpacker(ObjectManager objectManager, CommandManager commandManager, InetSocketAddress address) throws SQLException {
        writeLock.lock();
        readLock.lock();

            String inputCommand = objectManager.getCommand();

            if (inputCommand.equals("enter") || inputCommand.equals("registration")) {
                response=new AnswerManager(databaseManager.authorization(inputCommand, objectManager.getLogin(), objectManager.getPassword(),address));
                readLock.unlock();
                writeLock.unlock();
                return response;
            } else {

                productClient = objectManager.getProduct();

                if (!inputCommand.contains(" ")) {
                    collectionManager.setCommand(inputCommand, address);

                    response=new AnswerManager(commandManager.execCommand(inputCommand, address));

                    readLock.unlock();
                    writeLock.unlock();

                    return response;

                } else {
                    arrCommandLine = inputCommand.split(" ");

                    /**
                     *put data from command line to map for pass to a method
                     */
                    mapCommand = mapThread.get(Thread.currentThread().getName());
                    if(mapCommand!=null) {
                        mapCommand.put(arrCommandLine[0], arrCommandLine[1]);
                        mapThread.put(Thread.currentThread().getName(), mapCommand);
                    }else{
                        mapCommand=new HashMap<>();
                        mapCommand.put(arrCommandLine[0], arrCommandLine[1]);
                        mapThread.put(Thread.currentThread().getName(), mapCommand);
                    }

                    collectionManager.setCommand(arrCommandLine[0], address);


                    System.out.println("I am in method unpacker2");

                    response=new AnswerManager(commandManager.execCommand(arrCommandLine[0], address));
                    readLock.unlock();
                    writeLock.unlock();
                    return response;
                }
            }

    }


    public Integer getUniqueId() {
        writeLock.lock();
        readLock.lock();


        if (!collectionManager.getUniqueId().isEmpty()) {
            listUniqueId.addAll(collectionManager.getUniqueId());
        }
        if (listUniqueId.isEmpty()) {
            listUniqueId.add(1);
            collectionManager.setUniqueId(listUniqueId);

            writeLock.unlock();
            readLock.unlock();
            return 1;
        } else {
            listUniqueId.add(Collections.max(listUniqueId) + 1);
            collectionManager.setUniqueId(listUniqueId);

            writeLock.unlock();
            readLock.unlock();
            return Collections.max(listUniqueId);
        }
    }

    public Product getProduct(InetSocketAddress address) {
        return new Product(getUniqueId(), productClient.getName(), new Coordinates(productClient.getCoordinates().getCoordinateX(), productClient.getCoordinates().getCoordinateY()),
                productClient.getCreationDate(), productClient.getPrice(), productClient.getPartNumber(),
                productClient.getManufactureCost(), productClient.getUnitOfMeasure(),
                new Person(productClient.getOwner().getNamePerson(), productClient.getOwner().getBirthday(), productClient.getOwner().getWeight()), databaseManager.getLogin(address));

    }

    public Integer getUpdateId(String thread) {

        try {
            readLock.lock();

            String number = String.valueOf(mapThread.get(thread).get("update"));
            readLock.unlock();
            return Integer.valueOf(number);
        } catch (NumberFormatException e) {
            writeLock.unlock();
            System.err.println("Id for update must be integer number");
            consoleManager.setExeptionInfo(true);
            writeLock.unlock();
            return null;
        }

    }


    public Integer getRemoveId(String thread) {
        try {
            readLock.lock();
            String number = String.valueOf(mapThread.get(thread).get("remove_by_id"));
            readLock.unlock();
            return Integer.valueOf(number);
        } catch (NumberFormatException e) {
            writeLock.lock();
            System.err.println("Id for remove must be integer number");
            consoleManager.setExeptionInfo(true);
            writeLock.unlock();
            return null;
        }

    }

    public Float getRemoveManufactureCost(String thread) {
        try {
            readLock.lock();
            String number = String.valueOf(mapThread.get(thread).get("remove_all_by_manufacture_cost"));
            readLock.unlock();
            return Float.valueOf(number);
        } catch (NumberFormatException e) {
            writeLock.lock();
            System.err.println("Remove manufacture cost must be number");
            consoleManager.setExeptionInfo(true);
            writeLock.unlock();
            return null;
        }

    }

    public String getСomparisonUnitOfMeasure(String thread) {
        readLock.lock();
        String number = String.valueOf(mapThread.get(thread).get("count_greater_than_unit_of_measure"));
        readLock.unlock();
        return number;
    }

//    public void setLogin(String login){
//        this.login=login;
//    }

}
