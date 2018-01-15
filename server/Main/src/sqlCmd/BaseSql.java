package sqlCmd;

 
import comm.Model;
import comm.SqlPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class BaseSql {
    public final String FIELD_GUID="guid";
	public  String tabel;
    public  long currentGuid;
    private     Connection currentConn;
	public BaseSql(String tabel) {
       long start= (long)Model.serverID<<6*8;

        currentGuid = start;
        this.tabel=tabel;
        initGuid();
	}


    public boolean initGuid() {
        Connection conn=SqlPool.getConn();
        String cmd;
        ResultSet dataSet=null;
        try {
            conn.setAutoCommit(true);
            cmd="select max(guid)  from  "+tabel;
            Statement state = conn.createStatement();
            dataSet= state.executeQuery(cmd);
            dataSet.next();

            currentGuid =Math.max(currentGuid, dataSet.getLong(1));
            dataSet.close();
        } catch (Exception e) {

            new Exception("ReconnectError").printStackTrace();
e.printStackTrace();
            return  false;
        }finally{
            SqlPool.release(conn);
        }
return true;

    }
    public synchronized long getNewGuid() {
        return ++currentGuid;
    }


protected   void insert(String keys,String values) {
    SqlSaver.addBatch("insert into  " + tabel + " (" + keys + ") values(" + values + ")");

//	Connection conn=SqlPool.getConn();
//  boolean returnOk=false;
//	String cmd;
//
//		cmd="insert into  "+tabel+" ("+keys+") values("+values+")";
//    for (int i=0;i<3;i++) {
//        Statement state=null;
//        try {
//            state = conn.createStatement();
//
//            state.executeUpdate(cmd);
//            returnOk = true;
//
//            SqlPool.release(conn);
//            state.close();
//            break;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            if(state!=null){
//                try {state.close();} catch (Exception e2){};
//            }
//            conn = SqlPool.getReconnectConn();
//            if (conn == null) break;
//        }
//    }
//	return returnOk;
	
}
     public    void update(String keyName,String value,long guid) {
        update(keyName,value,FIELD_GUID+" = "+guid);
    }

    public    void  update(String keyName,String value,String condition) {
        //System.out.println("update  " + tabel + " set " + keyName + " = " + value + " where " + condition + " limit 1");

        SqlSaver.addBatch("update  " + tabel + " set " + keyName + " = " + value + " where " + condition + " limit 1");
    }

    public void delete(long guid) {
        delete(FIELD_GUID+"="+guid);
    }
protected   void delete(String condition) {
    SqlSaver.addBatch("delete from  "+tabel+" where "+condition+" limit 1");
//	Connection conn=SqlPool.getConn();
//	String cmd;
//
//		cmd="delete from  "+tabel+" where "+condition;
//    for (int i=0;i<3;i++) {
//        try {
//	Statement state = conn.createStatement();
//
//	state.executeUpdate(cmd);
//       SqlPool.release(conn);
//            state.closeOnCompletion();
//            break;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            conn=SqlPool.getReconnectConn();
//            if(conn==null)break;
//
//
//        }
//
//	}
//	return true;
	
}
protected   ResultSet readSet(String condition) {
	Connection conn=SqlPool.getConn();
    currentConn=conn;
	String cmd;
	ResultSet dataSet=null;

		cmd="select * from  "+tabel+" where "+condition;

        Statement state=null;
        try {
            conn.setAutoCommit(true);
      state = conn.createStatement();

            dataSet = state.executeQuery(cmd);



        } catch (SQLException e) {
           new Exception("ReconnectError").printStackTrace();

        }


	return dataSet;
	
}
    protected   void closeSetConn() {
        SqlPool.release( currentConn);
        currentConn=null;
    }
}
