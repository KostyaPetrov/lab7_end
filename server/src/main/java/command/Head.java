package command;


import manager.ServerCollectionManager;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class Head implements Commandable {
    private ServerCollectionManager collectionManager;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();

    public Head(ServerCollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(String s, InetSocketAddress address) {

        readLock.lock();

        String stringObject = collectionManager.getCollection().getFirst().toString();

        readLock.unlock();
        return stringObject;


    }
}
