package manager;


import collection.ProductClient;

import java.io.Serializable;

public class ObjectManager implements Serializable {
    String command, login, password;
    ProductClient productClient;

    public ObjectManager(String command, ProductClient productClient, String login, String password) {
        this.command = command;
        this.productClient = productClient;
        this.login = login;
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s", command, productClient, login, password);

//        String inputCommand = consoleManager.getCommand();
//        if (!inputCommand.contains(" ")) {
//            if (inputCommand.equals("add") || inputCommand.equals("add_if_max")) {
//                return String.format("%s, %s", inputCommand, consoleManager.getProduct());
//            } else {
//                return String.format("%s, %s", inputCommand, null);
//            }
//        } else if (inputCommand.split(" ")[0].equals("update")) {
//            return String.format("%s, %s", inputCommand, consoleManager.getProduct());
//        } else {
//            return String.format("%s, %s", inputCommand, null);
//        }

//            /**
//             * serialization of the collection object and return
//             */
//            if (inputCommand.equals("add") || inputCommand.equals("add_if_max")) {
//                ByteArrayOutputStream boas = new ByteArrayOutputStream();
//                ObjectOutputStream ois = new ObjectOutputStream(boas);
//                ois.writeObject(consoleManager.getProduct());
//                return boas.toByteArray();
//            }

    }

    public String getCommand() {
        return command;
    }

    public ProductClient getProduct() {
        return productClient;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }


}
