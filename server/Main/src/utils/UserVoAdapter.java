package utils;

import comm.CacheUserVo;
import comm.Model;
import friend.FriendGiftVo;
import friend.FriendModel;
import gluffy.utils.JkTools;
import protocol.*;
import table.PropTypeEnum;
import table.SkillBaseVo;

import java.util.ArrayList;

/**
 * Created by jackie on 16-3-23.
 */
public class UserVoAdapter {
    public  static SimpleUserPVo toSimpleUserPVo(CacheUserVo user){
        SimpleUserPVo pVo=new SimpleUserPVo();
        pVo.tempID=user.guid;
        pVo.vip=user.passportVo.vip;
        pVo.name=user.name;
        pVo.level=user.level;
        pVo.zdl=user.zdl;
        pVo.portrait= user.portrait;
        pVo.guid = user.guid;

        return pVo;
    }
    public  static RankUserPVo toRankUserPVo(CacheUserVo user){
        RankUserPVo pVo=new RankUserPVo();
        pVo.zdl=user.zdl;
        pVo.vip=user.passportVo.vip;
        pVo.portrait= user.portrait;
        pVo.tempID=user.guid;
        pVo.baseID=user.baseID;
        pVo.name=user.name;
        pVo.level=user.level;
        pVo.avata = new AvatarPVo();
        fillAvatar(user,pVo.avata);
        return pVo;
    }

    public  static SkillUserPVo toSkillUserPVo(CacheUserVo user){
        SkillUserPVo pVo=new SkillUserPVo();
        pVo.tempID=user.guid;
        pVo.baseID=user.baseID;
        pVo.zdl=user.zdl;
        pVo.name=user.name;
        pVo.level=user.level;
        pVo.equipSkills=user.skillModel.skills.values();
        pVo.weapon= user.equipItems[PropTypeEnum.MAIN_WEAPON];
        pVo.skin = user.skin;
//        if(udpClient!=null) {
//            pVo.dir = udpClient.dir;
//            pVo.posx = udpClient.posx;
//            pVo.posz = udpClient.posz;
//        }
        pVo.portrait = user.portrait;
        return pVo;
    }

    public  static OtherUserPVo toOtherUserPVo(CacheUserVo user,CacheUserVo me){
        OtherUserPVo pVo=new OtherUserPVo();
        pVo.userID = user.guid;
         pVo.baseID=user.baseID;
        pVo.vip = user.passportVo.vip;
         pVo.level=user.level;
        pVo.attentionMe = FriendModel.isAttentionMe(me.guid,user.guid);
        pVo.attribute = JkTools.intArrayAsList(user.attributeModel.attribute);
        pVo.arenaRank = user.rankArenaVo == null?-1:user.rankArenaVo.orderIndex;
        if(user.gang.gangVo == null){
            pVo.gangName = "";
        }else{
            pVo.gangName = user.gang.gangVo.gangName;
            pVo.office = user.gang.gangVo.users.get(user.guid).office;
        }
        pVo.equip = JkTools.asListTrimNull(user.equipItems);
        pVo.avata = new AvatarPVo();
        pVo.name = user.name;
        fillAvatar(user,pVo.avata);
        ArrayList<DragonPVo> dragonList = new ArrayList<>();
        for(DragonPVo dragonPVo : user.dragonCacheModel.dragonsMap.values()){
            dragonList.add(dragonPVo);
        }
        pVo.dragon=dragonList;
        pVo.talant=user.skillModel.talents.values();
        pVo.skin = user.skinModel.skinMap.values();
        pVo.hunshi = user.equipDragonStone.values();
        ArrayList<SkillPVo> list = new ArrayList<>();
        for(SkillPVo skillPVo : user.skillModel.skills.values()){
            SkillBaseVo skillBaseVo = Model.SkillBaseMap.get(skillPVo.baseID).get(0);
            if(skillBaseVo.type == 1)continue;
            list.add(skillPVo);
        }
        pVo.skill = list;
        return pVo;
    }

    public static CampUserPVo toCampUserPVo(CacheUserVo user) {
        CampUserPVo cvo = new CampUserPVo();
        cvo.name = user.name;
        cvo.baseID = user.baseID;
        cvo.tempID = user.guid;
cvo.level=user.level;
        cvo.avata = new AvatarPVo();
        cvo.dragonID = user.dragonCacheModel.getMainDragonID();
        cvo.yunvaID = user.yunwaID;
        cvo.heroTag = user.heroTagID;
        fillAvatar(user,cvo.avata);
        return cvo;
    }
    public static TalkRedPacketUserPVo toTalkRedPacketUserPVo(CacheUserVo user,int diamond) {
        TalkRedPacketUserPVo cvo = new TalkRedPacketUserPVo();
        cvo.userID = user.guid;
        cvo.roleName = user.name;
        cvo.portrait = user.portrait;
        cvo.diamond=diamond;
         return cvo;
    }
    public static void fillAvatar(CacheUserVo user,AvatarPVo avatarPVo) {
        avatarPVo.skinID=user.skin;
        avatarPVo.wingID=user.wingModel.equipWingID;
        PropPVo prop = user.equipItems[PropTypeEnum.MAIN_WEAPON];
        if(prop!=null){
            avatarPVo.weaponID=prop.baseID;
        }else   {
            avatarPVo.weaponID=0;
        }
    }
    public  static FriendUserPVo toFriendUserPVo(CacheUserVo user,CacheUserVo me){
        FriendUserPVo pVo=new FriendUserPVo();
        pVo.tempID=user.guid;
        pVo.vip=user.passportVo.vip;
        pVo.name=user.name;
        pVo.level=user.level;
        pVo.zdl=user.zdl;
        pVo.portrait= user.portrait;
        pVo.guid = user.guid;
        pVo.gangName = user.gang.gangVo == null ? "":user.gang.gangVo.gangName;
        pVo.fans = FriendModel.getAttentionMe(user.guid).size();
        pVo.yunwaID = user.yunwaID;
        pVo.together = FriendModel.isMyAttention(me.guid,user.guid) && FriendModel.isAttentionMe(me.guid,user.guid);
        return pVo;
    }

    public static FriendGiftPVo toFriendGiftPVo(FriendGiftVo friendGiftVo,CacheUserVo cacheUserVo,byte canReceiveCount){
        FriendGiftPVo friendGiftPVo = new FriendGiftPVo();
        friendGiftPVo.id = friendGiftVo.guid;
        friendGiftPVo.guid = cacheUserVo.guid;
        friendGiftPVo.time = friendGiftVo.createTime;
        friendGiftPVo.name = cacheUserVo.name;
        friendGiftPVo.vip = cacheUserVo.passportVo.vip;
        friendGiftPVo.portrait = cacheUserVo.portrait;
        friendGiftPVo.level = cacheUserVo.level;
        friendGiftPVo.isCanGet = friendGiftVo.isGet == 1 || canReceiveCount <= 0? (byte)0:(byte)1;
        return friendGiftPVo;
    }

    public static UserMinePVo toUserMinePVo(CacheUserVo userVo,byte index){
        UserMinePVo userMinePVo = new UserMinePVo();
        userMinePVo.index = index;
        userMinePVo.userID = userVo.guid;
        userMinePVo.level = userVo.level;
        userMinePVo.portrait = userVo.portrait;
        userMinePVo.name = userVo.name;
        userMinePVo.mineType = userVo.mineModel.mineType;
        userMinePVo.startTime = userVo.mineModel.startTime;
        userMinePVo.endTime = userVo.mineModel.endTime;
        userMinePVo.total = userVo.mineModel.total;
        userMinePVo.lost = userVo.mineModel.lostMineCount;
        userMinePVo.protectTime = userVo.mineModel.protectTime;
        return userMinePVo;
    }

    public static RecruitUserPVo toRecruitUserPVo(CacheUserVo userVo){
        RecruitUserPVo recruitUserPVo = new RecruitUserPVo();
        recruitUserPVo.userID = userVo.guid;
        recruitUserPVo.level = userVo.level;
        recruitUserPVo.name = userVo.name;
        recruitUserPVo.portrait = userVo.portrait;
        recruitUserPVo.zdl = userVo.zdl;
        return recruitUserPVo;
    }

    public static TeamUserPVo toTeamUserPVo(CacheUserVo userVo){
        return toTeamUserPVo(userVo,(byte)0);
    }

    public static TeamUserPVo toTeamUserPVo(CacheUserVo userVo,byte index){
        TeamUserPVo teamUserPVo = new TeamUserPVo();
        teamUserPVo.index = index;
        teamUserPVo.level = userVo.level;
        teamUserPVo.portrait = userVo.portrait;
        teamUserPVo.name = userVo.name;
        teamUserPVo.userID = userVo.guid;
        return teamUserPVo;
    }

    public  static MineRankPVo toMineRankPVo(CacheUserVo user){
        MineRankPVo pVo=new MineRankPVo();
        pVo.name=user.name;
        return pVo;
    }
}
