package comm;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingDeque;

public class SqlPool {

    private static LinkedBlockingDeque<Connection> waitList;
    private  static String  CONN_GAME,CONN_USER,CONN_PWD;
    public static boolean init(String CONN_GAME, String CONN_USER, String CONN_PWD,int maxCount) {
		try {
            SqlPool.CONN_GAME=CONN_GAME;
            SqlPool.CONN_USER=CONN_USER;
            SqlPool.CONN_PWD=CONN_PWD;
			Class.forName("com.mysql.jdbc.Driver");
		//	waitList=new LinkedBlockingDeque<>(maxCount);
            long tim=System.currentTimeMillis();
// 			for (int i = 0; i <maxCount; i++) {
//			 Connection	conn = DriverManager.getConnection(CONN_GAME,
//                     CONN_USER, CONN_PWD);
//			//conn.setAutoCommit(false);
//
//			waitList.offer(conn);
//			}
            System.out.println("tim:" + (System.currentTimeMillis() - tim));
			 
		 
		} catch (Exception e) {
			 e.printStackTrace();
		 return false;
		}
		return true;
	}

    private static Connection getReconnectConn()  {
        //System.out.println("reconnect-----");
        Connection conn=null;
        try {
            conn=DriverManager.getConnection(CONN_GAME,
                    CONN_USER, CONN_PWD);
        } catch (SQLException e) {
            return  null;
        }
        return  conn;
    }
    public static Connection getConn() {
        return SqlPool.getReconnectConn();
//        Connection conn=null;
//     while(true) {
//           conn = waitList.pollFirst();
//     if(conn!=null){
//         Statement testSt=null;
//         boolean error=false;
//         try {
//             testSt= conn.createStatement();
//          testSt.executeQuery("select 1 from linkhelp");
//             if(testSt!=null){
//                 try {
//                     testSt.close();
//                 } catch (SQLException e) {
//                     e.printStackTrace();
//                 }
//             }
//
//             break;
//         } catch (SQLException e) {
//             try {
//                 if(testSt!=null){
//                     try {
//                         testSt.close();
//                     } catch (SQLException e3) {
//                         e3.printStackTrace();
//                     }
//                 }
//                 Thread.sleep(30);
//                 Connection newC=SqlPool.getReconnectConn();
//                 if(newC==null) {
//                     release(conn);
//                 }else {
//                     return newC;
//                 }
//
//             } catch (InterruptedException e2) {
//                 e2.printStackTrace();
//             }
//             e.printStackTrace();
//
//
//         }
//
//     }else{
//         try {
//             Thread.sleep(30);
//         } catch (InterruptedException e) {
//             e.printStackTrace();
//         }
//     }
//     }
//
//       // System.out.println("count"+waitList.size());
//
//
//			return conn;
	  
	}
    public static int getIdleCount() {

        return 0;//waitList.size();

    }
    public static void release(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // waitList.addFirst(conn);
  
}
//心跳机制
    public static void keepAlive(int seconds) {



    }

}