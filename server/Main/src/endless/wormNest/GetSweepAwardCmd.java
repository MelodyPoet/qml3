package endless.wormNest;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.GeneralSuccessRspd;
import protocol.GetSweepAwardRqst;
import table.ReasonTypeEnum;
import table.TowerBaseVo;
import table.UserDataEnum;

import java.util.ArrayList;

/**
 * Created by admin on 2016/7/9.
 */
public class GetSweepAwardCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GetSweepAwardRqst rqst = (GetSweepAwardRqst) baseRqst;
        getSweepAward(client,user);
    }

    private void getSweepAward(Client client, User user){
        ArrayList<TowerBaseVo> towerBaseVoList = Model.TowerBaseMap.get(1);
        int [] rewardProp = new int[20];
        for(int i = user.getUserData(UserDataEnum.WormNestCurrentFloor); i<=user.getUserData(UserDataEnum.WormNestMaxFloor); i++){
            TowerBaseVo towerBaseVo = towerBaseVoList.get(i-1);
            if(towerBaseVo == null){
                return;
            }
            if(towerBaseVo.rewardProp==null)continue;
            for (int j =0;j<towerBaseVo.rewardProp.length;j+=2) {
                for(int z=0;z<rewardProp.length;z+=2){
                    if(towerBaseVo.rewardProp[j] == rewardProp[z]){
                       rewardProp[z+1] += towerBaseVo.rewardProp[j+1];
                        break;
                    }else if(rewardProp[z] == 0){
                        rewardProp[z] = towerBaseVo.rewardProp[j];
                        rewardProp[z+1] = towerBaseVo.rewardProp[j+1];
                        break;
                    }
                }
            }
        }
        user.propModel.addListToBag(rewardProp, ReasonTypeEnum.WORM_NEST_OPERATE);
        user.setUserData(UserDataEnum.WormNestCurrentFloor,user.getUserData(UserDataEnum.WormNestMaxFloor)+1,true);
        user.setUserData(UserDataEnum.WormNestNextEndTime,0,true);
        new GeneralSuccessRspd(client,GetSweepAwardRqst.PRO_ID);
    }
}
