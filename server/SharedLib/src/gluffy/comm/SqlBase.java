package gluffy.comm;

import gluffy.utils.LogManager;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public abstract class SqlBase {
	public static final byte /*ADD_MODE = 1,*/ UPDATE_MODE = 2, DELETE_MODE = 3;
	public static Connection conn;
	protected PreparedStatement psRead, psAdd, psUpdate, psDel;// ���������
	private int tempID;
	public static ByteBuffer tempBuffer= ByteBuffer.allocateDirect(Short.MAX_VALUE * 2 - 1);
	public static boolean init(String CONN_GAME, String CONN_USER, String CONN_PWD) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(CONN_GAME,
					CONN_USER, CONN_PWD);
			conn.setAutoCommit(false);
			 
			LogManager.serverLog.info("reset mysql");
		} catch (Exception e) {
			LogManager.printError(e);
		 return false;
		}
		return true;
	}

	public static  byte[] getTempBytes() {
		ByteBuffer saveBuffer = tempBuffer;
		saveBuffer.flip();
		byte[] bytes = new byte[saveBuffer.limit()];
		saveBuffer.get(bytes);
		return bytes;
	}

	public SqlBase() {
		tempID = 0;
	}

	public void setMixTempID(int id) {
		 
		if (tempID < id)
			tempID = id;
	}

	public abstract void loadAllData() throws SQLException;

	public abstract void saveOne(Object object) throws SQLException;

	public abstract void pushQueue(Object item, byte mode);
	public abstract void reconnect()  throws SQLException;

	public void onRoleLogin(AbsClient cl) {
	}

	public void onRoleLogout(int roleID) {
	};

	public synchronized int getTempID() {
		return ++tempID;
	}
}
