package manager;


import java.io.*;

public class Serializer {
    public byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            byte[] aboba=byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            objectOutputStream.close();
            return aboba;
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
    }



    public Serializable deserialize(byte[] data) {

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            Serializable request = (Serializable) is.readObject();
            in.close();
            is.close();
            return request;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

}

