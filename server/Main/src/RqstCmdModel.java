import arena.*;
import base.*;
import comm.BaseRqstCmd;
import dragon.*;
import elit.EliteBuyCountCmd;
import emperor.*;
import endless.wormNest.GetSweepAwardCmd;
import endless.wormNest.WormNestOperateCmd;
import friend.*;
import gang.guess.GuessCmd;
import gang.guess.GuessDoubleCmd;
import gang.guess.GuessInfoCmd;
import gang.commands.*;
import gang.gangBuild.*;
import gang.divination.DivinationCmd;
import gang.divination.GetGangGiftBoxCmd;
import gang.divination.GiftLogCmd;
import gang.divination.OpenGangBoxCmd;
import gang.map.GangMapCmd;
import gang.map.GangMapInfoCmd;
import gang.map.GetMapAwardCmd;
import gang.map.WishCmd;
import gang.talk.GangTalkCmd;
import gang.talk.GetGangTalkListCmd;
import giftCode.GiftCodeCmd;
import gluffy.comm.BaseRqst;
import gluffy.comm.RqstPool;
import heroTag.HeroTagActiveCmd;
import heroTag.HeroTagFlushCmd;
import heroTag.HeroTagOnCmd;
import illustrated.AglowIllustratedCmd;
import illustrated.IllustratedCmd;
import mail.MailListCmd;
import mail.PickAllAnnexCmd;
import mail.PickAnnexCmd;
import mail.ReadMailCmd;
import mine.*;
import mission.DailyMissionFlushCmd;
import mission.LimitTimeStatusCmd;
import mission.MissionCmpActCmd;
import mission.MissionCompleteCmd;
import prop.*;
import rank.RankCmd;
import realm.*;
import recharge.RechargeCmd;
import redness.*;
import sendMsg.GetClosedTestGiftCmd;
import sendMsg.SendIdentifyCodeCmd;
import sign.SignCheckInCmd;
import sign.SignMonRewardCmd;
import sign.SignWeekRewardCmd;
import skin.BuyInSkinShopCmd;
import skin.SkinAdvanceCmd;
import skin.SkinListCmd;
import skin.SkinUpCmd;
import snatch.SnatchFightCmd;
import snatch.SnatchGetMainCmd;
import snatch.SnatchGetTargetCmd;
import talk.GetTalkBackCmd;
import talk.TalkCmd;
import talk.TalkLikeCmd;
import wing.*;

/**
 * Created by jackie on 14-4-16.
 */
public class RqstCmdModel {
  public  static   Class<?>[] cmdClassList=new Class<?>[]{LoginCmd.class,
            RoleCreateCmd.class,
            LoginRoleCmd.class,
            GoMapCmd.class,
            GetLootCmd.class,
            UseBagPropCmd.class,
             SellPropCmd.class,
            SkillLevelUpCmd.class,
            IntensifyCmd.class,
           RankCmd.class,
          TalkCmd.class,
          BuyInShopCmd.class,
          EnchantCmd.class,
           GetTalkBackCmd.class,
          TypeTestCmd.class,

          MissionCompleteCmd.class,
          DailyMissionFlushCmd.class,
          GoTowerCmd.class,
          MissionCmpActCmd.class,
           TalentUpCmd.class,
          TalkLikeCmd.class,
           SignCheckInCmd.class,
          SignMonRewardCmd.class,
          SignWeekRewardCmd.class,
          DragonEquipPropCmd.class,
          DragonLevelUpCmd.class,
          DragonStoneUpCmd.class,
          DragonSkillUpCmd.class,
          DragonTouchCmd.class,
          DragonSelectCmd.class,
          ArenaGetMainCmd.class,
          ArenaGetTargetCmd.class,
          ArenaFightStartCmd.class,
          ArenaRecordCmd.class,
          ArenaFightEndCmd.class,
          UserChangePortraitCmd.class,
          UserChangeNameCmd.class,
          OtherPlayerCmd.class,
          RefineCmd.class,
          RefineReplaceCmd.class,
          MapAttackCmd.class,
          DissolveEquipCmd.class,
          InheritCmd.class,
          ComposeCmd.class,
          SkinChangeCmd.class,
          SnatchGetMainCmd.class,
          SnatchGetTargetCmd.class,
          SnatchFightCmd.class,
          ComposeClipsCmd.class,
          ClearCDCmd.class,
          FusionEquipCmd.class,
          BuyAndUsePropCmd.class,
          ArenaDailyRewardCmd.class,
          UnlockBagCellCmd.class,
          GangRankListCmd.class,
          CreateGangCmd.class,
          ApplyJoinGangCmd.class,
          GangInfoCmd.class,
          GetGangAwardCmd.class,
          KickOutGangCmd.class,
          QuitGangCmd.class,
          IsJoinGangCmd.class,
          ChangeNoticeCmd.class,
          GangTalkCmd.class,
          GetGangTalkListCmd.class,
          WormNestOperateCmd.class,
          GetSweepAwardCmd.class,

 
          MailListCmd.class,
          PickAnnexCmd.class,
          PickAllAnnexCmd.class,
          ReadMailCmd.class,
          RandomPropShopCmd.class,
          BuyInRandomPropShopCmd.class,
          IllustratedCmd.class,
          AglowIllustratedCmd.class,
          SendIdentifyCodeCmd.class,
          GetClosedTestGiftCmd.class,
          SkinListCmd.class,
          BuyInSkinShopCmd.class,
          CountersignCmd.class,
          SaveGuideCmd.class,
          GetAllMissionCmd.class,
          MyGangSkillUpCmd.class,
          GangMemberListCmd.class,
          GangSearchCmd.class,
          GangDetailCmd.class,
          QuickJoinGangCmd.class,
          ContributeCmd.class,
          GangBuildUpCmd.class,
          GangBuildLikeCmd.class,
          GangBuildInfoCmd.class,
          RelifeCmd.class,
          GetVipGiftCmd.class,
          BuyVipGiftCmd.class,
          GangOfficeChangeCmd.class,
          NormalResetCmd.class,
          ZdlLimitCmd.class,
          AdoptCmd.class,
          RejectCmd.class,
          AllRejectCmd.class,
          AuditingListCmd.class,
          GangLogCmd.class,
          GangUserEggListCmd.class,
          StartHatchCmd.class,
          ClearHatchCDCmd.class,
          HarvestEggCmd.class,
          GetTiLiCmd.class,
          RockMoneyTreeCmd.class,
          EquipAdvanceCmd.class,
          RechargeCmd.class,

          RP_RoomCreateCmd.class,
          RP_RoomListCmd.class,
          RP_RoomJoinCmd.class,
          RP_RoomExitCmd.class,
          RP_RecommendListCmd.class,
          RP_GangmenberListCmd.class,
          RP_InviteCmd.class,
          RP_RoomGameReadyCmd.class,
          DragonTalentOnCmd.class,
          ActivateDragonAchieveCmd.class,
          SaveChannelCmd.class,
          RP_QuickJoinCmd.class,
          AttentionListCmd.class,
          AttentionCmd.class,
          CloseGameCmd.class,
          SaveYunwaIDCmd.class,
          SystemInfoListCmd.class,
          SkinUpCmd.class,
          SkinAdvanceCmd.class,
          DivinationCmd.class,
          OpenGangBoxCmd.class,
          GetGangGiftBoxCmd.class,
          GiftLogCmd.class,
          ChangeMainCmd.class,
          UserUpdateCmd.class,
          GuessCmd.class,
          GuessDoubleCmd.class,
          GuessInfoCmd.class,
          SpecialFightResultCmd.class,
          EliteBuyCountCmd.class,
          GangMapInfoCmd.class,
          WingListCmd.class,
          WingCoreUpCmd.class,
          WingOnCmd.class,
          PickMoneyCmd.class,
          GiveTiliCmd.class,
          ReceiveTiliCmd.class,
          MyGiftCmd.class,
          GangMapCmd.class,
          GetMapAwardCmd.class,
          WishCmd.class,
          LimitTimeStatusCmd.class,
          GiftCodeCmd.class,
          GetDragonInOpenCmd.class,
          MyGangSkillListCmd.class,
          OpenEquipBagCellCmd.class,
          ComposePropCmd.class,
          DragonAdvanceCmd.class,
          EquipPurifyCmd.class,
          MR_ExploitCmd.class,
          MR_SearchPitCmd.class,
          EquipAttrReplaceCmd.class,
          MR_GetBagAwardCmd.class,
          MR_LootStartCmd.class,
          MR_GetLootTargetCmd.class,
          MR_LootEndCmd.class,
          MR_RecruitCmd.class,
          MR_RecruitListCmd.class,
          MR_PublicMineCmd.class,
          MR_PublicMineRobotCmd.class,
          MR_RankCmd.class,
          WingCoreAdvanceCmd.class,
          WingUnlockCmd.class,
          WingCoreUnlockCmd.class,
          LotteryCmd.class,
          HeroTagOnCmd.class,
          HeroTagActiveCmd.class,
          HeroTagFlushCmd.class,
          DragonQualityCmd.class,
          CommCostCmd.class,
          EmperorAwakenCmd.class,
          EmperorLevelCmd.class,
          EmperorLevelUpCmd.class,
          EmperorSealUpCmd.class,
          EmperorAdvanceCmd.class,
          EmperorAdvanceOnCmd.class,
          DanGasCmd.class,
          DanSoulCmd.class,
          DanUpCmd.class,
          CycloneOpenCmd.class,
          CycloneUpCmd.class,
          EmperorEquipOnCmd.class,
          EmperorEquipUpCmd.class,
          EmperorEquipAllUpCmd.class,
          EmperorEquipOffCmd.class,
          EmperorEquipIndexUpCmd.class,
          EmperorSkillUpCmd.class,
          EmperorAchieveActiveCmd.class
  };

    public static void init() throws ClassNotFoundException {
        for (Class<?> classCmd :RqstCmdModel.cmdClassList) {

            Class<?> classRqst=Class.forName("protocol."+classCmd.getSimpleName().substring(0,classCmd.getSimpleName().length()-3)+"Rqst");
            RqstPool.regCmdClass((Class<? extends BaseRqst>) classRqst, (Class<? extends BaseRqstCmd>) classCmd);

        }
    }
}
