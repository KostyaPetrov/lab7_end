package manager;

import java.util.ArrayList;

public class ClientCollectionManager {
    private ArrayList<String> collectionPathToScript=new ArrayList<>();
    public ArrayList<String> getCollectionPathToScript(){
        return collectionPathToScript;
    }

    public void setCollectionPathToScript(ArrayList<String> collectionPathToScript){
        this.collectionPathToScript=collectionPathToScript;
    }
}
