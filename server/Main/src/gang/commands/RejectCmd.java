package gang.commands;

import comm.BaseRqstCmd;
import comm.CacheUserVo;
import comm.Client;
import comm.User;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.*;

import java.util.Iterator;

/**
 * Created by admin on 2016/11/7.
 */
public class RejectCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        RejectRqst rqst = (RejectRqst) baseRqst;
        reject(client,user,rqst.userID);
    }

    private void reject(Client client,User user,long userID){
        if(user.cacheUserVo.gang.gangVo == null)return;
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(user.cacheUserVo.gang.isMember(user.guid))return;
        if(!CacheUserVo.allMap.containsKey(userID))return;
        if(!gangVo.gangApplyMap.containsKey(userID))return;
        gangVo.gangApplyMap.remove(userID);
        Iterator<GangApplyPVo> it = gangVo.gangApplyList.iterator();
        while (it.hasNext()){
            GangApplyPVo pVo = it.next();
            if(pVo.userID != userID)continue;
            it.remove();
            break;
        }
        gangVo.saveGangApplyInfo();
        new AuditingRspd(client,userID);
        CacheUserVo cacheUserVo = CacheUserVo.allMap.get(userID);
        gangVo.addLog((short) 1007,cacheUserVo.name);
    }
}
