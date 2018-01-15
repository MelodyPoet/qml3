package redness;

import comm.Model;
import gameset.GameSetModel;
import heroTag.HeroTagModel;
import mail.LoopSendMailModel;
import mail.MailModel;
import protocol.MailPVo;
import protocol.RednessWinnerPVo;
import rank.RankModel;
import table.GameSetBaseVo;
import utils.UserVoAdapter;

import java.util.HashMap;

/**
 * Created by admin on 2017/3/30.
 */
public class RednessLoopModel {


    public static void recordRednessRank(){
        RednessModel.lastRednessRank.clear();
        RednessModel.campUserPVo = null;
        RednessModel.lastMailID = 0;
        if(RankModel.rankRednessList.size() == 0){
            GameSetModel.gameSetVo.saveRedness();
            return;
        }
        RednessModel.campUserPVo = UserVoAdapter.toCampUserPVo(RankModel.rankRednessList.get(0).cacheUserVo);
        for(RankRednessVo rankRednessVo : RankModel.rankRednessList.list){
            if(rankRednessVo.orderIndex >= 3)break;
            RednessWinnerPVo rednessWinnerPVo = new RednessWinnerPVo();
            rednessWinnerPVo.index = (byte) rankRednessVo.orderIndex;
            rednessWinnerPVo.name = rankRednessVo.cacheUserVo.name;
            rednessWinnerPVo.rednessMoney = rankRednessVo.weekRednessMoney;
            RednessModel.lastRednessRank.add(rednessWinnerPVo);
        }
        MailPVo mailPVo = MailModel.createMail(100002,(long)0);
        LoopSendMailModel.addSystemMail(mailPVo,true);
        HashMap<Long,Integer> map = new HashMap<>();
        LoopSendMailModel.rankUsers.put(mailPVo.guid,map);
        GameSetBaseVo gameSetBaseVo = Model.GameSetBaseMap.get(73);
        for (RankRednessVo rankRednessVo : RankModel.rankRednessList.list) {
            if(rankRednessVo.orderIndex < gameSetBaseVo.intValue){
                HeroTagModel.addHeroTag(gameSetBaseVo.intArray,rankRednessVo.orderIndex+1,rankRednessVo.cacheUserVo);
            }
            map.put(rankRednessVo.cacheUserVo.guid,rankRednessVo.orderIndex);
            rankRednessVo.cacheUserVo.rankRednessVo = null;
        }
        RankModel.rankRednessList.list.clear();
        MailModel.sendMailToOnLine(mailPVo);
        LoopSendMailModel.saveRankUser(mailPVo.guid);
        RednessModel.lastMailID = mailPVo.guid;
        GameSetModel.gameSetVo.saveRedness();
    }
}
