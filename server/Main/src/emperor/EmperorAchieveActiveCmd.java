package emperor;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import gluffy.utils.JkTools;
import protocol.*;
import table.EmperorAchieveBaseVo;

import java.util.ArrayList;
import java.util.Map;

public class EmperorAchieveActiveCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        EmperorAchieveActiveRqst rqst = (EmperorAchieveActiveRqst)baseRqst;
        EmperorModel emperorModel = user.emperorModel;
        ArrayList<EmperorAchieveBaseVo> list = Model.EmperorAchieveBaseMap.get((int)rqst.achieveID);
        if(list == null)return;
        AcheivePVo acheivePVo = emperorModel.achieveMap.get(rqst.achieveID);
        if(acheivePVo == null){
            acheivePVo = new AcheivePVo();
            acheivePVo.achieveID = rqst.achieveID;
        }
        if(acheivePVo.level >= list.size())return;
        EmperorAchieveBaseVo emperorAchieveBaseVo = list.get(acheivePVo.level);
        if(emperorAchieveBaseVo == null)return;
        for(Map.Entry<Integer,Integer> item : emperorAchieveBaseVo.condition.entrySet()){
            EmperorPVo emperorPVo = emperorModel.emperorMap.get((byte)(int)item.getKey());
            if(emperorPVo == null)return;
            int value = 0;
            switch (emperorAchieveBaseVo.type){
                case 1:
                    value = emperorPVo.advance;
                    break;
                case 2:
                    value = emperorPVo.quality;
                    break;
                case 3:
                    value = emperorPVo.seal;
                    break;
                default:
                    return;
            }
            if(value < item.getValue())return;
        }
        if(acheivePVo.level == 0){
            emperorModel.achieveMap.put(acheivePVo.achieveID,acheivePVo);
        }
        acheivePVo.level++;
        emperorModel.saveSqlData();
        new EmperorAchieveRspd(client,emperorModel.achieveMap.values());
        user.updateZDL();
        EmperorAttributePVo emperorAttributePVo = emperorModel.attributeMap.get((byte)emperorAchieveBaseVo.emperor);
        if(emperorAttributePVo != null){
            new EmperorAttributeRspd(client,emperorAttributePVo);
        }
    }
}
