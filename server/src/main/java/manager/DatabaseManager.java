package manager;
import exeption.ConnectionDataBaseExeption;
import exeption.DatabaseExeption;

import javax.xml.bind.DatatypeConverter;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class DatabaseManager {
    ServerCollectionManager collectionManager;
    Map<InetSocketAddress,String> mapLogin=new HashMap<>();
    ArrayList<Integer>listLoginId=new ArrayList<>();
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    String url;
    String user;
    String password;
    static Connection connection;
    static final String DB_URL = "jdbc:postgresql://pg:5432/studs";//jdbc:postgresql://localhost:5432/collection
    static final String USER = "s339742";//kostya
    static final String PASS = "eMgmoDoZhCcsWa62";//12345
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public DatabaseManager(ServerCollectionManager collectionManager, String url, String user, String password){
        this.collectionManager=collectionManager;
        this.url=url;
        this.user=user;
        this.password=password;
    }

    public void connectionDB() throws DatabaseExeption {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            create(connection);
            System.out.println("PostgreSQL JDBC Driver successfully connected");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ConnectionDataBaseExeption("Database connection error");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new DatabaseExeption("PostgreSQL JDBC Driver is not found. Include it in your library path");
        }
    }

    public void create(Connection connection) throws SQLException {
        //language=SQL

        String createUserTableSQL = "CREATE TABLE IF NOT EXISTS \"authorization\"" +
                "(\"id\" INTEGER PRIMARY KEY, " +
                "\"username\" CHARACTER VARYING NOT NULL,"+
                "\"password\" CHARACTER VARYING NOT NULL);";


        String createCollectionTableSQL = "CREATE TABLE IF NOT EXISTS \"collections\"" +
                "(\"id\" INTEGER PRIMARY KEY, " +
                "\"name\" CHARACTER VARYING, "+
                "\"coordinatex\" BIGSERIAL, "+
                "\"coordinatey\" REAL, "+
                "\"creationdate\" DATE, "+
                "\"price\" INTEGER, "+
                "\"partnumber\" CHARACTER VARYING, "+
                "\"manufacturecost\" REAL, "+
                "\"unitofmesure\" CHARACTER VARYING, "+
                "\"ownername\" CHARACTER VARYING, "+
                "\"ownerbirthday\" timestamp without time zone, "+
                "\"ownerweight\" DOUBLE PRECISION, "+
                "\"username\" CHARACTER VARYING);";



        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(createUserTableSQL);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        preparedStatement.executeUpdate();
        System.out.println("Table authorization added");
        preparedStatement.close();

        PreparedStatement preparedStatement2 = null;
        try {
            preparedStatement2 = connection.prepareStatement(createCollectionTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        preparedStatement2.executeUpdate();
        System.out.println("Table collections added");
        preparedStatement2.close();




    }

    public static Connection getConnectionDataBase(){
        return connection;
    }

    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    /**
     * Authorization users in database
     */
    public String authorization(String type, String login, String password, InetSocketAddress address)  {

        try {
            writeLock.lock();
            readLock.lock();

            boolean checkerLogin = false;
            boolean checkerEnter = false;

            Statement statement = connection.createStatement();
            /**
             * if user want enter to account, search notation with this login and password
             * if user want register new account, search that such a login no longer exists
             */
            if (type.equals("enter")) {

                ResultSet reset = statement.executeQuery("select \"id\", \"username\", \"password\" from \"authorization\"");
                if(reset==null){
                    reset.close();
                    statement.close();


                    return "This account does not exist. Enter \"registration\" to register a new account ";
                }
                while (reset.next()) {
                    String id=reset.getString(1);
                    String databaseLogin=reset.getString(2);
                    String databasePassword=reset.getString(3);
                    if (databaseLogin.equals(login)) {
                        checkerLogin = true;
                        if (databasePassword.equals(password)) {
//                            this.log = login;
                            mapLogin.put(address,login);
                            checkerEnter = true;
                            break;
                        }
                    }
                }

                if (checkerEnter) {
                    reset.close();
                    statement.close();

                    return "You have successfully logged in";
                } else if (!checkerLogin) {
                    reset.close();
                    statement.close();

                    return "This account does not exist. Enter \"registration\" to register a new account ";
                } else {
                    reset.close();
                    statement.close();

                    return "You entered the wrong password";
                }
            } else {
                ResultSet reset = statement.executeQuery("select \"username\" from \"authorization\"");
                while (reset.next()) {
                    if (reset.getString(1).equals(login)) {
                        checkerLogin = true;
                        reset.close();
                        statement.close();
                        break;
                    }
                }
                if (checkerLogin) {
                    reset.close();
                    statement.close();



                    return "This login already exists";
                } else {

                    String selectSQL = "insert into \"authorization\" (\"id\", \"username\", \"password\") values (?, ?, ?)";

                    PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
                    Integer id=collectionManager.getUniqueId("authorization");
                    preparedStatement.setInt(1, id);
                    preparedStatement.setString(2, login);
                    preparedStatement.setString(3, password);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                    reset.close();
                    statement.close();

                    mapLogin.put(address,login);
                    listLoginId=collectionManager.getListIdUser();
                    listLoginId.add(id);
                    collectionManager.setListIdUser(listLoginId);
                    listLoginId.clear();

                    return "You have successfully logged in";
                }


            }

        } catch (SQLException e) {

            e.printStackTrace();
            return "Error connecting to database";
        } finally {
            writeLock.unlock();
            readLock.unlock();
        }


    }





    /**
     * Getter user login
     */
    public String getLogin(InetSocketAddress address){
        return mapLogin.get(address);
    }

    public ArrayList<String> readUserDatabase(String request){
        ArrayList<String> dataFromBase = new ArrayList<>();

        try {
            //Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/collection", "kostya", "12345");
            Statement statement=connection.createStatement();
            ResultSet reset=statement.executeQuery(request);

            while (reset.next()){

                String aaa=reset.getString(1);
                dataFromBase.add(aaa);

            }
            reset.close();
            statement.close();
            return dataFromBase;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Send request to database and get data
     */
    public ArrayList<String> readCollectionDatabase(String request){
        ArrayList<String> dataFromBase = new ArrayList<>();

        try {
            //Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/collection", "kostya", "12345");
            Statement statement=connection.createStatement();
            ResultSet reset=statement.executeQuery(request);

            while (reset.next()){

                for(int column=1; column<14;column++) {
                    String aaa=reset.getString(column);
                    dataFromBase.add(aaa);
                }
            }
            reset.close();
            statement.close();
            return dataFromBase;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
