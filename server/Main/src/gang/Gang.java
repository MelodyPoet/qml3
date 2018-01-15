package gang;

import comm.CacheUserVo;
import comm.Client;
import comm.Model;
import protocol.*;
import sqlCmd.AllSql;
import table.GangOfficeEnum;

import java.util.*;

/**
 * Created by admin on 2016/6/29.
 */
public class Gang {
    public static Map<Long,GangVo> allGangMap = new HashMap<>();
    public static ArrayList<GangVo> allGangList = new ArrayList<>();
    public static HashSet<String> usedName = new HashSet<>();
    public GangVo gangVo;
    public ArrayList<GangTalkMsgPVo> addTalkList = new ArrayList<>();

    public void join(GangVo gangVo, CacheUserVo cacheUserVo, byte office,boolean isActive){
        this.gangVo = gangVo;
        GangUserVo userVo = new GangUserVo();
        userVo.office = office;
        userVo.cacheUserVo = cacheUserVo;
        userVo.contributeTime = (byte)Model.GameSetBaseMap.get(8).intArray[0];
        userVo.endCDTime = -1;
        userVo.likeTime = (byte) Model.GameSetBaseMap.get(9).intArray[2];
        if(userVo.office == GangOfficeEnum.MASTER){
            gangVo.master = userVo;
            RankGangUserVo rankGangUserVo = new RankGangUserVo(userVo);
            gangVo.rankGangUserList.addEnd(rankGangUserVo);
        }else{
            gangVo.zdl += userVo.cacheUserVo.zdl;
            AllSql.gangSql.update(gangVo,AllSql.gangSql.FIELD_ZDL,gangVo.zdl);
            gangVo.addLog((short) 1001,cacheUserVo.name);
            RankGangUserVo rankGangUserVo = new RankGangUserVo(userVo);
            gangVo.rankGangUserList.addEnd(rankGangUserVo);
            gangVo.rankGangUserList.sortItem(rankGangUserVo,rankGangUserVo.orderScore());
        }
        gangVo.users.put(cacheUserVo.guid,userVo);
        AllSql.gangMemberSql.insertNew(userVo,gangVo.gangID);
        userVo.initData();
        if(isActive){
            Client client = Client.getOne(userVo.cacheUserVo.passportVo.guid);
            if(client != null && client.currentUser.guid == cacheUserVo.guid){
                cacheUserVo.gang.gangVo.createGangInfoRspd(client);
                client.currentUser.gangSkillModel.init();
                client.currentUser.updateZDL();
                ByteIntPVo dragon = gangVo.mapModel.gangDragon;
                new GangDragonRspd(client,dragon.value,dragon.key);
            }
        }else{
            cacheUserVo.gangStatus = 1;
            AllSql.userSql.update(cacheUserVo.guid,AllSql.userSql.FIELD_GANG_STATUS,1);
        }
    }

    public static void addOne(GangVo gangVo){
        allGangMap.put(gangVo.gangID,gangVo);
        allGangList.add(gangVo);
        usedName.add(gangVo.gangName);
    }

    public static GangUserVo nextMaster(GangVo gangVo,long masterID){
        GangUserVo newMaster = new GangUserVo();
        byte vipMax = -1;
        for(GangUserVo gangUserVo : gangVo.users.values()){
            if(gangUserVo.cacheUserVo.guid == masterID)continue;
            if(gangUserVo.cacheUserVo.passportVo.vip > vipMax){
                newMaster = gangUserVo;
                vipMax = gangUserVo.cacheUserVo.passportVo.vip;
            }else if(gangUserVo.cacheUserVo.passportVo.vip == vipMax && gangUserVo.cacheUserVo.zdl > newMaster.cacheUserVo.zdl){
                newMaster = gangUserVo;
                vipMax = gangUserVo.cacheUserVo.passportVo.vip;
            }
        }
        return newMaster;
    }

    public  static void sort(){
        Collections.sort(allGangList,new Comparator<GangVo>() {
            @Override
            public int compare(GangVo o1, GangVo o2) {
                if(o2.level == o1.level){
                    return o2.zdl-o1.zdl;
                }else{
                    return o2.level-o1.level;
                }
            }
        });
        for(int i=0;i<allGangList.size();i++){
            allGangList.get(i).rank = i;
        }
    }

    public boolean isMaster(Long userID){
        if(gangVo == null || gangVo.master.cacheUserVo.guid != userID)
            return false;
        return true;
    }

    public boolean isDeputyOrMaster(Long userID){
        if(isMaster(userID))
            return true;
        if(gangVo==null)
            return false;
        return (gangVo.users.get(userID).office == GangOfficeEnum.DEPUTY);
    }

    public boolean isMember(Long userID){
        if(gangVo == null || !gangVo.users.containsKey(userID))
            return false;
        return gangVo.users.get(userID).office == GangOfficeEnum.MEMBER;
    }
}
