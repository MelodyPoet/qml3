package gang.commands;

import airing.AiringModel;
import comm.*;
import gameset.GameSetModel;
import gang.Gang;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.AiringMessagePVo;
import protocol.CreateGangRqst;
import protocol.ServerTipRspd;
import sqlCmd.AllSql;
import table.*;

import java.util.ArrayList;

/**
 * Created by admin on 2016/7/5.
 */
public class CreateGangCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        CreateGangRqst rqst = (CreateGangRqst) baseRqst;
//        createGang(client,user,rqst.gangName,rqst.portrait);
        createGang(client,user,rqst.gangName,rqst.portrait);
    }

    private void createGang(Client client,User user,String gangName,byte portrait){
           CacheUserVo cacheUserVo = user.cacheUserVo;
           if(cacheUserVo.gang.gangVo != null)return;
           int[] arr = Model.GameSetBaseMap.get(6).intArray;
           if(cacheUserVo.level<arr[0])return;
           if(checkGangName(client,gangName)){
               if(user.costUserDataAndProp(UserDataEnum.DIAMOND, arr[1], true, ReasonTypeEnum.CREATE_GANG) == false) return;
               GangVo gangVo = new GangVo();
               gangVo.gangName = gangName;
               gangVo.zdl = user.zdl;
               gangVo.level = 1;
               gangVo.portrait = portrait;
               ArrayList<GangbuildBaseVo> buildList =  Model.GangbuildBaseMap.get(1);
               if(buildList == null)return;
               GangbuildBaseVo vo = buildList.get(0);
               if(vo == null)return;
               gangVo.maxUserCount = vo.effect[0];
               gangVo.rank = Gang.allGangList.size();
               AllSql.gangSql.insertNew(gangVo);
               cacheUserVo.gang.gangVo = gangVo;
               Gang.addOne(gangVo);
               gangVo.init();
               cacheUserVo.gang.join(gangVo,user.cacheUserVo, GangOfficeEnum.MASTER,true);
               if(GameSetModel.checkTime(3)){
                   gangVo.guessModel.open(Model.GameSetBaseMap.get(38).intArray,gangVo.users.size());
               }

               //走马灯
               HonorAiringBaseVo airingVo = Model.HonorAiringBaseMap.get(11);
               if(airingVo==null)return;
               if(airingVo.isUse != 1)return;
               AiringMessagePVo pVo = new AiringMessagePVo();
               pVo.type = 1;
//               pVo.msg = "恭喜 "+client.passportVo.name+" 创建了 "+gangName+" 公会。";
               pVo.msg = airingVo.msg.replace("{1}",user.cacheUserVo.name).replace("{2}",gangName);
               pVo.time = 1;
               ArrayList<AiringMessagePVo> list = new ArrayList<>();
               list.add(pVo);
               AiringModel.broadcast(list);
           }
    }

    private boolean checkGangName(Client client,String gangName){
        if(gangName.length()<2||gangName.length()>8)return false;
        if(Gang.usedName.contains(gangName)){
            new ServerTipRspd(client,(short)56,null);
            return false;
        }
        return  true;
    }
}
