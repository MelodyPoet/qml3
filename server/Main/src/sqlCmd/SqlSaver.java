package sqlCmd;

import comm.SqlPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.LinkedBlockingQueue;

public class SqlSaver extends Thread {
    private static LinkedBlockingQueue<String> savingList;

     public static void addBatch(String sql) {
        savingList.offer(sql);
    }
    private static boolean saveBatch() throws SQLException {
        long tim=System.currentTimeMillis();
        int count=savingList.size();
        if(count==0)return true;
                Connection conn = SqlPool.getConn();
        try {
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
int runCount=100;
            while (savingList.size() > 0&&runCount-->0) {
                String sql = savingList.poll();
                System.out.println(sql);
                state.addBatch(sql);

            }

            state.executeBatch();
            conn.commit();
            state.close();
        }finally {
            SqlPool.release(conn);

        }

         //  if(System.currentTimeMillis()-tim>500)
             System.out.println("saveall:"+count+",time:"+(System.currentTimeMillis()-tim));
        return true;
    }



    @Override
    public void run() {
        super.run();
        savingList = new LinkedBlockingQueue<>();

        while (true) {



            try {
                saveBatch();
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

}
