package gang;


import comm.CacheUserVo;
import comm.Model;
import protocol.UpdateGangUserDataRspd;
import sqlCmd.AllSql;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import table.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by admin on 2016/6/29.
 */
public class GangUserVo {
    public Long guid;
    public CacheUserVo cacheUserVo;
    public byte office;
    public int contributionToday;
    public byte contributeTime;
    public int endCDTime;
    public byte likeTime;
    public int luckValue;
    public byte isGetGift;
    public int[] userdata;
    public ArrayList<Integer> getBoxID;

    public GangUserVo(){
        userdata = new int[GangUserDataEnum.MAX];
        getBoxID = new ArrayList<>();
    }
    public void initData(){
        VipBaseVo vipBaseVo = Model.VipBaseMap.get((int)cacheUserVo.passportVo.vip);
        if(vipBaseVo==null)return;
        for (UserUpdateBaseVo vo : Model.UserUpdateBaseMap.values()) {
            if(vo.dataType != 2)continue;
            if(vo.init>=0){
                setGangUserData((byte) vo.dataID,vo.init, true);
            }else{
                switch (vo.dataID){
                    case GangUserDataEnum.DIVINATION_COUNT :
                        setGangUserData((byte) vo.dataID,vipBaseVo.divinationCount, true);
                        break;
                    case GangUserDataEnum.GUESS_COUNT :
                        setGangUserData((byte) vo.dataID,vipBaseVo.guessCount, true);
                        break;
                }
            }
        }
    }
    public void addGangUserData(byte type,int value,boolean sendRspd){
        setGangUserData(type,userdata[type]+value,sendRspd);
    }
    public void setGangUserData(byte type,int value,boolean sendRspd){
        userdata[type] = value;
        if(sendRspd){
            if(cacheUserVo.onlineUser != null){
                new UpdateGangUserDataRspd(cacheUserVo.onlineUser.client,type,value);
            }
        }
        switch (type){
            case GangUserDataEnum.DIVINATION_COUNT:
                AllSql.gangMemberSql.update(this, AllSql.gangMemberSql.FIELD_DIVINATION_COUNT,value);
                break;
            case GangUserDataEnum.GUESS_COUNT:
                AllSql.gangMemberSql.update(this, AllSql.gangMemberSql.FIELD_GUESS_COUNT,value);
                break;
            case GangUserDataEnum.DRAGON_JINPO:
                AllSql.gangMemberSql.update(this, AllSql.gangMemberSql.FIELD_DRAGON_JINPO,value);
                break;
            case GangUserDataEnum.MAP_COUNT:
                AllSql.gangMemberSql.update(this, AllSql.gangMemberSql.FIELD_MAP_COUNT,value);
                break;
            case GangUserDataEnum.CONTRIBUTION:
                AllSql.gangMemberSql.update(this, AllSql.gangMemberSql.FIELD_CONTRIBUTION,value);
                break;
        }
    }
    public int getGangUserData(byte type){
        return userdata[type];
    }

    public void loadGetBoxID(String str){
        if (str == null || str.length() < 1){
            return;
        }
        byte[] bytes = null;
        try {
            bytes = new BASE64Decoder().decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        short count = buffer.getShort();
        for(byte i=0;i<count;i++){
            getBoxID.add(buffer.getInt());
        }
    }

    public void saveGetBoxID(){
        byte[] bytes = new byte[getBoxID.size()*4+2];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putShort((short) getBoxID.size());
        for(int id : getBoxID){
            buffer.putInt(id);
        }
        AllSql.gangMemberSql.update(this,AllSql.gangMemberSql.FIELD_GET_BOX_ID,"'"+new BASE64Encoder().encode(bytes)+"'");
    }
}
