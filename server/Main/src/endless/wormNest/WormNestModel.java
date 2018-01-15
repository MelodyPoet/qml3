package endless.wormNest;

import airing.AiringModel;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.utils.JkTools;
import protocol.AiringMessagePVo;
import table.HonorAiringBaseVo;
import table.UserDataEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2016/7/11.
 */
public class WormNestModel {
    public static Map<Long,RankUserWormNestVo> wormNestMap = new HashMap<>();
    public static Map<Integer,Integer> floorUserCountMap = new HashMap<>();
    public static final int WORM_NEST_RANK_SIZE = 50;
    private User user;

    public WormNestModel(User user){
        this.user = user;
    }

    public void challengeSuccess(Client client){
        int currentFloor = user.getUserData(UserDataEnum.WormNestCurrentFloor);
        int maxFloor = user.getUserData(UserDataEnum.WormNestMaxFloor);

        if (currentFloor - maxFloor == 1) {
//            user.propModel.addListToBag(Model.TowerBaseMap.get(1).get(user.getUserData(UserDataEnum.WormNestCurrentFloor)-1).rewardProp);
            user.addUserData(UserDataEnum.WormNestCurrentFloor, 1, true);
            user.addUserData(UserDataEnum.WormNestMaxFloor, 1, true);
            user.setUserData(UserDataEnum.WormNestMaxFloorTime, JkTools.getGameServerTime(client),true);
            RankUserWormNestVo.toRank(user);

            //走马灯
            HonorAiringBaseVo airingVo = Model.HonorAiringBaseMap.get(4);
            if(airingVo==null)return;
            if(airingVo.isUse != 1)return;
            int floor = user.getUserData(UserDataEnum.WormNestMaxFloor);
            if(floor%airingVo.divisor != 0)return;
            if(JkTools.compare(floor,airingVo.conditionParams[1],airingVo.conditionParams[0]) == false)return;
            if(airingVo.conditionParams.length>=4&&JkTools.compare(floor,airingVo.conditionParams[3],airingVo.conditionParams[2]) == false)return;
            AiringMessagePVo pVo = new AiringMessagePVo();
            pVo.type = 1;
//            pVo.msg = "恭喜 "+user.cacheUserVo.passportVo.name+" 通过了毒虫巢穴第 "+floor+" 层。";
            pVo.msg = airingVo.msg.replace("{1}",user.cacheUserVo.name).replace("{2}",String.valueOf(floor));
            pVo.time = 1;
            ArrayList<AiringMessagePVo> list = new ArrayList<>();
            list.add(pVo);
            AiringModel.broadcast(list);
        } else if (currentFloor - maxFloor < 1) {
//            user.propModel.addListToBag(Model.TowerBaseMap.get(1).get(user.getUserData(UserDataEnum.WormNestCurrentFloor)-1).rewardProp);
            user.addUserData(UserDataEnum.WormNestCurrentFloor, 1, true);
        }
    }

    public boolean isCanChallenge(){
        if(user.getUserData(UserDataEnum.WormNestCurrentFloor) - user.getUserData(UserDataEnum.WormNestMaxFloor) > 1)return false;
        return true;
    }
}
