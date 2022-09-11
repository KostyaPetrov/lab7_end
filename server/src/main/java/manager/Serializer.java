package manager;


import java.io.*;
//TODO Сделать серелизацию и десерелизацию
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

//    public String deserialize(ByteBuffer buffer) {
//        buffer.flip();
//        int limit =buffer.limit();
//        byte[] bytes =new byte[limit];
//
//        buffer.get(bytes,0,limit);
//        String stringBuffer = new String(bytes);
//        return stringBuffer;
//    }

//    public String deserialize(ByteBuffer buffer) {
//        int limit =buffer.limit();
//        byte bytes[] =new byte[limit];
//        buffer.get(bytes,0,limit);
//        String stringBuffer = new String(bytes);
//        return stringBuffer;
//    }
//    public StringBuilder deserialize(ByteBuffer byteBuffer) {
//        StringBuilder builder = new StringBuilder();
//        while (byteBuffer.hasRemaining()) {
//            builder.append((char) byteBuffer.get()).toString();
//        }
//        return builder;
//    }
//    public Object deserialize(byte[] bytes) {
//        try {
//            InputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
//            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
//            return objectInputStream.readObject();
//
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//    public Object convertFromByteString(String byteString) throws IOException, ClassNotFoundException {
//        final byte[] bytes = Base64.getDecoder().decode(byteString);
//        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInput in = new ObjectInputStream(bis)) {
//            return in.readObject();
//        }
//    }
}

