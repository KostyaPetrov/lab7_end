package manager;


import collection.Product;

import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * class that work with collections
 */
public class ServerCollectionManager {
    LocalDate dateCreateCollection;
    ArrayList<Integer> listUniqueId, listIdUsers=new ArrayList<>();

    /**
     * main collection with elements Product
     */
    LinkedList<Product> collect;
    Map<InetSocketAddress, LinkedList<String>> mapHistoryCommand = new HashMap<>();
    LinkedList<String> collectionHistoryCommand = new LinkedList<>();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public ServerCollectionManager() {
    }



    /**
     * getter product collection
     *
     * @return Collection elements
     */
    public LinkedList<Product> getCollection() {

        return collect;

    }

    /**
     * setter product collection
     *
     * @param collect new collection elements Product, witch we get for intermediate storage
     */
    public void setCollection(LinkedList<Product> collect) {

        this.collect = collect;
    }

    public void createInitCollectionDate() {
        dateCreateCollection = LocalDate.now();
    }

    public LocalDate getDateCreateCollection() {
        return dateCreateCollection;
    }

    /**
     * Getter collection with unique id
     *
     * @return collection with unique id all elements main collection
     */
    public ArrayList<Integer> getListUniqueId() {
        return listUniqueId;
    }

    public void setListIdUser(ArrayList<Integer> listIdUser){
        this.listIdUsers=listIdUser;

    }

    public ArrayList<Integer> getListIdUser(){
        return listIdUsers;
    }

    /**
     * setter collection with unique id
     *
     * @param listUniqueId new collection unique id, witch we get for intermediate storage
     */
    public void setListUniqueId(ArrayList<Integer> listUniqueId) {
        this.listUniqueId = listUniqueId;
    }

    /**
     * getter for collection command
     *
     * @return collection of 15 last command
     */
    public LinkedList<String> getHistoryCommand(InetSocketAddress address) {
        return mapHistoryCommand.get(address);
    }

    /**
     * setter for collection command
     *
     * @param command last command, witch was execute
     */
    public void setCommand(String command, InetSocketAddress address) {
        collectionHistoryCommand = mapHistoryCommand.get(address);
        if (collectionHistoryCommand == null) {
            collectionHistoryCommand=new LinkedList<>();
        }
        //only the last 15 commands are stored, so there is a check
        if (collectionHistoryCommand.size() < 15) {

            collectionHistoryCommand.add(command);
            mapHistoryCommand.put(address, collectionHistoryCommand);
        } else {
            collectionHistoryCommand.removeFirst();
            collectionHistoryCommand.add(command);
            mapHistoryCommand.put(address, collectionHistoryCommand);
        }
    }

    ArrayList<Integer> listId = new ArrayList<>();
    public Integer getUniqueId(String table){
        writeLock.lock();
        readLock.lock();
        listId.clear();

        if(table.equals("collections")) {
            if (!getListUniqueId().isEmpty()) {
                listId.addAll(getListUniqueId());
            }
            if (listId.isEmpty()) {
                listId.add(1);
                setListUniqueId(listId);
                writeLock.unlock();
                readLock.unlock();
                return 1;
            } else {
                listId.add(Collections.max(listId) + 1);
                setListUniqueId(listId);
                writeLock.unlock();
                readLock.unlock();
                return Collections.max(listId);
            }
        }else{
            if (!getListIdUser().isEmpty()) {
                listId.addAll(getListIdUser());
            }
            if (listId.isEmpty()) {
                listId.add(1);
                setListIdUser(listId);
                writeLock.unlock();
                readLock.unlock();
                return 1;
            } else {
                listId.add(Collections.max(listId) + 1);
                setListIdUser(listId);

                writeLock.unlock();
                readLock.unlock();
                return Collections.max(listId);
            }
        }
    }
}