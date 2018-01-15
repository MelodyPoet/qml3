package airing;

import base.UserActState;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.AbsClient;
import gluffy.utils.JkTools;
import protocol.AiringMessagePVo;
import table.AiringBaseVo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by admin on 2016/8/26.
 */
public class AiringModel {
    public static ArrayList<AiringMessagePVo> sysAiringList = new ArrayList<>();
    public ArrayList<AiringMessagePVo> airingList = new ArrayList<>();
    public User user;

    public AiringModel(User user) {
        this.user = user;
    }

    public static void broadcast(ArrayList<AiringMessagePVo> list){
        for(AbsClient absClient : Client.allOnline.values()){
            Client client=(Client)absClient;
            User user = client.currentUser;
            if(client != null && user != null){
                int size = list.size();
                ArrayList<AiringMessagePVo> airingList = client.currentUser.airingModel.airingList;
                if(size <= 5){
                    if(size+airingList.size() > 5){
                        airingList.clear();
                    }
                    airingList.addAll(list);
                }
            }
        }
    }

    public static void broadcast(AiringMessagePVo pVo){
        for(AbsClient absClient : Client.allOnline.values()){
            Client client=(Client)absClient;
            User user = client.currentUser;
            if(client != null && user != null){
                if(user.actState != UserActState.CAMP)continue;
                if(user.airingModel.airingList.size() > 5){
                    user.airingModel.airingList.clear();
                }
                user.airingModel.airingList.add(pVo);
            }
        }
    }

    public static void flushAiringList(){
        Iterator it = AiringModel.sysAiringList.iterator();
        while (it.hasNext()){
            AiringMessagePVo pVo = (AiringMessagePVo) it.next();
            int time = JkTools.getGameServerTime(null) - pVo.deadTime;
            if(time > 0){
                it.remove();
            }else{
                if(pVo.time != -1){
                    pVo.time = (-time/(pVo.space*60) + 1);
                }
            }
        }
    }

    public static void addSystemAiring(int id){
        AiringBaseVo vo = Model.AiringBaseMap.get(id);
        if(vo == null)return;
        AiringMessagePVo pVo = new AiringMessagePVo();
        pVo.type = 0;
        pVo.msg = vo.msg;
        pVo.time = vo.times;
        pVo.space = vo.space;
        if(pVo.time == -1){
            pVo.deadTime = JkTools.getRundayTime(null)+Model.ONE_DAY_TIME;
        }else{
            pVo.deadTime = JkTools.getGameServerTime(null) + pVo.space * 60 *(pVo.time - 1);
        }
        sysAiringList.add(pVo);
        broadcast(pVo);
    }
}
