package exeption;


public class DatabaseExeption extends CollectionExeption {
    public DatabaseExeption(String s){
        super(s);
    }
    public DatabaseExeption(){super("database error");}
}
