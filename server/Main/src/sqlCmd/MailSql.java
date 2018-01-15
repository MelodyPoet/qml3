package sqlCmd;

import gluffy.utils.JkTools;
import mail.LoopSendMailModel;
import comm.SqlPool;
import comm.User;
import mail.MailModel;
import protocol.MailPVo;
import table.UserDataEnum;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import static mail.LoopSendMailModel.addSystemMail;
import static mail.LoopSendMailModel.systemMailList;
import static mail.MailModel.getAnnexPropList;
import static mail.MailModel.getAnnexPropStr;

/**
 * Created by admin on 2016/7/26.
 */
public class MailSql extends BaseSql{
    public final String FIELD_GUID = "guid";
    public final String FIELD_SENDER_ID = "senderID";
    public final String FIELD_RECEIVER_ID = "receiverID";
    public final String FIELD_RECEIVER_TIME = "receiverTime";
    public final String FIELD_IS_READ = "isRead";
    public final String FIELD_TITLE = "title";
    public final String FIELD_MSG = "msg";
    public final String FIELD_SENDER_NAME = "senderName";
    public final String FIELD_PROP = "prop";
    public final String FIELD_MONEY = "money";
    public final String FIELD_DIAMOND = "diamond";
    public final String FIELD_DEAD_DAY = "deadDay";
    public final String FIELD_FLAG = "flag";
    public final String FIELD_GET_USERS = "getUsers";
    public final String FIELD_READ_USERS = "readUsers";
    public final String FIELD_DEL_USERS = "delUsers";
    public final String FIELD_RANK_USERS = "rankUsers";
    public final String FIELD_IS_GET = "isGet";

    public MailSql() {
        super("mail");
    }

    public boolean loadOneMail(User user){
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try{
            Calendar calendar = Calendar.getInstance();
            int today = calendar.get(Calendar.DAY_OF_YEAR)+ JkTools.getBaseDay(calendar.get(Calendar.YEAR));
            cmd = "select guid,senderID,receiverID,receiverTime,isRead,title,msg,senderName,isGet,prop,money,diamond,deadDay from mail where deadDay > "+ today+" and flag = 0 and receiverID = " + user.guid + " order by receiverTime";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            while (dataSet.next()){
                int index = 1;
                MailPVo mailPVo = new MailPVo();
                mailPVo.guid = dataSet.getLong(index++);
                mailPVo.senderID = dataSet.getLong(index++);
                mailPVo.receiverID = dataSet.getLong(index++);
                mailPVo.receiverTime = dataSet.getInt(index++);
                mailPVo.isRead = dataSet.getByte(index++);
                mailPVo.title = dataSet.getString(index++);
                mailPVo.msg = dataSet.getString(index++);
                mailPVo.senderName = dataSet.getString(index++);
                if(dataSet.getByte(index++) == 1){//isGet
                    index++;
                    mailPVo.prop = getAnnexPropList("");
                }else{
                    mailPVo.prop = getAnnexPropList(dataSet.getString(index++));
                }
//                mailPVo.money = dataSet.getInt(index++);
//                mailPVo.diamond = dataSet.getInt(index++);
                index++;
                index++;
                mailPVo.deadDay = dataSet.getInt(index++);
                user.mailModel.addMail(mailPVo,false);
            }
            int createTime = user.getUserData(UserDataEnum.RoleCreatTime);
            Iterator<MailPVo> it = systemMailList.iterator();
            while (it.hasNext()){
                MailPVo pVo = it.next();
                if(pVo.senderID > LoopSendMailModel.BASEID){
                    if(LoopSendMailModel.rankUsers.containsKey(pVo.guid)){
                        HashMap<Long,Integer> map = LoopSendMailModel.rankUsers.get(pVo.guid);
                        if(!map.containsKey(user.guid))continue;
                    }else{
                        continue;
                    }
                }
                if(LoopSendMailModel.delUsers.containsKey(pVo.guid))continue;
                if(pVo.deadDay <= today){
                    AllSql.mailSql.update(pVo,AllSql.mailSql.FIELD_FLAG,1);
                    it.remove();
                    continue;
                }
                if(pVo.receiverTime <= createTime){
                    continue;
                }
                MailPVo mailPVo = new MailPVo();
                mailPVo.guid = pVo.guid;
                mailPVo.type = pVo.type;
                mailPVo.senderID = pVo.senderID;
                mailPVo.receiverTime = pVo.receiverTime;
                mailPVo.isRead = 0;
                if(LoopSendMailModel.readUsers.containsKey(pVo.guid)){
                    HashSet<Long> set = LoopSendMailModel.readUsers.get(pVo.guid);
                    if(set.contains(user.guid)){
                        mailPVo.isRead = 1;
                    }
                }
                mailPVo.title = pVo.title;
                mailPVo.msg = pVo.msg;
                mailPVo.senderName = pVo.senderName;
                mailPVo.prop = pVo.prop;
//                mailPVo.money = pVo.money;
//                mailPVo.diamond = pVo.diamond;
                mailPVo.deadDay = pVo.deadDay;
                mailPVo.receiverID = pVo.receiverID;
                if(pVo.senderID > LoopSendMailModel.BASEID){
                    MailModel.changeMail(user.guid,mailPVo);
                }
                if(LoopSendMailModel.getUsers.containsKey(pVo.guid)){
                    HashSet<Long> set = LoopSendMailModel.getUsers.get(pVo.guid);
                    if(set.contains(user.guid)){
                        mailPVo.prop = new ArrayList<>();
                    }
                }
                user.mailModel.addMail(mailPVo,false);
            }
            for(MailPVo pVo : LoopSendMailModel.initMailList){
                if(pVo.deadDay == -1 && user.cacheUserVo.passportVo.isOldUser != 1) continue;//老用户才发的邮件
                MailPVo mailPVo = new MailPVo();
                mailPVo.guid = pVo.guid;
                mailPVo.type = pVo.type;
                mailPVo.senderID = pVo.senderID;
                mailPVo.receiverTime = createTime;
                mailPVo.isRead = 0;
                if(LoopSendMailModel.readUsers.containsKey(pVo.guid)){
                    HashSet<Long> set = LoopSendMailModel.readUsers.get(pVo.guid);
                    if(set.contains(user.guid)){
                        mailPVo.isRead = 1;
                    }
                }
                mailPVo.title = pVo.title;
                mailPVo.msg = pVo.msg;
                mailPVo.senderName = pVo.senderName;
                mailPVo.prop = pVo.prop;
                if(LoopSendMailModel.getUsers.containsKey(pVo.guid)){
                    HashSet<Long> set = LoopSendMailModel.getUsers.get(pVo.guid);
                    if(set.contains(user.guid)){
                        mailPVo.prop = new ArrayList<>();
                    }
                }
                mailPVo.deadDay = pVo.deadDay;
                mailPVo.receiverID = pVo.receiverID;
                user.mailModel.addMail(mailPVo,false);
            }
            user.mailModel.flag = true;
            dataSet.close();
        }catch (Exception e){
            new Exception("ReconnectError").printStackTrace();
            e.printStackTrace();
            return false;
        } finally {
            SqlPool.release(conn);
        }
        return true;
    }

    public boolean loadSystemMail(){
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try{
            Calendar calendar = Calendar.getInstance();
            int today = calendar.get(Calendar.DAY_OF_YEAR)+ JkTools.getBaseDay(calendar.get(Calendar.YEAR));
            cmd = "select guid,senderID,receiverID,receiverTime,isRead,title,msg,senderName,prop,money,diamond,deadDay,getUsers,readUsers,delUsers,rankUsers from mail where deadDay > "+ today+" and flag = 0 and receiverID = 0 order by receiverTime desc";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            while (dataSet.next()){
                int index = 1;
                MailPVo mailPVo = new MailPVo();
                mailPVo.guid = dataSet.getLong(index++);
                mailPVo.senderID = dataSet.getLong(index++);
                mailPVo.receiverID = dataSet.getLong(index++);
                mailPVo.receiverTime = dataSet.getInt(index++);
                mailPVo.isRead = dataSet.getByte(index++);
                mailPVo.title = dataSet.getString(index++);
                mailPVo.msg = dataSet.getString(index++);
                mailPVo.senderName = dataSet.getString(index++);
                mailPVo.prop = getAnnexPropList(dataSet.getString(index++));
//                mailPVo.money = dataSet.getInt(index++);
//                mailPVo.diamond = dataSet.getInt(index++);
                index++;
                index++;
                mailPVo.deadDay = dataSet.getInt(index++);
                LoopSendMailModel.loadMailUser(mailPVo.guid,dataSet.getString(index++),LoopSendMailModel.GET_USERS);
                LoopSendMailModel.loadMailUser(mailPVo.guid,dataSet.getString(index++),LoopSendMailModel.READ_USERS);
                LoopSendMailModel.loadMailUser(mailPVo.guid,dataSet.getString(index++),LoopSendMailModel.DEL_USERS);
                LoopSendMailModel.loadRankUser(mailPVo.guid,dataSet.getString(index++));
                addSystemMail(mailPVo,false);
            }
            dataSet.close();
        }catch (Exception e){
            new Exception("ReconnectError").printStackTrace();
            e.printStackTrace();
            return false;
        } finally {
            SqlPool.release(conn);
        }
        return true;
    }

    public boolean loadInitMail(){
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try{
            cmd = "select guid,senderID,receiverID,receiverTime,isRead,title,msg,senderName,prop,money,diamond,deadDay,getUsers,readUsers,delUsers,rankUsers from mail where flag = 0 and receiverID = -1";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            while (dataSet.next()){
                int index = 1;
                MailPVo mailPVo = new MailPVo();
                mailPVo.guid = dataSet.getLong(index++);
                mailPVo.senderID = dataSet.getLong(index++);
                mailPVo.receiverID = dataSet.getLong(index++);
                mailPVo.receiverTime = dataSet.getInt(index++);
                mailPVo.isRead = dataSet.getByte(index++);
                mailPVo.title = dataSet.getString(index++);
                mailPVo.msg = dataSet.getString(index++);
                mailPVo.senderName = dataSet.getString(index++);
                mailPVo.prop = getAnnexPropList(dataSet.getString(index++));
//                mailPVo.money = dataSet.getInt(index++);
//                mailPVo.diamond = dataSet.getInt(index++);
                index++;
                index++;
                mailPVo.deadDay = dataSet.getInt(index++);
                LoopSendMailModel.loadMailUser(mailPVo.guid,dataSet.getString(index++),LoopSendMailModel.GET_USERS);
                LoopSendMailModel.loadMailUser(mailPVo.guid,dataSet.getString(index++),LoopSendMailModel.READ_USERS);
                LoopSendMailModel.loadMailUser(mailPVo.guid,dataSet.getString(index++),LoopSendMailModel.DEL_USERS);
                LoopSendMailModel.loadRankUser(mailPVo.guid,dataSet.getString(index++));
                LoopSendMailModel.addInitMail(mailPVo,false);
            }
            dataSet.close();
        }catch (Exception e){
            new Exception("ReconnectError").printStackTrace();
            e.printStackTrace();
            return false;
        } finally {
            SqlPool.release(conn);
        }
        return true;
    }

    public boolean insertNew(MailPVo mailPVo) {

        long guid= AllSql.mailSql.getNewGuid();
//        insert(FIELD_GUID+","+FIELD_SENDER_ID+","+FIELD_RECEIVER_ID+","+FIELD_RECEIVER_TIME+","+FIELD_IS_READ+","+FIELD_TITLE+","+FIELD_MSG+","+FIELD_SENDER_NAME+","+FIELD_PROP+","+FIELD_MONEY+","+FIELD_DIAMOND+","+ FIELD_DEAD_DAY,
//                guid+","+mailPVo.senderID+","+mailPVo.receiverID+","+mailPVo.receiverTime+","+mailPVo.isRead+",\'"+mailPVo.title+"\',\'"+mailPVo.msg+"\',\'"+mailPVo.senderName+"\',\'"+getAnnexPropStr(mailPVo.prop)+"\',"+mailPVo.money+","+mailPVo.diamond+","+mailPVo.deadDay);
        insert(FIELD_GUID+","+FIELD_SENDER_ID+","+FIELD_RECEIVER_ID+","+FIELD_RECEIVER_TIME+","+FIELD_IS_READ+","+FIELD_TITLE+","+FIELD_MSG+","+FIELD_SENDER_NAME+","+FIELD_PROP+","+ FIELD_DEAD_DAY,
                guid+","+mailPVo.senderID+","+mailPVo.receiverID+","+mailPVo.receiverTime+","+mailPVo.isRead+",\'"+mailPVo.title+"\',\'"+mailPVo.msg+"\',\'"+mailPVo.senderName+"\',\'"+getAnnexPropStr(mailPVo.prop)+"\',"+mailPVo.deadDay);
        mailPVo.guid =guid;
        return true;
    }

    public void update(MailPVo mailPVo,String keyName,int value) {
        update(mailPVo.guid,keyName, value+"");
    }
    public void update(MailPVo mailPVo,String keyName,String value) {
        update(mailPVo.guid,keyName, value);
    }
    public void update(long mailID,String keyName,int value) {
        update(keyName, value+"", mailID);
    }
    public void update(long mailID,String keyName,String value) {
        update(keyName, value, mailID);
    }
}
