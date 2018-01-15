package gluffy.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlUtils {

	public static void setValue(PreparedStatement psUpdate, int i, int val) throws SQLException {
		psUpdate.setInt(i, val);
	}
	public static void setValue(PreparedStatement psUpdate, int i, long val) throws SQLException {
		psUpdate.setLong(i, val);
	}
	public static void setValue(PreparedStatement psUpdate, int i, short val) throws SQLException {
		psUpdate.setShort(i, val);
	}
	public static void setValue(PreparedStatement psUpdate, int i, byte val) throws SQLException {
		psUpdate.setByte(i, val);
	}
	public static void setValue(PreparedStatement psUpdate, int i, String val) throws SQLException {
		psUpdate.setString(i, val);
	}
	public static void setValue(PreparedStatement psUpdate, int i, byte[] val) throws SQLException {
		psUpdate.setBytes(i, val);
	}

}
