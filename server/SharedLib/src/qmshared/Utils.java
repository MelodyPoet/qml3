package qmshared;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

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
}
