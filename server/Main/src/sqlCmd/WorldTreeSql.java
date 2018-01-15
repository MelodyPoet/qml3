package sqlCmd;

import comm.SqlPool;
import gluffy.utils.JkTools;
import protocol.TalkMsgPVo;
import table.TalkEnum;
import talk.LoopSendTalkModel;
import talk.TalkModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

/**
 * Created by admin on 2016/8/5.
 */
public class WorldTreeSql extends BaseSql {
    public final String FIELD_GUID = "guid";
    public final String FIELD_CLASS_TYPE = "classType";
    public final String FIELD_TYPE = "type";
    public final String FIELD_ROLE_NAME = "roleName";
    public final String FIELD_ROLE_ID = "roleID";
    public final String FIELD_MSG = "msg";
    public final String FIELD_PORTRAIT = "portrait";
    public final String FIELD_LIKES = "likes";
    public final String FIELD_CREATE_TIME = "createTime";
    public final String FIELD_GANG_ID = "gangID";
    public final String FIELD_USER_LIKES = "userLikes";
    public final String FIELD_RED_PACKET = "redPacket";
    public final String FIELD_FLAG = "flag";
    public WorldTreeSql() {
        super("worldtree");
        initWorldTree();
    }

    public void initWorldTree() {
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try {
            Calendar calendar = Calendar.getInstance();
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            if(hours < 6){
                calendar.add(Calendar.DATE,-1);
            }
            calendar.set(Calendar.HOUR_OF_DAY,6);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            int time = JkTools.getGameServerTime(calendar.getTimeInMillis());
            cmd = "select guid,classType,type,roleName,roleID,msg,portrait,likes,createTime,gangID,userLikes,redPacket from worldtree where flag = 0 and createTime >= " + time +" order by createTime";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            while (dataSet.next()) {
                int index = 1;
                TalkMsgPVo pVo = new TalkMsgPVo();
                pVo.ID = dataSet.getLong(index++);
                pVo.classType = dataSet.getByte(index++);
                pVo.type = dataSet.getByte(index++);
                pVo.roleName = dataSet.getString(index++);
                pVo.roleID = dataSet.getLong(index++);
                pVo.msg = dataSet.getString(index++);
                pVo.portrait = dataSet.getByte(index++);
                pVo.likes = dataSet.getShort(index++);
                pVo.createTime = dataSet.getInt(index++);
                pVo.gangID = dataSet.getLong(index++);
                TalkModel.addTalkMsg(pVo,null,false);
                TalkModel.loadUserLikes(pVo.ID,dataSet.getString(index++));
                TalkModel.loadRedPacket(pVo.ID,dataSet.getString(index++));
            }
            dataSet.close();
        } catch (Exception e) {
            new Exception("ReconnectError").printStackTrace();
            e.printStackTrace();
        } finally {
            SqlPool.release(conn);
        }
    }

    public void loadSystemTalk(){
        Connection conn = SqlPool.getConn();
        String cmd;
        ResultSet dataSet = null;
        try {
            Calendar calendar = Calendar.getInstance();
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            if(hours < 6){
                calendar.add(Calendar.DATE,-1);
            }
            calendar.set(Calendar.HOUR_OF_DAY,6);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            int time = JkTools.getGameServerTime(calendar.getTimeInMillis());
            cmd = "select guid,classType,type,roleName,roleID,msg,portrait,likes,createTime,userLikes,redPacket from worldtree where classType = "+ TalkEnum.OFFICIAL+" and flag = 0 and createTime >= " + time +" and roleID < "+ LoopSendTalkModel.baseID +" order by createTime";
            Statement state = conn.createStatement();
            dataSet = state.executeQuery(cmd);
            while (dataSet.next()) {
                int index = 1;
                TalkMsgPVo pVo = new TalkMsgPVo();
                pVo.ID = dataSet.getLong(index++);
                pVo.classType = dataSet.getByte(index++);
                pVo.type = dataSet.getByte(index++);
                pVo.roleName = dataSet.getString(index++);
                pVo.roleID = dataSet.getLong(index++);
                pVo.msg = dataSet.getString(index++);
                pVo.portrait = dataSet.getByte(index++);
                pVo.likes = dataSet.getShort(index++);
                pVo.createTime = dataSet.getInt(index++);
                LoopSendTalkModel.addSystemTalk(pVo,null,false);
            }
            dataSet.close();
        } catch (Exception e) {
            new Exception("ReconnectError").printStackTrace();
            e.printStackTrace();
        } finally {
            SqlPool.release(conn);
        }
    }

    public boolean insertNew(TalkMsgPVo talkMsgPVo) {

        long guid = AllSql.worldTreeSql.getNewGuid();
        insert(FIELD_GUID+","+FIELD_CLASS_TYPE+","+FIELD_TYPE+","+FIELD_ROLE_NAME+","+FIELD_ROLE_ID+","+FIELD_MSG+","+FIELD_PORTRAIT+","+FIELD_CREATE_TIME+","+FIELD_GANG_ID,
                guid+ ","+talkMsgPVo.classType+ ","+talkMsgPVo.type+",\'"+ talkMsgPVo.roleName +"\',"+talkMsgPVo.roleID+",\'"+ talkMsgPVo.msg +"\',"+talkMsgPVo.portrait+","+talkMsgPVo.createTime+","+talkMsgPVo.gangID);
        talkMsgPVo.ID =  guid;
        return true;
    }

    public void update(TalkMsgPVo talkMsgPVo, String keyName, int value) {
        update(keyName, value + "", talkMsgPVo.ID);
    }

    public void update(TalkMsgPVo talkMsgPVo, String keyName, String value) {
        update(keyName, value, talkMsgPVo.ID);
    }
}
