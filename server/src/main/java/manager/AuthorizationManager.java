package manager;


import java.io.Serializable;

/**
 * Create object with data for enter in database
 */
public class AuthorizationManager implements Serializable {
    String typeEnter;
    String login;
    String  password;
    public AuthorizationManager(String typeEnter, String login, String password){
        this.typeEnter=typeEnter;
        this.login=login;
        this.password=password;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s", typeEnter, login, password);
    }

    public String getTypeEnter(){
        return typeEnter;
    }
    public String getLogin(){
        return login;
    }
    public String getPassword(){return password;}
}
