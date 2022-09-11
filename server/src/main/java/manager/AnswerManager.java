package manager;

import java.io.Serializable;

public class AnswerManager implements Serializable {
    String stringObject;
    public AnswerManager(String stringObject){
        this.stringObject=stringObject;
    }
    @Override
    public String toString(){
        return String.format("%s", stringObject);
    }
}
