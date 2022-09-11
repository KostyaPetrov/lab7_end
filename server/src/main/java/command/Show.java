package command;


import collection.Product;
import manager.ServerCollectionManager;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Show implements Commandable {
    private ServerCollectionManager collectionManager;
    private LinkedList<Product> collectionElements;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();

    public Show(ServerCollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(String s, InetSocketAddress address) {
        readLock.lock();

        StringBuilder stringObject = new StringBuilder();
        collectionElements = collectionManager.getCollection();
        for (int i = 0; i < collectionElements.size(); i++) {
            stringObject.append(collectionElements.get(i)).append("\n");
        }

        readLock.unlock();
        return stringObject.toString();

    }

}

