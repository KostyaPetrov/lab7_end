package manager;


import client.Client;
import collection.Coordinates;
import collection.Person;
import collection.ProductClient;
import collection.UnitOfMeasure;
import exeption.CommandExeption;
import exeption.FieldProductExeption;
import exeption.IncorrectCommandExeption;
import exeption.RecursiveScriptExeption;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ConsoleManager {


    private ClientCollectionManager clientCollectionManager;
    private Client client;

    boolean checkExit = false;
    protected Map<String, String> map = new HashMap<>();
    private String endCommand = "";
    List<String> arrCommand;
    String[] arrCommandLine;

    public ConsoleManager() {
        this.arrCommand = new ArrayList<>(
                Arrays.asList("help",
                        "add",
                        "add_if_max",
                        "info",
                        "show",
                        "remove_by_id",
                        "clear",
                        "execute_script",
                        "exit",
                        "head",
                        "history",
                        "remove_all_by_manufacture_cost",
                        "count_greater_than_unit_of_measure",
                        "print_field_descending_unit_of_measure"));

    }

    public void getClientObject(Client client, ClientCollectionManager clientCollectionManager) {
        this.client=client;
        this.clientCollectionManager=clientCollectionManager;
    }


    public String getCommand() {


        System.out.println("Enter command(for list commands enter \"help\")");

        Scanner in = new Scanner(System.in);
        try {

            String inputCommand = in.nextLine();

            if (inputCommand.isEmpty()) {
                throw new IncorrectCommandExeption("Command can not be null");
            }
            inputCommand = inputCommand.trim();


            /**
             * Command type check (1 or 2 components)
             */
            if (!inputCommand.contains(" ")) {
                /**
                 * checking for the existence of a command
                 */
                if (!arrCommand.contains(inputCommand)) {
                    throw new IncorrectCommandExeption("No such command exists");
                }

                if (inputCommand.equals("exit")) {
                    setCheckerExit(true);
                }
                return inputCommand;


            } else {

                arrCommandLine = inputCommand.split(" ");
                if (!arrCommandLine[0].equals("remove_by_id") && !arrCommandLine[0].equals("update") && !arrCommandLine[0].equals("execute_script") && !arrCommandLine[0].equals("remove_all_by_manufacture_cost") && !arrCommandLine[0].equals("remove_all_by_manufacture_cost")) {
                    throw new IncorrectCommandExeption("Invalid command format");
                }
                return inputCommand;
            }


        } catch (CommandExeption e) {
            System.err.println(e.getMessage());
            return getCommand();
        } catch (NoSuchElementException ex) {
            in = new Scanner(System.in);
            return getCommand();
        }
    }

    public void getCommand(String inputCommand) {
        String[] arrCommandLine;
        ArrayList<String> collection;
        try {
            if (inputCommand.isEmpty()) {
                throw new IncorrectCommandExeption("Command can not be null");
            }

            inputCommand = inputCommand.trim();
            /**
             * checking correct command
             */
            if (!inputCommand.contains(" ")) {
                if (!arrCommand.contains(inputCommand)) {
                    throw new IncorrectCommandExeption("\"" + inputCommand + "\"" + " command does not exist. ABOBA");
                }

                client.send("command",inputCommand);

            } else {
                arrCommandLine = inputCommand.split(" ");
                collection = clientCollectionManager.getCollectionPathToScript();
                if (!arrCommandLine[0].equals("remove_by_id") && !arrCommandLine[0].equals("execute_script") && !arrCommandLine[0].equals("remove_all_by_manufacture_cost") && !arrCommandLine[0].equals("remove_all_by_manufacture_cost")) {
                    throw new IncorrectCommandExeption("\"" + inputCommand + "\"" + " invalid command format");

                } else {
                    if (arrCommandLine[0].equals("execute_script") && collection.contains(arrCommandLine[1].trim())) {
                        clientCollectionManager.setCollectionPathToScript(new ArrayList<>());
                        throw new RecursiveScriptExeption("Recursive in file");
                    } else {
                        collection.add(arrCommandLine[1]);
                        clientCollectionManager.setCollectionPathToScript(collection);
                    }

                    client.send("command",inputCommand);

                }


            }
        } catch (CommandExeption e) {
            System.err.println(e.getMessage());
            setExeptionInfo(true);
        }
    }

    boolean exeptionInfo = false;
    /**
     * Getter information about error occurred
     *
     * @return true-if in some command was error
     * @return false-if there were no errors
     */
    public boolean getExeptionInfo() {
        return exeptionInfo;
    }
    /**
     * Setter information about error occurred
     */
    public void setExeptionInfo(boolean exeptionInfo) {
        this.exeptionInfo = exeptionInfo;
    }

    /**
     * setter checker exit command
     *
     * @param checkExit if input command is "exit", checkExit=false
     */


    public void setCheckerExit(boolean checkExit) {
        this.checkExit = checkExit;
    }

    /**
     * getter checker exit command
     */
    public boolean getCheckerExit() {
        return checkExit;
    }

    /**
     * requesting name product from the user
     * @return name for product
     */
    public String getName() {
        System.out.println("Enter name product");
        Scanner in = new Scanner(System.in);
        String inputCommand = in.nextLine();
        try {
            if (inputCommand.isEmpty()) {
                throw new FieldProductExeption("Name cannot be null");

            } else {
                return (inputCommand);
            }
        } catch (CommandExeption e) {
            System.err.println(e.getMessage());
            return getName();
        }
    }
    /**
     * requesting coordinate x from the user
     * @return coordinate x
     */
    public Long getCoordinateX() {
        System.out.println("Enter coordinates(X less then 486)");
        System.out.println("Input X");

        Scanner in = new Scanner(System.in);


        try {
            String inputCommandLine = in.nextLine();
            Long inputCommand = Long.parseLong(inputCommandLine);
            if (inputCommand > 485) {
                throw new FieldProductExeption("Coordinate X must be less then 486");
            } else if (inputCommandLine.isEmpty()) {
                throw new FieldProductExeption("Coordinate can not be null");
            } else {

                return inputCommand;

            }

        } catch (CommandExeption e) {
            System.err.println(e.getMessage());
            return getCoordinateX();

        } catch (NumberFormatException ex) {
            System.err.println("Coordinate X must be integer number");
            return getCoordinateX();
        }
    }

    /**
     * requesting coordinate y from the user
     * @return coordinate y
     */
    public Float getCoordinateY() {
        System.out.println("Input Y");
        Scanner in = new Scanner(System.in);

        try {
            String inputCommandLine = in.nextLine();
            Float inputCommand = Float.parseFloat(inputCommandLine);
            if (inputCommandLine.isEmpty()) {
                throw new FieldProductExeption("Coordinate can not be null");
            } else {
                return inputCommand;
            }
        } catch (CommandExeption e) {
            System.err.println(e.getMessage());
            return getCoordinateY();
        } catch (NumberFormatException e) {
            System.err.println("Coordinate Y must be integer number");
            return getCoordinateY();
        }
    }
    /**
     * generate date now
     * @return date now
     */
    public LocalDate getDate() {
        return LocalDate.now();
    }
    /**
     * requesting price product from the user
     * @return price product
     */
    public Integer getPrice() {
        System.out.println("Enter price product");
        Scanner in = new Scanner(System.in);
        try {
            String inputCommandLine = in.nextLine();
            Integer inputCommand = Integer.parseInt(inputCommandLine);
            if (inputCommand < 0) {
                throw new FieldProductExeption("Price must be greater or equal to zero than 0");
            } else if (inputCommandLine.isEmpty()) {
                throw new FieldProductExeption("Price can not be null");

            } else {
                return inputCommand;
            }
        } catch (CommandExeption e) {
            System.err.println(e.getMessage());
            return getPrice();
        } catch (NumberFormatException e) {
            System.err.println("Price must be integer number");
            return getPrice();
        }

    }
    /**
     * requesting part number product's from the user
     * @return part number product
     */
    public String getPartNumber() {
        System.out.println("Enter part number");
        Scanner in = new Scanner(System.in);
        try {
            String inputCommand = in.nextLine();
            if (inputCommand.length() > 23 && inputCommand.length() < 51) {

                return (inputCommand);
            } else if (inputCommand.isEmpty()) {
                throw new FieldProductExeption("Part number can not be null");

            } else {
                throw new FieldProductExeption("Length part number must be greater than 23 and less than 51");

            }
        } catch (CommandExeption e) {
            System.err.println(e.getMessage());
            return getPartNumber();
        }
    }
    /**
     * requesting manufacture cost product from the user
     * @return manufacture cost product
     */
    public Float getManufactureCost() {

        System.out.println("Enter manufacture cost");
        Scanner in = new Scanner(System.in);


        try {
            String inputCommandLine = in.nextLine();
            Float inputCommand = Float.parseFloat(inputCommandLine);
            if (inputCommandLine.isEmpty()) {
                return null;
            } else {
                return (inputCommand);
            }
        } catch (NumberFormatException e) {
            System.err.println("Manufacture cost must be an integer number");
            return getManufactureCost();
        }

    }
    /**
     * requesting  from the user
     * @return
     */
    public UnitOfMeasure getUnitOfMeasure() {

        System.out.println("Enter unit of measure(LITERS, MILLILITERS, GRAMS)");
        Scanner in = new Scanner(System.in);

        String unitOfMeasure = in.nextLine();

        try {
            if (unitOfMeasure.equals("LITERS") || unitOfMeasure.equals("MILLILITERS") || unitOfMeasure.equals("GRAMS")) {
                return UnitOfMeasure.valueOf(unitOfMeasure);
            } else {
                throw new FieldProductExeption("Unit of measure may be only LITERS, MILLILITERS or GRAMS");
            }


        } catch (CommandExeption e) {
            System.err.println(e.getMessage());
            return getUnitOfMeasure();
        }

    }
    /**
     * requesting  from the user
     * @return name user
     */
    public String getNamePerson() {
        System.out.println("Enter name person");
        Scanner in = new Scanner(System.in);
        String inputCommand = in.nextLine();
        try {
            if (inputCommand.isEmpty()) {
                throw new FieldProductExeption("Name person can not be null");
            } else {
                return inputCommand;
            }
        } catch (CommandExeption e) {
            System.err.println(e.getMessage());
            return getNamePerson();
        }

    }
    /**
     * requesting  from the user
     * @return birthday person
     */
    public LocalDateTime getBirthdayPerson() {
        System.out.println("Enter birthday person(\"yyyy-MM-dd HH:mm\")");
        Scanner in = new Scanner(System.in);


        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String inputCommand = in.nextLine();
        try {
            if (inputCommand.isEmpty()) {
                return null;
            } else {
                return (LocalDateTime.parse(inputCommand, format));
            }
        } catch (DateTimeParseException e) {
            System.err.println("Birthday date must match the format \"yyyy-MM-dd HH:mm\" or be null");
            return getBirthdayPerson();
        }
    }
    /**
     * requesting  from the user
     * @return weight person
     */
    public Double getWeightPerson() {
        System.out.println("Enter weight person");
        Scanner in = new Scanner(System.in);
        String inputCommandLine = in.nextLine();
        try {
            Double inputCommand = Double.parseDouble(inputCommandLine);
            if (inputCommandLine.isEmpty()) {
                throw new FieldProductExeption("Weight person can not be null");
            } else if (inputCommand <= 0) {
                throw new FieldProductExeption("Weight person must be grate then 0");
            } else {
                return inputCommand;
            }
        } catch (NumberFormatException e) {
            System.err.println("Weight person must be a number");
            return getWeightPerson();
        } catch (CommandExeption ex) {
            System.err.println(ex.getMessage());
            return getWeightPerson();
        }

    }

    public ProductClient getClientProduct() {

        return new ProductClient(getName(), new Coordinates(getCoordinateX(), getCoordinateY()), getDate(), getPrice(), getPartNumber(), getManufactureCost(), getUnitOfMeasure(), new Person(getNamePerson(), getBirthdayPerson(), getWeightPerson()));


    }

    /**
     * Prompts the user for login information to the database
     * @return Data for enter or registration to account in database or null, if user input exit or ctrl+c
     */
    public StringBuffer accountInDb(){
        StringBuffer accountData = new StringBuffer();
        System.out.println("Do you want enter to account(\"enter\") or registration new account(\"registration\")\n(\"exit\" for to exit the program)");
        Scanner in = new Scanner(System.in);
        try {
            String typeAccount = in.nextLine();
            if(typeAccount.equals("exit")){
                setCheckerExit(true);
                return null;
            }else if(!typeAccount.equals("enter") && !typeAccount.equals("registration")) {
                throw new CommandExeption("You input invalid command");
            }else{
                accountData.append(typeAccount + "\n");
                accountData.append(getLogin() + "\n");
                accountData.append(getPassword() + "\n");
                return accountData;
            }

        }catch (CommandExeption e){
            System.err.println(e.getMessage());
            return accountInDb();
        }catch (NoSuchElementException ex) {
            in = new Scanner(System.in);
            setCheckerExit(true);
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
        String s= DatatypeConverter.printHexBinary(digest).toUpperCase();
        return s;
    }

    public String getLogin(){
        System.out.println("Enter your login");
        try {
            Scanner in = new Scanner(System.in);
            String login = in.nextLine();
            login = login.trim();
            if (login.equals("")) {
                throw new CommandExeption("You input invalid login");
            }
            return login;
        }catch (CommandExeption e){
            System.err.println(e.getMessage());
            return getLogin();
        }
    }

    public String getPassword(){
        System.out.println("Enter your password");
        try {
            Scanner in = new Scanner(System.in);
            String password = in.nextLine();
            password = password.trim();
            if (password.equals("")) {
                throw new CommandExeption("You input invalid password");
            }
            String hashingPassword=hashingPassword(password);
            return hashingPassword;
        }catch (CommandExeption e){
            System.err.println(e.getMessage());
            return getPassword();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return getPassword();
        }
    }



    public String getPathScriptFile() {
        return map.get("execute_script");
    }

    public void setEndScriptCommand(String endCommand) {
        this.endCommand = endCommand;
    }

    public String getEndScriptCommand() {
        return endCommand;
    }

    public void closeScan() {
        Scanner in = new Scanner(System.in);
        in.close();
    }


}
