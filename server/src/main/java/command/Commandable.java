package command;

import java.net.InetSocketAddress;
import java.sql.SQLException;

public interface Commandable {

    String execute(String arg, InetSocketAddress address) throws SQLException;

//    public default void execute() {
//        execute("");
//    }


}
