package command;


import collection.Product;
import manager.ServerCollectionManager;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class PrintFieldDescendingUnitOfMeasure implements Commandable {
    private ServerCollectionManager collectionManager;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();


    public PrintFieldDescendingUnitOfMeasure(ServerCollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(String s, InetSocketAddress address) {
        readLock.lock();
        String stringObject = "";
        ArrayList<String> arrayUnitOfMesure = new ArrayList<>();
        for (Product element : collectionManager.getCollection()) {
            arrayUnitOfMesure.add(element.getUnitOfMeasure().getWord());
        }
        Collections.sort(arrayUnitOfMesure, Collections.reverseOrder());

        for (int i = 0; i < arrayUnitOfMesure.size(); i++) {
            stringObject = arrayUnitOfMesure.get(i);
        }

        readLock.unlock();
        return stringObject;

    }
}
