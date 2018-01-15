package friend;

import comm.*;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.AttentionListRqst;
import protocol.AttentionRqst;
import protocol.AttentionRspd;
import protocol.ServerTipRspd;
import sqlCmd.AllSql;

import java.util.Set;

/**
 * Created by admin on 2017/4/5.
 */
public class AttentionCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        AttentionRqst rqst = (AttentionRqst)baseRqst;
        if(user.guid == rqst.guid || rqst.guid == 0)return;
        Set<Long> myAttention = FriendModel.getMyAttention(user.guid);
        if(rqst.type == 1){//关注
            if(myAttention.size() >= Model.GameSetBaseMap.get(31).intArray[0]){
                new ServerTipRspd(client,(short)245,null);
                return;
            }
            CacheUserVo cacheUserVo = CacheUserVo.allMap.get(rqst.guid);
            if(myAttention.contains(cacheUserVo.guid)){
                new ServerTipRspd(client,(short)251,null);
                return;
            }
            if(user.friendModel.userTimeMap.containsKey(cacheUserVo.guid)){
                int time = JkTools.getGameServerTime(client) - user.friendModel.userTimeMap.get(cacheUserVo.guid);
                if(time<0) {
                    time = -time;
                    if(time/60 > 0){
                        new ServerTipRspd(client,(short)247,time/60+"分钟");
                    }else{
                        new ServerTipRspd(client,(short)247,time%60+"秒");
                    }
                    return;
                }
            }
            if(FriendModel.isMyAttention(cacheUserVo.guid,user.guid)){
                RelationVo relationVo = FriendModel.getRelationVo(FriendModel.myAttentionMap,cacheUserVo.guid,user.guid);
                if(relationVo == null)return;
                relationVo.relation = 3;
                AllSql.friendSql.update(relationVo,AllSql.friendSql.FIELD_RELATION,relationVo.relation);
                FriendModel.addAttention(user.guid,cacheUserVo.guid,relationVo);
            }else{
                RelationVo relationVo = new RelationVo();
                relationVo.user1 = user.guid;
                relationVo.user2 = cacheUserVo.guid;
                relationVo.relation = 1;
                AllSql.friendSql.insertNew(relationVo);
                FriendModel.addAttention(user.guid,cacheUserVo.guid,relationVo);
            }
            FriendModel.addSysInfo((byte)1,user.cacheUserVo,cacheUserVo);
            new ServerTipRspd(client,(short)249,cacheUserVo.name);
        }
        if(rqst.type == 2){//取关
            CacheUserVo cacheUserVo = CacheUserVo.allMap.get(rqst.guid);
            if(!myAttention.contains(cacheUserVo.guid))return;
            RelationVo relationVo = FriendModel.getRelationVo(FriendModel.myAttentionMap,user.guid,cacheUserVo.guid);
            if(relationVo == null)return;
            if(relationVo.relation == 3){
                relationVo.relation = 2;
                AllSql.friendSql.update(relationVo,AllSql.friendSql.FIELD_RELATION,relationVo.relation);
                FriendModel.delAttention(user.guid,cacheUserVo.guid);
            }else{
                FriendModel.myAttentionMap.get(user.guid).remove(cacheUserVo.guid);
                AllSql.friendSql.delete(relationVo.guid);
                FriendModel.delAttention(user.guid,cacheUserVo.guid);
            }
            user.friendModel.userTimeMap.put(cacheUserVo.guid,JkTools.getGameServerTime(client)+5*60);
            AttentionListRqst attentionListRqst = new AttentionListRqst();
            attentionListRqst.startIndex = 0;
            attentionListRqst.type = 1;
            new AttentionListCmd().execute(client,user,attentionListRqst);
        }
        new AttentionRspd(client,rqst.type,rqst.guid,myAttention.size());
    }
}
