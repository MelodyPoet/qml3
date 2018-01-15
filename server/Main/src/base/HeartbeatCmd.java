//被不帅注释 因为udp 通信 服务端可 主动推送了 不需要走心跳，新增一个 OpenUIRqst 同样带UIID和childID来做这些处理
//package base;
//
//import comm.*;
//import dragon.DragonEggModel;
//import dragon.DragonEggVo;
//import friend.FriendModel;
//import gang.GangVo;
//import gang.guess.GuessModel;
//import gang.commands.IsJoinGangCmd;
//import gang.map.GangMapCmd;
//import gluffy.comm.BaseRqst;
//import gluffy.utils.JkTools;
//import mine.MineModel;
//import protocol.*;
//import redness.RP_RoomListCmd;
//import redness.RP_RoomVo;
//import sqlCmd.AllSql;
//import table.ModuleUIEnum;
//import table.TalkEnum;
//
//
//import java.util.ArrayList;
//
///**
// * Created by admin on 2017/3/21.
// */
//public class HeartbeatCmd extends BaseRqstCmd{
//    @Override
//    public void execute(Client client, User user, BaseRqst baseRqst) {
//        HeartbeatRqst rqst = (HeartbeatRqst)baseRqst;
////        System.out.println("=================HeartbeatCmd================="+user.guid+"==========uiID======="+rqst.uiID);
//        if(user == null){
//            return;
//        }
//        switch (rqst.uiID){
//
//            case -1://战斗
//                break;
//            case 0://主程
//
//                if(user.functionSet.contains(ModuleUIEnum.MAIL_VIEW)) {
//                    if(user.mailModel.addMailList.size() > 0){
//                        new MailListRspd(client, (short) user.mailModel.unReadMailMap.size(), user.mailModel.addMailList);
////                        System.out.println("======"+user.mailModel.unReadMailMap.size()+"==="+user.mailModel.addMailList.size());
//                        user.mailModel.addMailList.clear();
//                    }
//                }
//                if(user.functionSet.contains(ModuleUIEnum.WORLD_TREE)) {
//                    ArrayList<TalkMsgPVo> talkList = new ArrayList<>();
//                    ArrayList<Long> redpacketOpen = new ArrayList<>();
//                    if(user.userNewMsgItem.newSystemMsgNum > 0 || user.userNewMsgItem.newBossMsgNum > 0 || user.userNewMsgItem.newRedPackMsgNum > 0){
//                        new GetTalkBackRspd(client, (byte) 0, TalkEnum.UPDATE, talkList, redpacketOpen, user.userNewMsgItem.newSystemMsgNum, user.userNewMsgItem.newBossMsgNum, user.userNewMsgItem.newRedPackMsgNum);
//                    }
////                    System.out.println("====="+user.userNewMsgItem.newRedPackMsgNum);
//                }
//                changGangStatus(user,client);
//                if(user.airingModel.airingList.size()>0){
//                    new AiringListRspd(client,user.airingModel.airingList);
//                    user.airingModel.airingList.clear();
//                }
//                if(user.rednessModel.inviteMeList.size()>0){
//                    new RP_InviteMeRspd(client,user.rednessModel.inviteMeList);
//                    user.rednessModel.inviteMeList.clear();
//                }
//                if(user.functionSet.contains(ModuleUIEnum.MY_FRIEND)) {
//                    FriendModel friendModel = user.friendModel;
//                    int newAttentionCount = FriendModel.getAttentionMe(user.guid).size() - friendModel.lastAttentionCount;
//                    if(newAttentionCount > 0 && newAttentionCount != friendModel.lastNewAttentionCount){
//                        friendModel.lastNewAttentionCount = newAttentionCount;
//                        new NewInfoRspd(client,(byte)1,newAttentionCount);
//                    }
//                    int newSysInfoCount = user.cacheUserVo.newSysInfoCount;
//                    if(newSysInfoCount > 0 && newSysInfoCount != friendModel.lastNewSysInfoCount){
//                        friendModel.lastNewSysInfoCount = newSysInfoCount;
//                        new NewInfoRspd(client,(byte)2,newSysInfoCount);
//                    }
//                    int newGiftCount = user.cacheUserVo.friendCacheModel.addGiftList.size();
//                    if(newGiftCount > 0 && newGiftCount != friendModel.lastNewGiftCount){
//                        friendModel.lastNewGiftCount = newSysInfoCount;
//                        new NewInfoRspd(client,(byte)3,newGiftCount);
//                    }
//                }
//                ArrayList<HeroTagPVo> list = user.cacheUserVo.addHeroTagList;
//                if(list.size() > 0){
//                    for(HeroTagPVo heroTagPVo : list){
//                        new HeroTagActiveRspd(client,(byte) 1,heroTagPVo);
//                    }
//                    list.clear();
//                }
//                break;
//                case ModuleUIEnum.DRAGON:
//                    if(user.functionSet.contains(ModuleUIEnum.DRAGON)){
//                        DragonEggModel myEggModel = user.cacheUserVo.dragonEggModel;
//                            if(myEggModel.theirsEggUpSet.size() > 0){
//                                ArrayList<GangUserEggUpPVo> upInfo = new ArrayList<>();
//                                for(Long userID : myEggModel.theirsEggUpSet){
//                                    if(!CacheUserVo.allMap.containsKey(userID))continue;
//                                    CacheUserVo cacheUserVo = CacheUserVo.allMap.get(userID);
//                                    GangUserEggUpPVo pVo = new GangUserEggUpPVo();
//                                    pVo.userID = userID;
//                                    pVo.eggList = user.cacheUserVo.dragonEggModel.getEggPVoList(cacheUserVo);
//                                    upInfo.add(pVo);
//                                }
//                                myEggModel.theirsEggUpSet.clear();
//                                new GangUserEggListUpRspd(client,upInfo);
//                                System.out.println("=====UP_GANG_USER_EGG===="+user.guid+"======="+upInfo.size());
//                            }
//                        if(myEggModel.myEggUpSet.size() > 0){
//                            for(Byte tempID : myEggModel.myEggUpSet){
//                                DragonEggVo dragonEggVo = myEggModel.getEggByTempID(tempID);
//                                if (dragonEggVo == null)return;
//                                new DragonEggTouchUpRspd(client,tempID,dragonEggVo.DragonEggVoToPVo());
//                                System.out.println("=====TOUCH_EGG===="+tempID+"======="+dragonEggVo.count);
//                            }
//                            myEggModel.myEggUpSet.clear();
//                        }
//                    }
//                    break;
//                case ModuleUIEnum.REDNESS_PALACE:
//                    user.rednessModel.checkForRobot();
//                    if(user.rednessModel.inviteMeList.size()>0){
//                        new RP_InviteMeRspd(client,user.rednessModel.inviteMeList);
//                        user.rednessModel.inviteMeList.clear();
//                    }
//                    if(rqst.childID == 1){
//                        new RP_RoomListCmd().execute(client,user,null);
//                    }
//                    if(user.rednessModel.startGameTime > 0 && --user.rednessModel.startGameTime == 0){
//                        RP_RoomVo roomVo = user.rednessModel.myRoom;
//                        if(roomVo != null && roomVo.guestReady == true){
//                            new RP_RoomGameStartRspd(client);
//                        }
//                        user.rednessModel.startGameTime = 0;
//                    }
//                    break;
//            case ModuleUIEnum.WORLD_BOSS:
//                int myAttackPoint=-1,myRank=-1;
////                if(user.cacheUserVo.rankUserWorldBossVo!=null){
////                    myAttackPoint=user.cacheUserVo.rankUserWorldBossVo.orderScore();
////                    myRank=user.cacheUserVo.rankUserWorldBossVo.orderIndex;
////                }
//             //   new WB_RankListRspd(client, WorldBossModel.cachedTopUsers20,myAttackPoint,myRank);
//                break;
//            case ModuleUIEnum.UNION_VIEW:
//                changGangStatus(user,client);
//                if(rqst.childID == 1){
//                    GangVo gangVo = user.cacheUserVo.gang.gangVo;
//                    if(gangVo == null)break;
//                    for(GangGuessLogPVo gangGuessLogPVo : user.cacheUserVo.addGuessLogList){
//                        new AddGuessLogRspd(client,gangGuessLogPVo);
//                        if(gangGuessLogPVo.type == 1){
//                            GuessModel guessModel = gangVo.guessModel;
//                            new GuessInfoRspd(client,guessModel.guessMin,guessModel.guessMax,guessModel.guessDouble,guessModel.guessTimes,guessModel.logList,guessModel.lastWinner,guessModel.isGetKey);
//                        }
//                    }
//                    user.cacheUserVo.addGuessLogList.clear();
//                    byte guessDouble = gangVo.guessModel.guessDouble;
//                    if(user.cacheUserVo.guessDouble != guessDouble){
//                        new GuessDoubleRspd(client,gangVo.guessModel.guessDouble,gangVo.guessModel.guessDouble);
//                        user.cacheUserVo.guessDouble = guessDouble;
//                    }
//                }
//                break;
//            case ModuleUIEnum.UNION_MAP:
//                new GangMapCmd().execute(client,user,new GangMapRqst());
//                break;
//            case ModuleUIEnum.MINERAL_RES_PUBLIC:
//                MineModel mineModel = user.cacheUserVo.mineModel;
//                MineModel.flushPublicMine(mineModel.myRoom);
//                mineModel.lastTime = JkTools.getGameServerTime(null);
//                new MR_PublicMineRspd(client,mineModel.myRoom.publicMap.values());
//                break;
//            case ModuleUIEnum.MINERAL_RES_PRIVATE:
//                MineModel myModel = user.cacheUserVo.mineModel;
//                if(myModel.sendLootMsg){
//                    myModel.sendLootMsg = false;
//                    new MR_LootedRspd(client,myModel.lostMineCount,myModel.lostStoneCount);
//                }
//                break;
//        }
//        }
//    public void changGangStatus(User user,Client client){
//        if(user.functionSet.contains(ModuleUIEnum.UNION_VIEW)) {
//            switch (user.cacheUserVo.gangStatus){
//                case 0:
//                    break;
//                case 1://审核通过
//                    user.cacheUserVo.gang.gangVo.createGangInfoRspd(client);
//                    user.gangSkillModel.init();
//                    user.updateZDL();
//                    new ServerTipRspd(client,(short)147,user.cacheUserVo.gang.gangVo.gangName);
//                    user.cacheUserVo.gangStatus = 0;
//                    AllSql.userSql.update(user.guid,AllSql.userSql.FIELD_GANG_STATUS,0);
//                    break;
//                case 2://踢出公会
//                    IsJoinGangCmd.isJoinGang(client, user, (byte) 2);
//                    user.gangSkillModel.clearGangSkill(false,user.cacheUserVo.gangName);
//                    user.cacheUserVo.gang.addTalkList.clear();
//                    user.cacheUserVo.gangStatus = 0;
//                    AllSql.userSql.update(user.guid,AllSql.userSql.FIELD_GANG_STATUS,0);
//                    user.cacheUserVo.gangName = "";
//                    break;
//                case 3://降为会员
//                case 4://升为长老
//                case 6://升为会长
//                    user.cacheUserVo.gang.gangVo.createGangInfoRspd(client);
//                    new GangOfficeChangeRspd(client,(byte)(user.cacheUserVo.gangStatus - 3),user.guid,user.guid);
//                    user.cacheUserVo.gangStatus = 0;
//                    AllSql.userSql.update(user.guid,AllSql.userSql.FIELD_GANG_STATUS,0);
//                    break;
//            }
//            if(user.cacheUserVo.gang.gangVo != null){
//                GangVo gangVo = user.cacheUserVo.gang.gangVo;
//                if(!user.cacheUserVo.gang.isMember(user.guid))
//                    new AuditingUpdateRspd(client,(byte) gangVo.gangApplyList.size());
//            }
//        }
//    }
//
//}
