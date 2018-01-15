package snatch;

import comm.*;
import gluffy.comm.BaseRqst;
import prop.PropModel;
import protocol.SimpleUserPVo;
import protocol.SnatchGetTargetRqst;
import protocol.SnatchGetTargetRspd;
import table.UserDataEnum;

import utils.UserVoAdapter;

import java.util.ArrayList;
import java.util.HashSet;


public class SnatchGetTargetCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
        if (user.getUserData(UserDataEnum.LEVEL) < SnatchModel.needLvl) return;

        SnatchGetTargetRqst rqst = (SnatchGetTargetRqst) baseRqst;
        //
        if (PropModel.isSnatchItem(rqst.propBaseID) == false) return;
        user.snatchModel.propBaseID = rqst.propBaseID;
        ArrayList<SimpleUserPVo> targets = new ArrayList<>();

        HashSet<CacheUserVo>[] lvlUsers = SnatchModel.allItemOwnners.get(rqst.propBaseID);
        int lvl = user.getUserData(UserDataEnum.LEVEL);

        //3 个 高等级 高概率
        ArrayList<CacheUserVo> targetsHigh = new ArrayList<>();
        //2 个 低等级 低概率
        ArrayList<CacheUserVo> targetsLow = new ArrayList<>();
        int targetLvl;
        for (int i = 0,len=3- targetsHigh.size();i<len; i++) {
            targetLvl=Math.min(lvl+5,Model.userLevelMax);
            targetsHigh.add(MemoryRobot.allSantchUsers[targetLvl].get((int) (MemoryRobot.allSantchUsers[targetLvl].size()* Math.random())));
        }
        for (int i = 0,len=2- targetsLow.size();i<len; i++) {
            targetLvl=Math.max(lvl-5,1);
            targetsLow.add(MemoryRobot.allSantchUsers[targetLvl].get((int) (MemoryRobot.allSantchUsers[targetLvl].size()* Math.random())));
        }
        //用真实玩家替换
        if (lvlUsers != null) {
            ArrayList<CacheUserVo> tempUser=new ArrayList<>();
            for (int i = 4; i <= 6; i++) {
                if (lvl + i >= Model.userLevelMax) break;
                tempUser.addAll(lvlUsers[lvl + i]);
             }
            for (int i = 0,len=Math.min(tempUser.size()/2,3); i <len ; i++) {
             int index=(int)(Math.random()*  tempUser.size());
                targetsHigh.add(tempUser.get(index));
                targetsHigh.remove(0);
                tempUser.remove(index);
            }
           // targetsHigh
            tempUser=new ArrayList<>();
            for (int i = 4; i <= 6; i++) {
                if (lvl - i <= 0) break;
                tempUser.addAll(lvlUsers[lvl - i]);
             }
            for (int i = 0,len=Math.min(tempUser.size()/2,2); i <len ; i++) {
                int index=(int)(Math.random()*  tempUser.size());
                targetsLow.add(tempUser.get(index));
                targetsLow.remove(0);
                tempUser.remove(index);
            }
        }


            user.snatchModel.targets.clear();
            user.snatchModel.targets.addAll(targetsHigh);
            user.snatchModel.targets.addAll(targetsLow);
            for (CacheUserVo uvo : targetsHigh) targets.add(UserVoAdapter.toSimpleUserPVo(uvo));
            for (CacheUserVo uvo : targetsLow) targets.add(UserVoAdapter.toSimpleUserPVo(uvo));

            new SnatchGetTargetRspd(client, targets);


    }

}
