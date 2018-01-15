package gang.commands;

import comm.BaseRqstCmd;
import comm.CacheUserVo;
import comm.Client;
import comm.User;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.*;
import table.GangOfficeEnum;

import java.util.Iterator;

/**
 * Created by admin on 2016/11/7.
 */
public class AdoptCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        AdoptRqst rqst = (AdoptRqst) baseRqst;
        adopt(client,user,rqst.userID);
    }

    private void adopt(Client client,User user,long userID){
        if(user.cacheUserVo.gang.gangVo == null)return;
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(user.cacheUserVo.gang.isMember(user.guid))return;
        if(!gangVo.gangApplyMap.containsKey(userID))return;
        if(!CacheUserVo.allMap.containsKey(userID))return;
        CacheUserVo cacheUserVo = CacheUserVo.allMap.get(userID);
        if(cacheUserVo.gang.gangVo != null){
            new ServerTipRspd(client,(short)144,null);
            return;
        }
        if(gangVo.users.size() >= gangVo.maxUserCount){
            new ServerTipRspd(client,(short)133,null);
            return;
        }
        gangVo.gangApplyMap.remove(userID);
        Iterator<GangApplyPVo> it = gangVo.gangApplyList.iterator();
        while (it.hasNext()){
            GangApplyPVo pVo = it.next();
            if(pVo.userID != userID)continue;
            it.remove();
            break;
        }
        gangVo.saveGangApplyInfo();
        cacheUserVo.gang.join(gangVo, cacheUserVo, GangOfficeEnum.MEMBER,false);
        gangVo.createGangInfoRspd(client);
        new AuditingRspd(client,userID);
    }
}
