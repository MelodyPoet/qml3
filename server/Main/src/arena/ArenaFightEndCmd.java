package arena;

import airing.AiringModel;
import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import mail.MailModel;
import protocol.*;
import rank.RankModel;
import sqlCmd.AllSql;
import table.ArenaRewardBaseVo;
import table.HonorAiringBaseVo;
import table.ReasonTypeEnum;
import table.UserDataEnum;

import java.util.ArrayList;


public class ArenaFightEndCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
        ArenaFightEndRqst rqst = (ArenaFightEndRqst) baseRqst;

if(user.arenaModel.cacheArenaFighting.timeout){
    return;
}
        RankUserArenaVo targetArenaVo = user.arenaModel.fightingTarget;

        ArenaRecordPVo recordPVo = new ArenaRecordPVo();
        recordPVo.tempID1 = user.guid;
        recordPVo.name1 = user.cacheUserVo.name;
        recordPVo.level1 = user.cacheUserVo.level;
        recordPVo.zdl1 = user.cacheUserVo.zdl;
        recordPVo.portrait1 = user.cacheUserVo.portrait;
        recordPVo.vip1 = user.cacheUserVo.passportVo.vip;
        recordPVo.tempID2 = targetArenaVo.cacheUserVo.guid;
        recordPVo.name2 = targetArenaVo.cacheUserVo.name;
        recordPVo.level2 = targetArenaVo.cacheUserVo.level;
        recordPVo.zdl2 = targetArenaVo.cacheUserVo.zdl;
        recordPVo.portrait2 = targetArenaVo.cacheUserVo.portrait;
        recordPVo.vip2 = targetArenaVo.cacheUserVo.passportVo.vip;
        recordPVo.time = JkTools.getGameServerTime(null);

        user.cacheUserVo.rankArenaVo.addArenaRecord(recordPVo);
        ArenaModel.saveArenaRecord(user.cacheUserVo.rankArenaVo);
        targetArenaVo.addArenaRecord(recordPVo);
        ArenaModel.saveArenaRecord(targetArenaVo);
        recordPVo.isWin = rqst.isWin;
        user.arenaModel.cacheArenaFighting.completed=true;
//        user.arenaModel.nextFightTime= JkTools.getGameServerTime(client)+Model.MapBaseMap.get(1000).coolingTime;
if(rqst.isWin&&targetArenaVo.orderIndex<user.cacheUserVo.rankArenaVo.orderIndex){

    RankModel.rankArenaList.swap(targetArenaVo,user.cacheUserVo.rankArenaVo);
        AllSql.userSql.update(user,AllSql.userSql.FIELD_ARENA_RANK,user.cacheUserVo.rankArenaVo.orderIndex);
        AllSql.userSql.update(targetArenaVo.cacheUserVo.guid,AllSql.userSql.FIELD_ARENA_RANK,targetArenaVo.orderIndex);
        upBestRecord(user,user.cacheUserVo.rankArenaVo.orderIndex);
        airing(user.cacheUserVo.rankArenaVo);
}
        ArenaRewardBaseVo rewardBaseVo= Model.ArenaRewardBaseMap.get(user.getUserData(UserDataEnum.LEVEL));
        user.propModel.addListToBag(rewardBaseVo.pvpReward, ReasonTypeEnum.ARENA_FIGHT);
        user.addUserData(UserDataEnum.LJ_ARENA_COUNT,1,true);
        //new GeneralSuccessRspd(client);

      //  user.actState = UserActState.CAMP;
     //   new GoMapCmd().execute(client, user, 1, true);

    }

    private static void airing(RankUserArenaVo arenaVo){
        //走马灯
        HonorAiringBaseVo airingVo = Model.HonorAiringBaseMap.get(7);
        if(airingVo==null)return;
        if(airingVo.isUse != 1)return;
        int index = arenaVo.orderIndex + 1;
        if(index%airingVo.divisor != 0)return;
        if(JkTools.compare(index,airingVo.conditionParams[1],airingVo.conditionParams[0]) == false)return;
        if(airingVo.conditionParams.length>=4&&JkTools.compare(index,airingVo.conditionParams[3],airingVo.conditionParams[2]) == false)return;
        AiringMessagePVo pVo = new AiringMessagePVo();
        pVo.type = 1;
//        pVo.msg = "恭喜 "+arenaVo.cacheUserVo.passportVo.name+" 成为竞技场第 "+index+" 名。";
        pVo.msg = airingVo.msg.replace("{1}",arenaVo.cacheUserVo.name).replace("{2}",String.valueOf(index));
        pVo.time = 1;
        ArrayList<AiringMessagePVo> list = new ArrayList<>();
        list.add(pVo);
        AiringModel.broadcast(list);
    }

    private void upBestRecord(User user,int orderIndex){
        if(orderIndex>=1000 || orderIndex >= user.arenaModel.bestRecord)return;
        int count = user.arenaModel.bestRecord - orderIndex;
        if(count > 1000)return;
        MailPVo mailPVo = MailModel.createMail(10004,user.guid);
//        mailPVo.diamond = count;
        AnnexPropPVo pVo = new AnnexPropPVo();
        pVo.propID = UserDataEnum.DIAMOND;
        pVo.count = count;
        mailPVo.prop.add(pVo);
        user.mailModel.sendMail(mailPVo,true);
        user.arenaModel.bestRecord = orderIndex;
        user.arenaModel.saveSqlData();
    }
}
