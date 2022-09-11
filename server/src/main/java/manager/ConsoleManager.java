package manager;


import java.util.HashMap;
import java.util.Map;

public class ConsoleManager {


    protected Map<String, Boolean> map = new HashMap<>();

    public ConsoleManager() {

    }




    /**
     * Getter information about error occurred
     *
     * @return true-if in some command was error
     * @return false-if there were no errors
     */
    public Boolean getExeptionInfo(String threadName) {
        Boolean checkException=map.get(threadName);
        if(checkException==null){
            checkException=false;
        }
        return checkException;
    }

    /**
     * Setter information about error occurred
     */
    public void setExeptionInfo(Boolean exeptionInfo) {
        map.put(Thread.currentThread().getName(), exeptionInfo);
    }
}
