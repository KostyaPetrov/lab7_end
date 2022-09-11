package client;


import collection.ProductClient;
import exeption.CommandExeption;
import manager.*;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

public class Client {
    String host;
    Integer port;
    ConsoleManager consoleManager;
    InetAddress ipAddress;
    DatagramChannel clientChannel;
    Serializer serializer;
    SocketAddress socketAddress;
    CommandManager commandManager;
    String login;
    String password;

    public Client(String host, Integer port, ConsoleManager consoleManager) {
        this.host = host;
        this.port = port;
        this.consoleManager = consoleManager;
        this.serializer = new Serializer();

        try {
            this.clientChannel = DatagramChannel.open();
            this.ipAddress = InetAddress.getByName(host);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void start(CommandManager commandManager) {
        this.commandManager = commandManager;

        Collection<String> collection = Arrays.asList("help", "info", "show", "head", "history", "count_greater_than_unit_of_measure", "print_field_descending_unit_of_measure");
        String command;
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);


        String authorizationData;
        do {
            StringBuffer accountInDbBuffer = consoleManager.accountInDb();
            if(!consoleManager.getCheckerExit()) {
                authorizationData = authorization(accountInDbBuffer, byteBuffer);
                System.err.println(authorizationData);
                accountInDbBuffer.delete(0, accountInDbBuffer.length());
            }else{
                authorizationData="You have successfully logged in";
            }

        } while (!authorizationData.equals("You have successfully logged in"));


        while (!consoleManager.getCheckerExit()) {

            command = consoleManager.getCommand();
            if (!command.equals("execute_script")) {
                if (command.equals("exit")) {
                    System.out.println("Have a good day!");
                    break;
                } else if (command.equals("add") || (command.contains(" ") && (command.split(" ")[0].equals("update") || command.split(" ")[0].equals("add_if_max")))) {
                    send("add", consoleManager.getClientProduct());
                } else {
                    send("command", command);
                }
            } else {
                commandManager.execCommand(command);
            }

            /**
             * If server return to client information, that bring her out
             */


            System.out.println("Get from server:\n" + resive(byteBuffer));

        }


    }

    /**
     * Method for resiving message from server and print on console
     *
     * @param byteBuffer buffer bytes for resiving and write message from server
     */
    public String resive(ByteBuffer byteBuffer) {
        try {
            //clientChannel.socket().setSoTimeout(30000000);
            byte[] bytes = new byte[byteBuffer.limit()];
            DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
            clientChannel.socket().receive(datagramPacket);
            byteBuffer.put(datagramPacket.getData());
            AnswerManager response = (AnswerManager) serializer.deserialize(byteBuffer.array());
            byte[] charset = response.toString().getBytes(StandardCharsets.UTF_8);
            String responseUtf = new String(charset, StandardCharsets.UTF_8);
            byteBuffer.clear();
            return responseUtf;
            //clientChannel.receive(byteBuffer);
        } catch (SocketTimeoutException e) {
            return ("Server error");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * method for authorization user
     *
     * @param accountInDbBuffer-buffer with information for authorization
     * @param byteBuffer-buffer        for write resiving message
     */
    public String authorization(StringBuffer accountInDbBuffer, ByteBuffer byteBuffer) {
        send("authorization", accountInDbBuffer.toString());
        String answer = resive(byteBuffer);
        return answer;
    }

    /**
     * Method for send message to server
     *
     * @param type    type sending message(command or information for authorization)
     * @param message message which will send to server
     */
    public void send(String type, String message) {
        try {
            Object stringObject = null;
            ByteBuffer byteBuffer;

            if (type.equals("command")) {

                /**
                 * Command type check (1 or 2 components)
                 */
                if (!message.contains(" ")) {
                    if (message.equals("add") || message.equals("add_if_max")) {
                        stringObject = new ObjectManager(message, consoleManager.getClientProduct(), login, password);

                    } else {
                        stringObject = new ObjectManager(message, null,login, password);
                    }
                } else if (message.split(" ")[0].equals("update_id")) {
                    stringObject = new ObjectManager(message, consoleManager.getClientProduct(), login, password);
                } else if (message.split(" ")[0].equals("execute_script")) {
                    commandManager.execCommand("execute_script");

                } else {
                    stringObject = new ObjectManager(message, null, login, password);
                }
            } else {
                String[] accountInDb = message.split("\n");
                login=accountInDb[1];
                password=accountInDb[2];
                stringObject=new ObjectManager(accountInDb[0], null, login, password);
//                stringObject = new AuthorizationManager(accountInDb[0], accountInDb[1], accountInDb[2]);
            }
            socketAddress = new InetSocketAddress(ipAddress, port);


            byte[] serializeObject = serializer.serialize(stringObject);
            byteBuffer = ByteBuffer.wrap(serializeObject);
            clientChannel.send(byteBuffer, socketAddress);
            System.out.println("Data send");
            byteBuffer.clear();


        } catch (CommandExeption e) {
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void send(String command, ProductClient product) {
        ByteBuffer byteBuffer;
        ObjectManager stringObject;
        try {
            /**
             * Command type check (1 or 2 components)
             */
            if (!command.contains(" ")) {
                if (command.equals("add") || command.equals("add_if_max")) {
                    stringObject = new ObjectManager(command, product, login, password);

                } else {
                    stringObject = new ObjectManager(command, null, login, password);
                }
            } else if (command.split(" ")[0].equals("update")) {
                stringObject = new ObjectManager(command, product, login, password);
            } else {
                stringObject = new ObjectManager(command, null, login, password);
            }

            socketAddress = new InetSocketAddress(ipAddress, port);
            byte[] serializeObject = serializer.serialize(stringObject);
            byteBuffer = ByteBuffer.wrap(serializeObject);
            clientChannel.send(byteBuffer, socketAddress);
            System.out.println("Data send");
            byteBuffer.clear();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
