package sqlCmd;

import comm.CachePassportVo;
import comm.CacheUserVo;
import gluffy.utils.CpuDebuger;

public class AllSql {
    public static PassportSql passportSql;
public static UserSql userSql;
public static PropSql propSql;
    public static GangSql gangSql;
    public static GangMemberSql gangMemberSql;
    public static MailSql mailSql;
    public static WorldTreeSql worldTreeSql;
    public static FriendSql friendSql;
    public static FriendLogSql friendLogSql;
    public static FriendGiftSql friendGiftSql;
    public static GameSetSql gameSetSql;
    public static MineLootSql mineLootSql;
    public static MineRecruitSql mineRecruitSql;
    public static HeroTagSql heroTagSql;
    public static PropLogSql propLogSql;

    public  static  void init(){
        passportSql=new PassportSql();
        if(CachePassportVo.guidMap.size() < 10){
            AllSql.passportSql.isInitDataBase();
        }
        CpuDebuger.print("PassportSql", 0);
        userSql=new UserSql();
        if(CacheUserVo.allMap.size() == 0){
            AllSql.userSql.isInitDataBase();
        }
        CpuDebuger.print("UserSql",0);
        propSql=new PropSql();
//        if(isAddProp){
//            AllSql.propSql.isInitDataBase();
//        }
        CpuDebuger.print("PropSql",0);
        gangSql = new GangSql();
        CpuDebuger.print("GangSql",0);
        gangMemberSql = new GangMemberSql();
        CpuDebuger.print("GangMemberSql",0);
        mailSql = new MailSql();
        CpuDebuger.print("MailSql",0);
        worldTreeSql = new WorldTreeSql();
        CpuDebuger.print("WorldTreeSql",0);
        friendSql = new FriendSql();
        CpuDebuger.print("FriendSql",0);
        friendLogSql = new FriendLogSql();
        CpuDebuger.print("FriendLogSql",0);
        friendGiftSql = new FriendGiftSql();
        CpuDebuger.print("FriendGiftSql",0);
        gameSetSql = new GameSetSql();
        CpuDebuger.print("gameSetSql",0);
        mineLootSql = new MineLootSql();
        CpuDebuger.print("mineLootSql",0);
        mineRecruitSql = new MineRecruitSql();
        CpuDebuger.print("mineRecruitSql",0);
        heroTagSql = new HeroTagSql();
        CpuDebuger.print("heroTagSql",0);
        propLogSql = new PropLogSql();
        CpuDebuger.print("propLogSql",0);
    }
}
