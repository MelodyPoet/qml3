package base;
import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.GeneralSuccessRspd;
import protocol.SkinChangeRqst;
import sqlCmd.AllSql;
import table.RoleBaseVo;


public class SkinChangeCmd extends BaseRqstCmd {

    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {

        SkinChangeRqst rqst = (SkinChangeRqst) baseRqst;
       if(!user.cacheUserVo.skinModel.skinMap.containsKey((short)rqst.baseID)){
           RoleBaseVo roleBaseVo = Model.RoleBaseMap.get((int)user.baseID);
           if(roleBaseVo == null)return;
           if(rqst.baseID != roleBaseVo.defaultSkin){
               return;
           }else{
               user.cacheUserVo.skin= (byte) 0;
               AllSql.userSql.update(user,AllSql.userSql.FIELD_SKIN,0);
               user.updateZDL();
               new GeneralSuccessRspd(client,SkinChangeRqst.PRO_ID);
               return;
           }
       }
        user.cacheUserVo.skin= (byte) rqst.baseID;
        AllSql.userSql.update(user,AllSql.userSql.FIELD_SKIN,rqst.baseID);
        user.updateZDL();
        new GeneralSuccessRspd(client,SkinChangeRqst.PRO_ID);
    }
}
