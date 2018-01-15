package illustrated;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.AglowIllustratedRqst;
import protocol.AglowIllustratedRspd;
import protocol.MonsterGroupPVo;
import protocol.MonsterPVo;

/**
 * Created by admin on 2016/8/17.
 */
public class AglowIllustratedCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        AglowIllustratedRqst rqst = (AglowIllustratedRqst) baseRqst;
        aglowIllustrated(client,user,rqst.mapIndex,rqst.illustratedID);
    }

    private void aglowIllustrated(Client client, User user,int mapIndex,int illustratedID){
        if(user.illustratedModel.monsterGroupMap.containsKey(mapIndex)){
            MonsterGroupPVo pVo = user.illustratedModel.monsterGroupMap.get(mapIndex);
                for(MonsterPVo monsterPVo : pVo.monsterList){
                    if(monsterPVo.illustratedID == illustratedID){
                        if(monsterPVo.count < monsterPVo.needCount)return;
                        monsterPVo.count = 0;
                        monsterPVo.isAglow = 1;
                        user.illustratedModel.saveSqlData();
                    }
                }
            new AglowIllustratedRspd(client,pVo);
        }
    }
}
