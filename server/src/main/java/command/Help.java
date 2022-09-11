package command;



import manager.Serializer;

import java.net.InetSocketAddress;


public class Help implements Commandable {
    Serializer serializer;



    public Help(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public String execute(String arg, InetSocketAddress address) {
        String stringObject = "\"help\" : \"display help on available commands\"\n" +
                "\"info\" : print information about the collection to the standard output stream (type, initialization date, number of elements, etc.)\n" +
                "\"show\" : print to standard output all elements of the collection in string representation\n" +
                "\"add\" : add a new element to the collection\n" +
                "\"update\" id {element} : update the value of the collection element whose id is equal to the given one\n" +
                "\"remove_by_id\" id : remove element from collection by its id\n" +
                "\"clear\" : clear the collection\n" +
                "\"execute_script\" file_name : read and execute the script from the specified file. The script contains commands in the same form in which they are entered by the user in interactive mode..\n" +
                "\"exit\" : terminate program (without saving to file)\n" +
                "\"head\" : get the first element of the collection\n" +
                "\"add_if_max\" {element} : add a new element to the collection if its value is greater than the value of the largest element in this collection\n" +
                "\"history\" : print the last 15 commands (without their arguments)\n" +
                "\"remove_all_by_manufacture_cost\" manufactureCost : remove from the collection all elements whose manufactureCost field value is equivalent to the given one\n" +
                "\"count_greater_than_unit_of_measure\" unitOfMeasure : display the number of elements whose unitOfMeasure field value is greater than the given one\n" +
                "\"print_field_descending_unit_of_measure\" : display unitOfMeasure field values of all elements in descending order";
//        String stringObject = "\"help\" : \"вывести справку по доступным командам\"\n" +
//                "\"info\" : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
//                "\"show\" : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
//                "\"add\" : добавить новый элемент в коллекцию\n" +
//                "\"update\" id {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
//                "\"remove_by_id\" id : удалить элемент из коллекции по его id\n" +
//                "\"clear\" : очистить коллекцию\n" +
//                "\"save\" : сохранить коллекцию в файл\n" +
//                "\"execute_script\" file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
//                "\"exit\" : завершить программу (без сохранения в файл)\n" +
//                "\"head\" : вывести первый элемент коллекции\n" +
//                "\"add_if_max\" {element} : добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции\n" +
//                "\"history\" : вывести последние 15 команд (без их аргументов)\n" +
//                "\"remove_all_by_manufacture_cost\" manufactureCost : удалить из коллекции все элементы, значение поля manufactureCost которого эквивалентно заданному\n" +
//                "\"count_greater_than_unit_of_measure\" unitOfMeasure : вывести количество элементов, значение поля unitOfMeasure которых больше заданного\n" +
//                "\"print_field_descending_unit_of_measure\" : вывести значения поля unitOfMeasure всех элементов в порядке убывания";
        return stringObject;


    }

}
