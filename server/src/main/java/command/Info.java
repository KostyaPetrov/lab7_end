package command;


import collection.Product;
import manager.ServerCollectionManager;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Info implements Commandable {
    private ServerCollectionManager collectionManager;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();

    private LinkedList<Product> collectionElements;

    public Info(ServerCollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(String s, InetSocketAddress address) {
        readLock.lock();


        String stringObject;
        collectionElements = collectionManager.getCollection();
        if (collectionElements.isEmpty()) {
            stringObject = "Collection is empty";
        } else {
            System.out.println("stringObject = String.format");

            stringObject = String.format("LinkedList, %s, %s\n", collectionManager.getDateCreateCollection(), collectionElements.size());
            System.out.println("before stringObject");
        }

        System.out.println("before try");
        try {
            System.out.println("In try");
            return (stringObject);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            readLock.unlock();
        }
    }
}
