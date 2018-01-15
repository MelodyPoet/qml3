package gang.guess;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gameset.GameSetModel;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import mail.LoopSendMailModel;
import mail.MailModel;
import protocol.*;
import table.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 2017/4/19.
 */
public class GuessCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GuessRqst rqst = (GuessRqst)baseRqst;
        int num = rqst.num;
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(gangVo == null)return;
        GangUserVo vo = user.cacheUserVo.gang.gangVo.users.get(user.guid);
        if(vo == null)return;
        int count = vo.getGangUserData(GangUserDataEnum.GUESS_COUNT);
        if(count <= 0)return;
        GuessModel guessModel = gangVo.guessModel;
        if(!GameSetModel.checkTime(3))return;
        if(num >= guessModel.guessMax || num <= guessModel.guessMin){
            new ServerTipRspd(client,(short)257,null);
            new GuessInfoRspd(client,guessModel.guessMin,guessModel.guessMax,guessModel.guessDouble,guessModel.guessTimes,guessModel.logList,guessModel.lastWinner,guessModel.isGetKey);
            return;
        }
        VipBaseVo vipBaseVo = Model.VipBaseMap.get((int)client.passportVo.vip);
        if(vipBaseVo==null)return;
        int id = vipBaseVo.guessCount - count;
        ArrayList<CommCostBaseVo> costList = Model.CommCostBaseMap.get((int) ModuleUIEnum.GUESS);
        if(costList == null)return;
        if(id < 0)return;
        CommCostBaseVo costBaseVo = costList.get(id);
        if(costBaseVo == null)return;
        if(user.costUserDataAndPropList(costBaseVo.costUserdata,true,ReasonTypeEnum.GUESS,null) == false)return;
        vo.setGangUserData(GangUserDataEnum.GUESS_COUNT,count - 1,true);
        boolean isOver = false;
        GangGuessLogPVo gangGuessLogPVo = new GangGuessLogPVo();
        gangGuessLogPVo.guid = user.guid;
        gangGuessLogPVo.name = user.cacheUserVo.name;
        gangGuessLogPVo.num = num;
        gangGuessLogPVo.portrait = user.cacheUserVo.portrait;
        gangGuessLogPVo.time = JkTools.getGameServerTime(client);
        if(num == guessModel.guessRandom){
            gangGuessLogPVo.type = 1;
            isOver =  true;
        }else if(num > guessModel.guessRandom){
            if(num - guessModel.guessRandom < 5){
                gangGuessLogPVo.type = 3;
            }else{
                gangGuessLogPVo.type = 2;
                guessModel.guessMax = num;
            }
        }else if(num < guessModel.guessRandom){
            if(num - guessModel.guessRandom > -5){
                gangGuessLogPVo.type = 3;
            }else{
                gangGuessLogPVo.type = 0;
                guessModel.guessMin = num;
            }
        }
        guessModel.logList.add(gangGuessLogPVo);
        user.propModel.addListToBag(Model.GameSetBaseMap.get(36).intArray,ReasonTypeEnum.GUESS);
        user.activationModel.progressBuyAct(MissionConditionEnum.GANG_GUESS,0);
        new GuessRspd(client,gangGuessLogPVo);
        for(GangUserVo gangUserVo : gangVo.users.values()){
            if(gangUserVo.cacheUserVo.guid == gangGuessLogPVo.guid)continue;
            gangUserVo.cacheUserVo.addGuessLogList.add(gangGuessLogPVo);
        }
        if(isOver){
            guessModel.lastWinner = gangGuessLogPVo;
            guessModel.over();
            new GuessInfoRspd(client,guessModel.guessMin,guessModel.guessMax,guessModel.guessDouble,guessModel.guessTimes,guessModel.logList,guessModel.lastWinner,guessModel.isGetKey);
            MailPVo mailPVo = MailModel.createMail(10005,user.guid);
            ArrayList<AnnexPropPVo> list = new ArrayList<>();
            int[] data = Model.GameSetBaseMap.get(45).intArray;
            for(int i=0;i<data.length;i+=2){
                AnnexPropPVo pVo = new AnnexPropPVo();
                pVo.propID = data[i];
                pVo.count = data[i+1];
                list.add(pVo);
            }
            mailPVo.prop = list;
            user.mailModel.sendMail(mailPVo,true);
            MailPVo pVo = MailModel.createMail(100003,(long)0);
            LoopSendMailModel.addSystemMail(pVo,true);
            HashMap<Long,Integer> rankUsers = new HashMap<>();
            for(GangUserVo gangUserVo : gangVo.users.values()){
                rankUsers.put(gangUserVo.cacheUserVo.guid,(int)guessModel.guessDouble);
            }
            LoopSendMailModel.rankUsers.put(pVo.guid,rankUsers);
            MailModel.sendMailToOnLine(pVo);
            LoopSendMailModel.saveRankUser(pVo.guid);
        }
    }
}
