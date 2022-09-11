package manager;



import command.*;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    InetSocketAddress inetSocketAddress;
    protected Map<String, Commandable> map = new HashMap<>();

    public void addCommand(String key, Commandable cmd) {

        map.put(key, cmd);

    }

    public String execCommand(String key, InetSocketAddress address) throws SQLException {

        return(map.get(key).execute("", address));
    }



    public CommandManager(ServerCollectionManager collectionManager, ConsoleManager consoleManager, Serializer serializer, CommandHandler commandHandler, DatabaseManager databaseManager) {

        addCommand("help", new Help(serializer));//вывести справку по доступным командам
        addCommand("info", new Info(collectionManager));// вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)
        addCommand("show", new Show(collectionManager));//вывести в стандартный поток вывода все элементы коллекции в строковом представлении
        addCommand("add", new Add(collectionManager, consoleManager,commandHandler));// добавить новый элемент в коллекцию
        addCommand("update", new UpdateId(collectionManager, consoleManager, commandHandler, databaseManager));//обновить значение элемента коллекции, id которого равен заданному
        addCommand("remove_by_id", new RemoveId(consoleManager, collectionManager, commandHandler, databaseManager));//     : удалить элемент из коллекции по его id
        addCommand("clear", new Clear(collectionManager, databaseManager));//     : очистить коллекцию
        addCommand("head", new Head(collectionManager));//     : вывести первый элемент коллекции
        addCommand("add_if_max", new AddIfMax(collectionManager, commandHandler));//     : добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции
        addCommand("history", new History(collectionManager));//     : вывести последние 15 команд (без их аргументов)
        addCommand("remove_all_by_manufacture_cost", new RemoveWithManufactureCost(consoleManager, collectionManager, commandHandler, databaseManager));//     : удалить из коллекции все элементы, значение поля manufactureCost которого эквивалентно заданному
        addCommand("count_greater_than_unit_of_measure", new CountGreaterThanUnitMeasure(collectionManager, commandHandler));//     : вывести количество элементов, значение поля unitOfMeasure которых больше заданного
        addCommand("print_field_descending_unit_of_measure", new PrintFieldDescendingUnitOfMeasure(collectionManager));//    print_field_descending_unit_of_measure

        //addallcommands

    }

}