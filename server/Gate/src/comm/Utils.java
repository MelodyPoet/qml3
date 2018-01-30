package comm;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

public class Utils {
    public static Charset UTF8 = Charset.forName("UTF-8");

    public static String readString(ByteBuffer bb, int sizeMax) {
        if (bb.remaining() < 2)
            return "";
        short len = bb.getShort();
        if (len <= 0) {
            return "";

        }
        if (len > bb.remaining())
            return "";
        if (len > sizeMax)
            len = (short) sizeMax;
        byte[] bytes = new byte[len];
        bb.get(bytes);
        return new String(bytes, UTF8);
    }
    public static void writeString(ByteBuffer bb, String str) {

        if (str != null) {
            byte[] bytes = str.getBytes(UTF8);
            bb.putShort((short) bytes.length);
            bb.put(bytes);
        } else {
            bb.putShort((short) 0);
        }

    }
    public static void writeArray(ByteBuffer bytes, Collection<?> array) {
        if(array==null){
            bytes.putInt(0);
            return;
        }
        bytes.putInt(array.size());
        for (Object i : array) {
            writeValue(bytes, i);
        }
    }
    public static void writeValue(ByteBuffer bytes, Object value) {
        if (value instanceof Long) {
            bytes.putLong((Long) value);
        }	else if (value instanceof Integer) {
            bytes.putInt((Integer) value);
        } else if (value instanceof Short) {
            bytes.putShort((Short) value);
        } else if (value instanceof Byte) {
            bytes.put((Byte) value);
//        } else if (value instanceof IBytes) {
//            ((IBytes)value).toBytes(bytes);
        }else {
            new Exception("������������������"+value.getClass()).printStackTrace();
        }

    }
    public static <T> ArrayList<T> readArray(ByteBuffer bytes, Class<T> ct)
    {
        int size = bytes.getInt();

        ArrayList<T> array = new ArrayList<>();
        if (size == 0) {

            return array;
        }

        // t.getClass().newInstance();
        for (int i = 0; i < size; i++) {
            // array.add(readValue(bytes,t));
            T value = null;
            if (ct == Long.class) {
                value = (T) (Long) bytes.getLong();
            } else if (ct == Integer.class) {
                value = (T) (Integer) bytes.getInt();
            } else if (ct == Short.class) {
                value = (T) (Short) bytes.getShort();
            } else if (ct == Byte.class) {
                value = (T) (Byte) bytes.get();
            } else {

                try {
                    value = (T) ct.newInstance();
                } catch (InstantiationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
               // ((IBytes) value).fromBytes(bytes);

            }
            array.add(value);
        }
        return array;
    }
}
