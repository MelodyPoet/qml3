package comm;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class CheckUtils {
	private static Pattern namePattern;

	static MessageDigest md;

	public static void init() {
		namePattern = Pattern.compile("^[A-Za-z0-9\u4e00-\u9fa5\u00A4-\u00D7\u02C7\u2606-\u2fff\u3001-\u3005\u3041-\u3129]{2,8}$");
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static boolean checkName(String name) {
		return namePattern.matcher(name).matches();
	}

	static public boolean checkHash(byte[] hashval, long uid) {
		if (md == null) {
			try {
				md = MessageDigest.getInstance("SHA-1");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		} else {

			md.reset();
		}
		String key1 = "huDqrvKLuk69Ajdl";
		String key2 = "lsnMS3SYrnQNkndf";
		byte[] des = hashval;

		// des = hashval.getBytes();

		md.update(des, 8, 4);
		md.update(key1.getBytes(), 0, key1.getBytes().length);
		md.update(des, 0, 4);
		byte[] uidBytes = new byte[8];
		ByteBuffer bbBuffer = ByteBuffer.allocate(8);
		bbBuffer.putLong(uid);
		bbBuffer.position(0);
		bbBuffer.get(uidBytes);
		md.update(uidBytes, 0, 8);
		md.update(des, 12, 4);
		md.update(key2.getBytes(), 0, key2.getBytes().length);
		md.update(des, 4, 4);

		byte[] src = md.digest();
		for (int i = 0; i < 20; i++) {
			if (src[i] != des[i + 16])
				return false;
		}
		return true;
	}
}
