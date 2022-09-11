package command;


import collection.Product;
import manager.CommandHandler;
import manager.ServerCollectionManager;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CountGreaterThanUnitMeasure implements Commandable {
    private ServerCollectionManager collectionManager;
    private CommandHandler commandHandler;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public CountGreaterThanUnitMeasure(ServerCollectionManager collectionManager, CommandHandler commandHandler) {
        this.collectionManager = collectionManager;
        this.commandHandler=commandHandler;
    }

    @Override
    public String execute(String s, InetSocketAddress address) {
        writeLock.lock();
        readLock.lock();


        LinkedList<Product> collectionElements = collectionManager.getCollection();
        String comparisonUnitOfMeasure = commandHandler.getСomparisonUnitOfMeasure(Thread.currentThread().getName());
        int count = 0;
        for (Product element : collectionElements) {
            if (comparisonUnitOfMeasure.compareTo(element.getUnitOfMeasure().getWord()) < 0) {
                count += 1;
            }
        }
        String stringObject="In collection " + count + " elements great than " + comparisonUnitOfMeasure;

        writeLock.unlock();
        readLock.unlock();
        return stringObject;


    }
}
