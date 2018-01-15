package gluffy.utils;


public class BoolUtils {
public static final byte TRUE=1,FALSE=0;

public static byte getByte(int val) {
	return (byte)val;
}
public static byte getByte(boolean	val) {
	return (byte) (val?1:0);
}
}
