import comm.SqlPool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by jackie on 14-4-18.
 */
public class RobotSqlInit {
    public static void main(String[] args) throws SQLException {
      //  SqlPool.init("jdbc:mysql://114.80.118.54:3306/iceAndFire", "root", "jk820606#*#", 10);
        SqlPool.init("jdbc:mysql://127.0.0.1:3306/iceAndFire", "root", "jk820606#*#", 10);
      Connection conn= SqlPool.getConn();
        conn.setAutoCommit(false);
        conn.createStatement().execute("delete  from user where guid>10000");
        for (int i = 10001; i < 15000; i++) {
         //   System.out.println("insert into user (guid,devID,uname,baseID,level) values("+i+","+i+",robot"+i+",1,1)");
            conn.createStatement().execute("insert into user (guid,devID,uname,baseID,level) values("+i+","+i+",\"robot"+i+"\",1,1)");
        }
        conn.createStatement().execute("delete  from prop where guid>10000");
        for (int i = 10001; i < 15000; i++) {
             conn.createStatement().execute("insert into prop (guid,userID,baseID,tableID) values("+i+","+i+",3,1)");
        }
        conn.commit();
    }
}
