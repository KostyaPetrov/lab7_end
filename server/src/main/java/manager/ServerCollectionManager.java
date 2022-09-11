package manager;


import collection.Product;

import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * class that work with collections
 */
public class ServerCollectionManager {
    LocalDate dateCreateCollection;
    ArrayList<Integer> listUniqueId;
    /**
     * main collection with elements Product
     */
    LinkedList<Product> collect;
    Map<InetSocketAddress, LinkedList<String>> mapHistoryCommand = new HashMap<>();
    LinkedList<String> collectionHistoryCommand = new LinkedList<>();

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
    public ArrayList<Integer> getUniqueId() {
        return listUniqueId;
    }

    /**
     * setter collection with unique id
     *
     * @param listUniqueId new collection unique id, witch we get for intermediate storage
     */
    public void setUniqueId(ArrayList<Integer> listUniqueId) {
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


}