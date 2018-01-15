package talk;

import comm.*;
import gluffy.comm.AbsClient;
import gluffy.comm.BaseRqst;
import protocol.*;
import table.ModuleUIEnum;
import table.TalkEnum;

import java.util.ArrayList;

import static talk.TalkModel.checkIsDead;
import static talk.TalkModel.resetNewMsgItem;


public class GetTalkBackCmd extends BaseRqstCmd {

	@Override
	public void execute(Client client,User user, BaseRqst baseRqst) {
        GetTalkBackRqst rqst= (GetTalkBackRqst) baseRqst;
        if(rqst.type == TalkEnum.UPDATE){//世界树和邮件消息更新

        }
        getTalkBack(client,user,rqst,false);
    }

    public void getTalkBack(Client client,User user, GetTalkBackRqst rqst,boolean isNewMsg) {
        if(!user.functionSet.contains(ModuleUIEnum.WORLD_TREE))return;
//        TalkModel.talkModel();
        ArrayList<TalkMsgPVo> list=new ArrayList<>();
        ArrayList<Long> redpacketOpen=new ArrayList<>();
        int pageSize=10;
//        for (int i = 0; i < pageSize; i++) {
//            int index=rqst.page*pageSize+i;
//            if(TalkModel.rankLikeList.size()<=index)break;
//            list.add(TalkModel.rankLikeList.get(index).vo);
//        }
        int size = TalkModel.talkMsgList.size();
if(rqst.page<0||rqst.page>size/pageSize)return;
//            for (int i = 0; i < pageSize; i++) {
//                int  index=-rqst.page*pageSize-i+TalkModel.timeListIndex-1;
//                index%=TalkModel.timeList.length;
//                index+=TalkModel.timeList.length;
//                TalkMsgPVo vo= TalkModel.timeList[index%TalkModel.timeList.length];
//                if(vo==null)break;
//              list.add(vo);
//            }
if(rqst.type<0) {
    for (int i = 0; i < pageSize; i++) {
        int index = -rqst.page * pageSize - i + size - 1;
        if (index < 0) break;
        TalkMsgPVo vo = TalkModel.talkMsgList.get(index);
        if (vo == null) break;
        if(checkIsDead(vo))continue;
        if (vo.type == TalkEnum.REDPACKET) {
            if (TalkModel.redPacketMap.get(vo.ID).allGet.containsKey(user.guid)) {
                redpacketOpen.add(vo.ID);
            }
        }else{
            if(TalkModel.userLikesMap.containsKey(vo.ID)){
                if(TalkModel.userLikesMap.get(vo.ID).contains(user.guid)){
                    vo.isLike = 1;
                }
            }
        }
        list.add(vo);
    }
}else {
    int startCount=0;
    int typeCount=0;
    for (int i = 0; i < TalkModel.LIMIT_COUNT; i++) {
        int index = - i + size - 1;
        if (index < 0) break;
        TalkMsgPVo vo = TalkModel.talkMsgList.get(index);

        if (vo == null) break;
        if(checkIsDead(vo))continue;
        if(rqst.type == TalkEnum.GANG&&vo.classType ==TalkEnum.GANG){
            if(startCount<rqst.page * pageSize) {
                startCount++;
            }else if(user.cacheUserVo.gang.gangVo == null){
                if(vo.type == TalkEnum.GANG){
                    list.add(vo);
                    if (TalkModel.userLikesMap.containsKey(vo.ID)) {
                        if (TalkModel.userLikesMap.get(vo.ID).contains(user.guid)) {
                            vo.isLike = 1;
                        }
                    }
                    if (list.size() >= pageSize) break;
                }
            }else{
                if(vo.gangID == user.cacheUserVo.gang.gangVo.gangID){
                    list.add(vo);
                    if (TalkModel.userLikesMap.containsKey(vo.ID)) {
                        if (TalkModel.userLikesMap.get(vo.ID).contains(user.guid)) {
                            vo.isLike = 1;
                        }
                    }
                    if (list.size() >= pageSize) break;
                }
            }
        }else if(rqst.type == TalkEnum.SYSTEM&&vo.classType == TalkEnum.OFFICIAL&&vo.type != TalkEnum.REDPACKET&&vo.type != TalkEnum.BOSS){
            if(startCount<rqst.page * pageSize) {
                startCount++;
            }else{
                list.add(vo);
                if(TalkModel.userLikesMap.containsKey(vo.ID)){
                    if(TalkModel.userLikesMap.get(vo.ID).contains(user.guid)){
                        vo.isLike = 1;
                    }
                }
                if(list.size()>=pageSize)break;
            }
        }else if(vo.type==rqst.type){
            if(startCount<rqst.page * pageSize) {
                startCount++;
            }else{
                list.add(vo);
                if (vo.type == TalkEnum.REDPACKET) {
                    if (TalkModel.redPacketMap.get(vo.ID).allGet.containsKey(user.guid)) {
                        redpacketOpen.add(vo.ID);
                    }
                }else{
                    if(TalkModel.userLikesMap.containsKey(vo.ID)){
                        if(TalkModel.userLikesMap.get(vo.ID).contains(user.guid)){
                            vo.isLike = 1;
                        }
                    }
                }
                if(list.size()>=pageSize)break;
             }


        }

    }
}
        int newSize = TalkModel.talkMsgList.size();
        for(TalkMsgPVo talkMsgPVo : TalkModel.talkMsgList){
            if(talkMsgPVo.classType == TalkEnum.OFFICIAL)continue;
            CacheUserVo cacheUserVo = CacheUserVo.allMap.get(talkMsgPVo.roleID);
            if(cacheUserVo == null)continue;
            talkMsgPVo.roleName = cacheUserVo.name;
            talkMsgPVo.portrait = cacheUserVo.portrait;
            talkMsgPVo.level = cacheUserVo.level;
            talkMsgPVo.yunvaID = cacheUserVo.yunwaID;
        }
        if(newSize == 0){
            new GetTalkBackRspd(client,rqst.page,rqst.type, list,redpacketOpen,0,0,0);
            return;
        }
        long lastTalkId = TalkModel.talkMsgList.get(newSize-1).ID;
         if(list.size()>0) {
                 UserNewMsgItem item = user.userNewMsgItem;
                 if(rqst.type == TalkEnum.SYSTEM){
                     item.lastReadSystemMsg = lastTalkId;
                     item.newSystemMsgNum = 0;
                     item.lastOperateDay = TalkModel.lastOperateDay;
                     item.isChange = true;
                 }
                 if(rqst.type == TalkEnum.BOSS){
                     item.lastReadBossMsg = lastTalkId;
                     item.newBossMsgNum = 0;
                     item.lastOperateDay = TalkModel.lastOperateDay;
                     item.isChange = true;
                 }
                 if(rqst.type == TalkEnum.REDPACKET){
                     item.lastReadRedPackMsg = lastTalkId;
                     item.newRedPackMsgNum = 0;
                     item.lastOperateDay = TalkModel.lastOperateDay;
                     item.isChange = true;
                 }
                 resetNewMsgItem(user,lastTalkId);
                 new GetTalkBackRspd(client,rqst.page,rqst.type, list,redpacketOpen,item.newSystemMsgNum,item.newBossMsgNum,item.newRedPackMsgNum);

             if(isNewMsg){
                 if(rqst.type != TalkEnum.REDPACKET)return;
                 for(AbsClient absClient : Client.allOnline.values()){
                     Client oneClient=(Client)absClient;
//                 for(CacheUserVo cacheUserVo : CacheUserVo.allMap.values()){
//                     Client oneClient = Client.allOnline.get(cacheUserVo.passportVo.devID);
                     if(oneClient != null && oneClient.currentUser != null && oneClient.currentUser.guid != user.guid){
                         resetNewMsgItem(oneClient.currentUser,lastTalkId);
                     }
                 }
             }
        }else{
             resetNewMsgItem(user,lastTalkId);
             new GetTalkBackRspd(client,rqst.page,rqst.type, list,redpacketOpen,user.userNewMsgItem.newSystemMsgNum,user.userNewMsgItem.newBossMsgNum,user.userNewMsgItem.newRedPackMsgNum);
         }
	}

    public void addSystemTalk(){
        int newSize = TalkModel.talkMsgList.size();
        if(newSize == 0){
            return;
        }
        long lastTalkId = TalkModel.talkMsgList.get(newSize-1).ID;
        for(AbsClient absClient : Client.allOnline.values()){
            Client client=(Client)absClient;
//        for(CacheUserVo cacheUserVo : CacheUserVo.allMap.values()){
//            Client client = Client.allOnline.get(cacheUserVo.passportVo.devID);
            if(client != null && client.currentUser != null){
                resetNewMsgItem(client.currentUser,lastTalkId);
            }
        }
    }
}
