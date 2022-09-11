package command;


import manager.ServerCollectionManager;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class History implements Commandable {
    private ServerCollectionManager collectionManager;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public History(ServerCollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(String s, InetSocketAddress address) {
        writeLock.lock();
        readLock.lock();


        LinkedList<String> collectCommand = collectionManager.getHistoryCommand(address);
        String stringObject = "";
        for (int i = 0; i < collectCommand.size(); i++) {
            stringObject += (i + 1) + "." + collectCommand.get(i) + "\n";
        }

        writeLock.unlock();
        readLock.unlock();
        return stringObject;


    }


}
