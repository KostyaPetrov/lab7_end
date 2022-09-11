package server;


import collection.*;
import exeption.*;
import manager.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {
    ConsoleManager consoleManager;
    ServerCollectionManager collectionManager;
    CommandManager commandManager;
    CommandHandler commandHandler;
    Serializer serializer;
    DatagramChannel server;
    InetSocketAddress inetSocketAddress;
    SocketAddress remoteAddress;
    DatabaseManager databaseManager;
    ObjectManager objectManager;
    int servisePort;
    private ExecutorService receiverThreadPool;
    private ExecutorService requestHandlerThreadPool;
    private ExecutorService senderThread;

    private Selector selector;
    public final int MAX_CLIENT = 10;
    private volatile boolean running;

    private Queue<Map.Entry<InetSocketAddress, ObjectManager>> requestQueue;
    private Queue<Map.Entry<InetSocketAddress, AnswerManager>> responseQueue;

    public Server(ConsoleManager consoleManager, ServerCollectionManager collectionManager, Serializer serializer, CommandHandler commandHandler, CommandManager commandManager, DatabaseManager databaseManager, int servisePort) throws DatabaseExeption, ConnectionExeption {
        this.consoleManager = consoleManager;
        this.collectionManager = collectionManager;
        this.serializer = serializer;
        this.commandHandler = commandHandler;
        this.commandManager = commandManager;
        this.databaseManager = databaseManager;
        this.servisePort = servisePort;
        init();
    }


    public void init() throws DatabaseExeption, ConnectionExeption {
        running = true;
        databaseManager.connectionDB();
        receiverThreadPool = Executors.newFixedThreadPool(MAX_CLIENT);
        requestHandlerThreadPool = Executors.newFixedThreadPool(MAX_CLIENT);
        senderThread = Executors.newSingleThreadExecutor();
        requestQueue = new ConcurrentLinkedQueue<>();
        responseQueue = new ConcurrentLinkedQueue<>();

        addDataToCollection(databaseManager.readDatabase("select * from collections"));
        collectionManager.createInitCollectionDate();

        host();
    }

    public void host() throws ConnectionExeption {

        try {
            if (server != null && server.isOpen()) server.close();
            server = DatagramChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(servisePort));
            selector = Selector.open();
            server.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

        } catch (AlreadyBoundException e) {
            throw new PortAlreadyInUseException();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new InvalidPortException();
        } catch (IOException e) {
            throw new ConnectionExeption("something went wrong during server initialization");
        }
    }


    public void run() {

        while (running) {

            try {
                selector.select();
            } catch (IOException e) {
                continue;
            }
            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();

            while (selectedKeys.hasNext()) {
                SelectionKey key = selectedKeys.next();

                if (!key.isValid()) {
                    continue;
                }
//                if(key.isConnectable()){
//                    System.out.println("Connected");
//                    continue;
//                }
//                System.out.println("before isReadable");
//                System.out.println(key.isReadable());
//                System.out.println(key.isWritable());
//                System.out.println(key.isValid());
//                System.out.println(key.isAcceptable());
//                System.out.println(key.isConnectable());
//                System.out.println("#######################################################");
                if (key.isReadable()) {
                    receiverThreadPool.submit(new Receiver());
                    continue;
                }
                if (key.isWritable() && responseQueue.size() > 0) {
                    senderThread.submit(new Sender(responseQueue.poll()));
                }
                selectedKeys.remove();
            }
            if (requestQueue.size() > 0) {
                requestHandlerThreadPool.submit(new Request(requestQueue.poll()));
            }
            Scanner scanner = new Scanner(System.in);

            try {
                if (System.in.available() > 0) {
                    String str = scanner.next();

                    if (str.equals("exit")) {
                        running = false;
                        databaseManager.closeConnection();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


//
//
//
//
//        try {
//
//            ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
//
//
//            boolean checkerExit = false;
//            System.out.println("Waiting for a client to connect...");
//
//
//            String statusConnectDb;
//            do {
//
//                AuthorizationManager authorizationManager = (AuthorizationManager) serializer.deserialize(recive().array());
//                statusConnectDb = databaseManager.authorization(authorizationManager.getTypeEnter(), authorizationManager.getLogin(), authorizationManager.getPassword());
//                send(statusConnectDb);
//                byteBuffer.clear();
//            } while (!statusConnectDb.equals("You have successfully logged in"));
//            addDataToCollection(databaseManager.readDatabase("select * from collections"));
//            collectionManager.createInitCollectionDate();
//
//            while (!checkerExit) {
//                try {
//                    //                   server.socket().setSoTimeout(1000000000);
//
//                    ObjectManager objectManager = (ObjectManager) serializer.deserialize(recive().array());
//                    this.objectManager = objectManager;
//                    System.out.println("Get data :" + objectManager);
//                    System.out.println("Command :" + objectManager.getCommand());
//                    System.out.println("Product :" + objectManager.getProduct());
//
//
//                    try {
//
//
//                        send(commandManager.execCommand(commandHandler.unpacker(objectManager)));
//
//
//                    } catch (NullPointerException ignored) {
//
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                    byteBuffer.clear();
//
//                } catch (SocketTimeoutException e) {
//                    if (System.in.available() > 0) {
//                        Scanner scanner = new Scanner(System.in);
//                        String str = scanner.next();
//                        if (str.equals("exit")) {
//                            checkerExit = true;
//                        }
//                    }
//                }
//            }
//        } catch (BindException e) {
//            System.out.println("This port already used");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
    }


//    /**
//     * Send data to Client
//     *
//     * @param stringObject Message for user
//     * @throws IOException
//     */

//    public void send(String stringObject) throws IOException {
//        AnswerManager answerObject = new AnswerManager(stringObject);
//        byte[] serializeObject = serializer.serialize(answerObject);
//        ByteBuffer byteBuffer = ByteBuffer.wrap(serializeObject);
//
//
//        server.send(byteBuffer, remoteAddress);
//        System.out.println("Data send to client");
//
//    }


    private class Receiver implements Runnable {
        public void run() {

            try {
                receive();
            } catch (ConnectionExeption e) {
                e.printStackTrace();
            } catch (InvalidReceivedDataException e) {
                e.printStackTrace();
            }

        }
    }

    public void receive() throws ConnectionExeption, InvalidReceivedDataException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
        InetSocketAddress clientAddress = null;
        ObjectManager request = null;
        try {
            clientAddress = (InetSocketAddress) server.receive(byteBuffer);
            if (clientAddress == null) return; //no data to read
        } catch (ClosedChannelException e) {
            throw new ClosedConnectionException();
        } catch (IOException e) {
            throw new ConnectionExeption("something went wrong during receiving request");
        }
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteBuffer.array()));
            request = (ObjectManager) objectInputStream.readObject();

            System.out.println("Get data :" + request + "\n" +
                    "Command :" + request.getCommand() + "\n" +
                    "Product :" + request.getProduct() + "\n" +
                    "Owner :" + request.getLogin());

        } catch (ClassNotFoundException | ClassCastException | IOException e) {
            e.printStackTrace();
            throw new InvalidReceivedDataException();
        }

        requestQueue.offer(new AbstractMap.SimpleEntry<>(clientAddress, request));

    }

    private class Sender implements Runnable {
        private final AnswerManager response;
        private final InetSocketAddress address;

        public Sender(Map.Entry<InetSocketAddress, AnswerManager> responseEntry) {
            response = responseEntry.getValue();
            address = responseEntry.getKey();
        }

        public void run() {
            try {
                send(address, response);
                System.out.println("Response to the client "+databaseManager.getLogin(address)+":"+response+"\n\n");
            } catch (ConnectionExeption e) {
                e.printStackTrace();
            }
        }
    }

    public void send(InetSocketAddress clientAddress, AnswerManager response) throws ConnectionExeption {
        if (clientAddress == null) throw new InvalidAddressException("no client address found");
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024 * 1024);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(response);
            server.send(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()), clientAddress);

        } catch (IOException e) {
            throw new ConnectionExeption("something went wrong during sending response");
        }

    }


    private class Request implements Runnable {
        private final ObjectManager request;
        private final InetSocketAddress address;

        public Request(Map.Entry<InetSocketAddress, ObjectManager> requestEntry) {
            request = requestEntry.getValue();
            address = requestEntry.getKey();
        }

        public void run() {
            try {
                handleRequest(address, request);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private void handleRequest(InetSocketAddress address, ObjectManager request) throws SQLException {

        responseQueue.offer(new AbstractMap.SimpleEntry<>(address, commandHandler.unpacker(request, commandManager, address)));

    }


    public ProductClient getClientProduct() {
        return objectManager.getProduct();
    }

    /**
     * Receiving data from the client
     * //     * @param byteBuffer Byte buffer for writing information from the client
     *
     * @return byte buffer with information from the client in serialized form
     * @throws IOException
     */
//    public ByteBuffer recive() throws IOException {
//        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
//        byte[] bytes = new byte[byteBuffer.limit()];
//        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
//        server.socket().receive(datagramPacket);
//        byteBuffer.put(datagramPacket.getData());
//        this.remoteAddress = datagramPacket.getSocketAddress();
//        return byteBuffer;
//    }

    /**
     * Add data from database to collect element
     */
    public void addDataToCollection(ArrayList<String> arrayData) {
        LinkedList<Product> collectionElement = new LinkedList<>();
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (arrayData != null) {
            for (int i = 0; i < arrayData.size(); i++) {
                collectionElement.add(new Product(Integer.parseInt(arrayData.get(i)), arrayData.get(i + 1),
                        new Coordinates(Long.parseLong(arrayData.get(i + 2)), Float.parseFloat(arrayData.get(i + 3))),
                        LocalDate.parse(arrayData.get(i + 4)), Integer.parseInt(arrayData.get(i + 5)), arrayData.get(i + 6),
                        Float.valueOf(arrayData.get(i + 7)), UnitOfMeasure.valueOf(arrayData.get(i + 8)), new Person(arrayData.get(i + 9),
                        parserBirthdayTime(arrayData.get(i + 10)), Double.valueOf(arrayData.get(i + 11))), arrayData.get(i + 12)));


                arrayList.add(Integer.parseInt(arrayData.get(i)));

                i += 12;
            }
        }
        collectionManager.setCollection(collectionElement);
        collectionManager.setUniqueId(arrayList);
    }

    public LocalDateTime parserBirthdayTime(String data) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String inputCommand = data.substring(0, data.length() - 3);

        return (LocalDateTime.parse(inputCommand, format));

    }


}
