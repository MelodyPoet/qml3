package friend;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.ReceiveTiliRqst;
import protocol.ReceiveTiliRspd;
import protocol.ServerTipRspd;
import sqlCmd.AllSql;
import table.UserDataEnum;

import java.util.ArrayList;

/**
 * Created by admin on 2017/4/27.
 */
public class ReceiveTiliCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        ReceiveTiliRqst rqst = (ReceiveTiliRqst)baseRqst;
        byte count = user.friendModel.canReceiveCount;
        ArrayList<FriendGiftVo> list = user.friendModel.giftList;
        if(count <= 0)return;
        if(rqst.id == 0){
            for(FriendGiftVo friendGiftVo : list){
                if(friendGiftVo.isGet == 1)continue;
                friendGiftVo.isGet = 1;
                AllSql.friendGiftSql.update(friendGiftVo,AllSql.friendGiftSql.FIELD_IS_GET,friendGiftVo.isGet);
                count--;
                if(count <= 0)break;
            }
        }else{
            boolean flag = false;
            for(FriendGiftVo friendGiftVo : list){
                if(friendGiftVo.guid == rqst.id){
                    flag = true;
                    friendGiftVo.isGet = 1;
                    AllSql.friendGiftSql.update(friendGiftVo,AllSql.friendGiftSql.FIELD_IS_GET,friendGiftVo.isGet);
                    break;
                }
            }
            if(!flag)return;
            count--;
        }
        if(user.friendModel.canReceiveCount == count){
            new ServerTipRspd(client,(short)279,null);
            return;
        }
        int times = user.friendModel.canReceiveCount - count;
        user.friendModel.canReceiveCount = count;
        user.addUserData(UserDataEnum.TILIZHI,Model.GameSetBaseMap.get(43).intArray[1]*times,true);
        new ReceiveTiliRspd(client,rqst.id,count);
        user.friendModel.saveSqlData();
    }
}
