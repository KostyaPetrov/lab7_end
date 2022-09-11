package manager;
import exeption.ConnectionDataBaseExeption;
import exeption.DatabaseExeption;

import javax.xml.bind.DatatypeConverter;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class DatabaseManager {
    ServerCollectionManager collectionManager;
    Map<InetSocketAddress,String> mapLogin=new HashMap<>();
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    String url;
    String user;
    String password;
    static Connection connection;
    static final String DB_URL = "jdbc:postgresql://localhost:5432/collection";
    static final String USER = "kostya";
    static final String PASS = "12345";
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
            System.out.println("PostgreSQL JDBC Driver successfully connected");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ConnectionDataBaseExeption("Database connection error");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new DatabaseExeption("PostgreSQL JDBC Driver is not found. Include it in your library path");
        }
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
            String hashPassword = hashingPassword(password);

            Statement statement = connection.createStatement();
            /**
             * if user want enter to account, search notation with this login and password
             * if user want register new account, search that such a login no longer exists
             */
            if (type.equals("enter")) {
                ResultSet reset = statement.executeQuery("select \"userName\", \"password\" from \"authorization\"");
                while (reset.next()) {
                    String re=reset.getString(1);
                    String re2=reset.getString(2);
                    if (re.equals(login)) {
                        checkerLogin = true;
                        if (re2.equals(hashPassword)) {
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
                } else if (checkerLogin) {
                    reset.close();
                    statement.close();


                    return "This account does not exist. Enter \"registration\" to register a new account ";
                } else {
                    reset.close();
                    statement.close();


                    return "You entered the wrong password";
                }
            } else {
                ResultSet reset = statement.executeQuery("select \"userName\" from \"authorization\"");
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
                    String selectSQL = "insert into \"authorization\" (\"userName\", \"password\") values (?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
                    preparedStatement.setString(1, login);
                    preparedStatement.setString(2, hashingPassword(password));
                    preparedStatement.executeUpdate();

                    preparedStatement.close();
                    reset.close();
                    statement.close();




                    return "You have successfully logged in";
                }


            }

        }catch (NoSuchAlgorithmException e){


            return "Password encryption error, please try again";
        }catch (SQLException e) {


            return "Error connecting to database";
        }finally {
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

    /**
     * Send request to database and get data
     */
    public ArrayList<String> readDatabase(String request){
        ArrayList<String> dataFromBase = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/collection", "kostya", "12345");
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

    /**
     * Method for hashing password with algorithm MD5
     * @param password user's password for authorization in database
     * @return hashed password as string
     * @throws NoSuchAlgorithmException Something exeption. Intellegent Idea say that needed
     */
    public String hashingPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        return DatatypeConverter
                .printHexBinary(digest).toUpperCase();
    }
}
