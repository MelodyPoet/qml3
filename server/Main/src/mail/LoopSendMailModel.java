package mail;

import comm.Model;
import gluffy.utils.JkTools;
import protocol.MailPVo;
import sqlCmd.AllSql;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import table.MailBaseVo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import static mail.MailModel.LIMIT_COUNT;

/**
 * Created by admin on 2016/8/1.
 */
public class LoopSendMailModel {
    public static Map<Integer,int[]> registerMap = new HashMap<>();
    public static Map<Integer,int[]> loopDayMap = new HashMap<>();
    public static Map<Integer,int[]> loopWeekMap = new HashMap<>();
    public static Map<Integer,int[]> loopMonthMap = new HashMap<>();
    public static ArrayList<MailPVo> systemMailList = new ArrayList<>();
    public static Map<Long,HashSet<Long>> readUsers = new HashMap<>();
    public static Map<Long,HashSet<Long>> getUsers = new HashMap<>();
    public static Map<Long,HashSet<Long>> delUsers = new HashMap<>();
    public static Map<Long,HashMap<Long,Integer>> rankUsers = new HashMap<>();
    public static ArrayList<MailPVo> initMailList = new ArrayList<>();
    public static int baseID;
    public static int BASEID = 100000;
    public static byte GET_USERS = 0;
    public static byte READ_USERS = 1;
    public static byte DEL_USERS = 2;

    public static void init(){
        AllSql.mailSql.loadSystemMail();
        AllSql.mailSql.loadInitMail();
        for(MailBaseVo vo : Model.MailBaseMap.values()){
            if(vo.ID>baseID){
                baseID = vo.ID;
            }
           classify(vo);
        }
        HashSet<Integer> set = new HashSet();
        for(int[] arr : registerMap.values()){
            for(MailPVo mailPVo : initMailList){
                if(mailPVo.senderID != arr[0])continue;
                set.add(arr[0]);
                break;
            }
        }
        for(int i : set){
            registerMap.remove(i);
        }
        for(int[] arr : registerMap.values()){
            MailPVo pVo = MailModel.createMail(arr[0],(long)-1);
            if(arr[1] == 1)pVo.deadDay = -1;
            addInitMail(pVo,true);
        }
    }

    public static void classify(MailBaseVo vo){
        int day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        if(vo.cycleType == 0){
            int[] arr = new int[2];
            arr[0] = vo.ID;
            arr[1] = vo.day;
            registerMap.put(vo.ID,arr);
        }
        if(vo.cycleType == 1){//每天(当day不为0时，表示某一天)
            if(vo.day < day && vo.day != 0){
                return;
            }
            int[] arr = new int[3];
            arr[0] = vo.ID;
            arr[1] = vo.day;
            arr[2] = vo.hours;
            loopDayMap.put(vo.ID,arr);
        }
        if(vo.cycleType == 2){//每周
            int[] arr = new int[3];
            arr[0] = vo.ID;
            arr[1] = vo.day%7+1;
            arr[2] = vo.hours;
            loopWeekMap.put(vo.ID,arr);
        }
        if(vo.cycleType == 3){//每月（day为99时表示每个月最后一天）
            int[] arr = new int[4];
            arr[0] = vo.ID;
            if(vo.day == 99){
                int max = JkTools.getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH);
                arr[3] = 1;//每月最后一天类型的标识
                arr[1] = max;
            }else{
                arr[1] = vo.day;
            }
            arr[2] = vo.hours;
            loopMonthMap.put(vo.ID,arr);
        }
    }

    public static void addSystemMail(MailPVo mailPVo,boolean insertNew) {
        Calendar calendar = JkTools.getCalendar();
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
//        int time = JkTools.getGameServerTime(null,calendar.getTimeInMillis());
//        for(MailPVo pVo : systemMailList){
//            if(pVo.senderID == mailPVo.senderID){
//                if(time - pVo.receiverTime <Model.ONE_HOURS_TIME){
//                    return;
//                }
//            }
//        }
        if(systemMailList.size()>=LIMIT_COUNT){
            if(insertNew){
                int index = -1;
                for(int i=systemMailList.size()-1;i>=0;i--){
                    MailPVo pVo = systemMailList.get(i);
                    if(pVo.prop.size()!=0){
//                    if(pVo.prop.size()!=0 || pVo.money != 0 || pVo.diamond !=0){
                        continue;
                    }
                    index = i;
                    break;
                }
                if(index == -1){
                    index = systemMailList.size() - 1;
                }
                systemMailList.remove(index);
            }else{
               return;
            }
        }
        if(insertNew){
            AllSql.mailSql.insertNew(mailPVo);
            if(mailPVo.senderID < LoopSendMailModel.BASEID){
                MailModel.sendMailToOnLine(mailPVo);
            }
            systemMailList.add(mailPVo);
        }else{
            systemMailList.add(0,mailPVo);
        }
    }

    public static void addInitMail(MailPVo mailPVo,boolean insertNew) {
        if(insertNew){
            AllSql.mailSql.insertNew(mailPVo);
        }
        initMailList.add(mailPVo);
    }

    public static void loopSendMail(){
        Calendar cal = JkTools.getCalendar();
        int day = cal.get(Calendar.DAY_OF_YEAR);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        HashSet<Integer> set = new HashSet<>();
        for(int[] arr : loopDayMap.values()){//每天或某一天
            if(arr[1] > 0){//某一天特殊处理
                if(day!=arr[1])continue;
            }
            if(hours!=arr[2]%24)continue;
            if(arr[2]/24>0){
                arr[2] -= 25;
                continue;
            }
            arr[2] = arr[2] + 25;
            saveSystemMail(arr[0]);
            if(arr[1] > 0) {//某一天，执行完后移除
                set.add(arr[0]);
            }
        }
        for(int i : set){
            loopDayMap.remove(i);
        }
        for(int[] arr : loopWeekMap.values()){//每周
            if(dayOfWeek!=arr[1])continue;
            if(hours!=arr[2]%24)continue;
            if(arr[2]/24>0){
                arr[2] -= 25;
                continue;
            }
            arr[2] = arr[2] + 25;
            saveSystemMail(arr[0]);
        }
        for(int[] arr : loopMonthMap.values()){//每月
            if(dayOfMonth!=arr[1])continue;
            if(hours!=arr[2]%24)continue;
            if(arr[2]/24>0){
                arr[2] -= 25;
                continue;
            }
            arr[2] = arr[2] + 25;
            saveSystemMail(arr[0]);
            if(arr[3] == 1){
                Calendar calendar = JkTools.getCalendar();
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.MONTH, 1);
                arr[1] = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
        }
    }

    private static void saveSystemMail(int id){
        MailPVo pVo = MailModel.createMail(id,(long)0);
        addSystemMail(pVo,true);
    }

    public static void loadMailUser(long mailID,String str,byte type){
        if (str == null || str.length() < 4){
            return;
        }
        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int size = buffer.getInt();
        HashSet<Long> set = new HashSet<>();
        for(int i=0;i<size;i++){
            set.add(buffer.getLong());
        }
        if(type ==GET_USERS){
            getUsers.put(mailID,set);
        }else if(type == READ_USERS){
            readUsers.put(mailID,set);
        }else{
            delUsers.put(mailID,set);
        }
    }

    public static void saveMailUser(long mailID,byte type){
        HashSet<Long> set;
        if(type ==GET_USERS){
            if(!getUsers.containsKey(mailID))return;
            set = getUsers.get(mailID);
        }else if(type == READ_USERS){
            if(!readUsers.containsKey(mailID))return;
            set = readUsers.get(mailID);
        }else{
            if(!delUsers.containsKey(mailID))return;
            set = delUsers.get(mailID);
        }
        byte[] bytes = new byte[set.size()*8+4];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putInt(set.size());
        for(long i: set){
            buffer.putLong(i);
        }
        if(type ==GET_USERS){
            AllSql.mailSql.update(mailID,AllSql.mailSql.FIELD_GET_USERS,"'"+new BASE64Encoder().encode(bytes)+"'");
        }else if(type == READ_USERS){
            AllSql.mailSql.update(mailID,AllSql.mailSql.FIELD_READ_USERS,"'"+new BASE64Encoder().encode(bytes)+"'");
        }else{
            AllSql.mailSql.update(mailID,AllSql.mailSql.FIELD_DEL_USERS,"'"+new BASE64Encoder().encode(bytes)+"'");
        }
    }

    public static void loadRankUser(long mailID,String str){
        if (str == null || str.length() < 4){
            return;
        }
        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int size = buffer.getInt();
        HashMap<Long,Integer> Users = new HashMap<>();
        for(int i=0;i<size;i++){
            Users.put(buffer.getLong(),buffer.getInt());
        }
        rankUsers.put(mailID,Users);
    }

    public static void saveRankUser(long mailID){
        if(!rankUsers.containsKey(mailID))return;
        HashMap<Long,Integer> map = rankUsers.get(mailID);
        byte[] bytes = new byte[map.size()*12+4];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putInt(map.size());
        for(Map.Entry<Long,Integer> item: map.entrySet()){
            buffer.putLong(item.getKey());
            buffer.putInt(item.getValue());
        }
        AllSql.mailSql.update(mailID,AllSql.mailSql.FIELD_RANK_USERS,"'"+new BASE64Encoder().encode(bytes)+"'");
    }
}
