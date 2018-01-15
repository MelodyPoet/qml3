package prop;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.ComposeClipsRqst;
import table.PropComposeBaseVo;
import table.ReasonTypeEnum;
import table.UserDataEnum;

import java.util.List;


public class ComposeClipsCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
        ComposeClipsRqst rqst=(ComposeClipsRqst) baseRqst;

if(user.isCDClear(client,UserDataEnum.CD_COMPOSE_CLIPS)==false)return;



       PropComposeBaseVo propComposeBaseVo = Model.PropComposeBaseMap.get(rqst.baseID);
        if(propComposeBaseVo ==null||rqst.clips.size()==0)return;

       if( propComposeBaseVo.needChips.length!=rqst.clips.size())return;
        for (int i = 0; i < propComposeBaseVo.needChips.length ; i++) {
            if(!user.propModel.bagItems.containsKey(((List<Long>)rqst.clips).get(i)))return;
           if(user.propModel.bagItems.get(((List<Long>)rqst.clips).get(i)).baseID!= propComposeBaseVo.needChips[i])return;
        }
        for (int i = 0; i < propComposeBaseVo.needChips.length ; i++) {
            user.costUserDataAndProp(user.propModel.bagItems.get(((List<Long>)rqst.clips).get(i)).baseID,1,true, ReasonTypeEnum.COMPOSE_PROP);
        }
if(propComposeBaseVo.cd>0) {
    user.setUserData(UserDataEnum.CD_COMPOSE_CLIPS, JkTools.getGameServerTime(client) + propComposeBaseVo.cd,true);
}
  user.propModel.addListToBag(new int[]{rqst.baseID,1},ReasonTypeEnum.COMPOSE_PROP);

 	}

	 

}
