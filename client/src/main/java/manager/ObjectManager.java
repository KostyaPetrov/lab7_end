package manager;


import collection.ProductClient;

import java.io.Serializable;

public class ObjectManager implements Serializable {
    String command, login, password;
    ProductClient productClient;

    public ObjectManager(String command, ProductClient productClient, String login, String password) {
        this.command=command;
        this.productClient=productClient;
        this.login=login;
        this.password=password;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s", command, productClient, login, password);
    }

    public String getCommand(){
        return command;
    }
    public ProductClient getProduct(){
        return productClient;
    }
    public String getLogin(){return login;}
    public String getPassword(){return password;}



}
