package   qmshared ;

import gluffy.comm.IBytes;
import gluffy.utils.JkTools;
import org.jdom2.Document;
import table.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.*;

public class Model  extends gluffy.utils.Model {
    public  static   String GateIp="localhost";
    public  static  int GatePort=19091;
    public  static  int MainPort =19092;
    public  static  int ScenePort =19093;




    public  static    int serverVs=106;

    public static Calendar openDay;
    public static String dataPath;
    public static int  userLevelMax;

    public static byte gangMaxLevel;
    public static boolean serverOpen;
    public static Map<Integer, MapBaseVo> MapBaseMap;
    public static Map<Integer, PropBaseVo> PropBaseMap;
    public static Map<Integer, NpcBaseVo> NpcBaseMap;
    public static Map<Integer, RoleBaseVo> RoleBaseMap;
    public static Map<Integer, RoleLevelBaseVo> RoleLevelBaseMap;
    public static Map<Integer, ArrayList<DropBaseVo>> DropBaseMap;
    public static Map<Integer, ArrayList<SkillBaseVo>> SkillBaseMap;
    public static Map<Integer, ShopBaseVo> ShopBaseMap;
    public static Map<Integer,EnchantBaseVo> EnchantBaseMap;
    public static Map<Integer,GameSetBaseVo> GameSetBaseMap;
    public static Map<Integer,SkillShopBaseVo> SkillShopBaseMap;
    public static Map<Integer, ArrayList<SkillDropBaseVo>> SkillDropBaseMap;
    public static Map<Integer,EnchantshopBaseVo> EnchantshopBaseMap;
    public static Map<Integer,MissionBaseVo> MissionBaseMap;
    public static Map<Integer, ArrayList<NpcLayoutBaseVo>> NpcLayoutBaseMap;
    public static Map<Integer, ArrayList<TowerBaseVo>> TowerBaseMap;
    public static Map<Integer, MapChapterBaseVo> MapChapterBaseMap;
    public static Map<Integer, ArrayList<TalentBaseVo>> TalentBaseMap;
    public static Map<Integer, SignBaseVo> SignBaseMap;
    public static Map<Integer, ArrayList<SignBoxBaseVo>> SignBoxBaseMap;
    public static Map<Integer, SignWeekBaseVo> SignWeekBaseMap;
    public static Map<Integer, DragonBaseVo> DragonBaseMap;
    public static Map<Integer,  DragonStoneUpBaseVo> DragonStoneUpBaseMap;
    public static Map<Integer, EquipRefineBaseVo> EquipRefineBaseMap;
    public static Map<Integer, CommZdlBaseVo> CommZdlBaseMap;
    public static Map<Integer, EquipDissolveBaseVo> EquipDissolveBaseMap;
    public static Map<Integer, EquipInheritBaseVo> EquipInheritBaseMap;
    public static Map<Integer, PropComposeBaseVo> PropComposeBaseMap;
    public static Map<Integer, ArenaRewardBaseVo>  ArenaRewardBaseMap ;
    public static Map<Integer, CommCooldownBaseVo> CommCooldownBaseMap;
    public static Map<Integer, AutoBuyBaseVo>  AutoBuyBaseMap;
    public static Map<Integer, NameBaseVo>  NameBaseMap;
    public static Map<Integer,  ArrayList<RobotBaseVo>>  RobotBaseMap;
    public static Map<Integer, RedPacketsBaseVo>  RedPacketsBaseMap ;
    public static Map<Integer, BagBaseVo> BagBaseMap;
    public static Map<Integer, CommAwardBaseVo> CommAwardBaseMap;
    public static Map<Integer,NpcLevelBaseVo> NpcLevelBaseMap;
    public static Map<Integer,FunctionOpenBaseVo> FunctionOpenBaseMap;
    public static Map<Integer,MailBaseVo> MailBaseMap;
    public static Map<Integer,ArrayList<PropShopBaseVo>> PropShopBaseMap;
    public static Map<Integer,WorldTreeBaseVo> WorldTreeBaseMap;
    public static Map<Integer,ArrayList<MonsterIllustratedBaseVo>> MonsterIllustratedBaseMap;
    public static Map<Integer,SkinBaseVo> SkinBaseMap;
    public static Map<Integer,HonorAiringBaseVo> HonorAiringBaseMap;
    public static Map<Integer,MessageBaseVo> MessageBaseMap;
    public static Map<Integer,ArrayList<TelphoneBaseVo>> TelphoneBaseMap;
    public static Map<Integer,ArrayList<GangSkillBaseVo>> GangSkillBaseMap;
    public static Map<Integer,ArrayList<GangbuildBaseVo>> GangbuildBaseMap;
    public static Map<Integer,GangContributeBaseVo> GangContributeBaseMap;
    public static Map<Integer,VipBaseVo> VipBaseMap;
    public static Map<Integer,ArrayList<CommCostBaseVo>> CommCostBaseMap;
    public static Map<Integer,DragonEggBaseVo> DragonEggBaseMap;
    public static Map<Integer,ArrayList<ActivationBaseVo>> ActivationBaseMap;
    public static Map<Integer,ArrayList<CommUnlockBaseVo>> CommUnlockBaseMap;
    public static Map<Integer,ShieldBaseVo> ShieldBaseMap;
    public static Map<Integer, EquipIntensifyUpBaseVo> EquipIntensifyUpBaseMap;
    public static Map<Integer, EquipIntensifyExpBaseVo> EquipIntensifyExpBaseMap;
    public static Map<Integer, EquipAdvanceBaseVo> EquipAdvanceBaseMap;
    public static Map<Integer, AiringBaseVo> AiringBaseMap;
    public static Map<Integer,GMBaseVo> GMBaseMap;
    public static Map<Integer,DragonAchieveBaseVo> DragonAchieveBaseMap;
    public static Map<Integer,ArrayList<SkinUpBaseVo>> SkinUpBaseMap;
    public static Map<Integer,ArrayList<SkinAdvanceBaseVo>> SkinAdvanceBaseMap;
    public static Map<Integer,GangBoxBaseVo> GangBoxBaseMap;
    public static Map<Integer,SystemUpdateBaseVo> SystemUpdateBaseMap;
    public static Map<Integer,UserUpdateBaseVo> UserUpdateBaseMap;
    public static Map<Integer,ArrayList<RangeConvertBaseVo>> RangeConvertBaseMap;
    public static Map<Integer,GangMapBaseVo> GangMapBaseMap;
    public static Map<Integer,WingBaseVo> WingBaseMap;
    public static Map<Integer,ArrayList<WingCoreUpBaseVo>> WingCoreUpBaseMap;
    public static Map<Integer,GangDragonUpBaseVo> GangDragonUpBaseMap;
    public static Map<Integer,GangDragonWishBaseVo> GangDragonWishBaseMap;
    public static Map<Integer,LimitTimeActivationBaseVo> LimitTimeActivationBaseMap;
    public static Map<Integer,GiftBaseVo> GiftBaseMap;
    public static Map<Integer,DragonLevelBaseVo> DragonLevelBaseMap;
    public static Map<Integer,ArrayList<DragonAdvanceBaseVo>> DragonAdvanceBaseMap;
    public static Map<Integer,NpcAttributeBaseVo> NpcAttributeBaseMap;
    public static Map<Integer,NpcAttributePowerBaseVo> NpcAttributePowerBaseMap;
    public static Map<Integer,EquipPurifyBaseVo> EquipPurifyBaseMap;
    public static Map<Integer,PropSuitBaseVo> PropSuitBaseMap;
    public static Map<Integer,MineralResBaseVo> MineralResBaseMap;
    public static Map<Integer,ArrayList<CommRewardPropBaseVo>> CommRewardPropBaseMap;
    public static Map<Integer,ArrayList<WingCoreAdvanceBaseVo>> WingCoreAdvanceBaseMap;
    public static Map<Integer,WingCoreBaseVo> WingCoreBaseMap;
    public static Map<Integer,ArrayList<LotteryBaseVo>> LotteryBaseMap;
    public static Map<Integer,HeroTagBaseVo> HeroTagBaseMap;
    public static Map<Integer,ArrayList<DragonQualityBaseVo>> DragonQualityBaseMap;
    public static Map<Integer,EmperorBaseVo> EmperorBaseMap;
    public static Map<Integer,ArrayList<EmperorQualityBaseVo>> EmperorQualityBaseMap;
    public static Map<Integer,EmperorLevelBaseVo> EmperorLevelBaseMap;
    public static Map<Integer,EmperorLevelUpBaseVo> EmperorLevelUpBaseMap;
    public static Map<Integer,ArrayList<EmperorAdvanceBaseVo>> EmperorAdvanceBaseMap;
    public static Map<Integer,ArrayList<EmperorAchieveBaseVo>> EmperorAchieveBaseMap;
    public static Map<Integer,ArrayList<EmperorSealBaseVo>> EmperorSealBaseMap;
    public static Map<Integer,RealmBaseVo> RealmBaseMap;
    public static Map<Integer,ArrayList<RealmDanBaseVo>> RealmDanBaseMap;
    public static Map<Integer,ArrayList<RealmDanGasBaseVo>> RealmDanGasBaseMap;
    public static Map<Integer,ArrayList<RealmCycloneBaseVo>> RealmCycloneBaseMap;
    public static Map<Integer,ArrayList<RealmCycloneUpBaseVo>> RealmCycloneUpBaseMap;
    public static Map<Integer,ArrayList<RealmDanSoulBaseVo>> RealmDanSoulBaseMap;
    public static int gameID;
    public static int platformID;
    public static int serverID;
    public static Document configXmlDoc;


    public static void init() throws Exception{


        dataPath="";
        readMap(GameSetBaseVo.class);
        readMap(RoleBaseVo.class);
        readMap(MapBaseVo.class);
        readMap(NpcBaseVo.class);
        readMap(RoleLevelBaseVo.class);

        readMap(PropBaseVo.class);
        readMapList(DropBaseVo.class, "groupID");
        readMapList(SkillBaseVo.class);
        readMap(ShopBaseVo.class);
        readMap(EnchantBaseVo.class);
        readMap(SkillShopBaseVo.class);
        readMapList(SkillDropBaseVo.class);
        readMap(EnchantshopBaseVo.class);
        readMap(MissionBaseVo.class);
        readMapList(NpcLayoutBaseVo.class);
        readMap(TowerBaseVo.class);
        readMap(MapChapterBaseVo.class);
        readMapList(TalentBaseVo.class);
        readMap(SignBaseVo.class);
        readMapList(SignBoxBaseVo.class);
        readMap(SignWeekBaseVo.class);
        readMap(DragonBaseVo.class);
        readMap(DragonStoneUpBaseVo.class);
        readMap(EquipRefineBaseVo.class);
        readMap(CommZdlBaseVo.class);
        readMap(EquipDissolveBaseVo.class);
        readMap(EquipInheritBaseVo.class);
        readMap(PropComposeBaseVo.class);
        readMap(ArenaRewardBaseVo.class);
        readMap(CommCooldownBaseVo.class);
        readMap(AutoBuyBaseVo.class);
        readMap(NameBaseVo.class);
        readMapList(RobotBaseVo.class);
        readMap(RedPacketsBaseVo.class);
        readMap(BagBaseVo.class);
        readMap(CommAwardBaseVo.class);
        readMap(NpcLevelBaseVo.class);
        readMap(FunctionOpenBaseVo.class);
        readMapList(TowerBaseVo.class);
        readMap(MailBaseVo.class);
        readMapList(PropShopBaseVo.class);
        readMap(WorldTreeBaseVo.class);
        readMapList(MonsterIllustratedBaseVo.class);
        readMap(SkinBaseVo.class);
        readMap(HonorAiringBaseVo.class);
        readMap(MessageBaseVo.class);
        readMapList(TelphoneBaseVo.class);
        readMapList(GangSkillBaseVo.class);
        readMapList(GangbuildBaseVo.class);
        readMap(GangContributeBaseVo.class);
        readMap(VipBaseVo.class);
        readMapList(CommCostBaseVo.class);
        readMap(DragonEggBaseVo.class);
        readMapList(ActivationBaseVo.class);
        readMapList(CommUnlockBaseVo.class);
        readMap(ShieldBaseVo.class);
        readMap(EquipAdvanceBaseVo.class);
        readMap(EquipIntensifyExpBaseVo.class);
        readMap(EquipIntensifyUpBaseVo.class);
        readMap(AiringBaseVo.class);
        readMap(GMBaseVo.class);
        readMap(DragonAchieveBaseVo.class);
        readMapList(SkinUpBaseVo.class);
        readMapList(SkinAdvanceBaseVo.class);
        readMap(GangBoxBaseVo.class);
        readMap(SystemUpdateBaseVo.class);
        readMap(UserUpdateBaseVo.class);
        readMapList(RangeConvertBaseVo.class);
        readMap(GangMapBaseVo.class);
        readMap(WingBaseVo.class);
        readMapList(WingCoreUpBaseVo.class);
        readMap(GangDragonUpBaseVo.class);
        readMap(GangDragonWishBaseVo.class);
        readMap(LimitTimeActivationBaseVo.class);
        readMap(GiftBaseVo.class);
        readMap(DragonLevelBaseVo.class);
        readMapList(DragonAdvanceBaseVo.class);
        readMap(NpcAttributeBaseVo.class);
        readMap(NpcAttributePowerBaseVo.class);
        readMap(EquipPurifyBaseVo.class);
        readMap(PropSuitBaseVo.class);
        readMap(MineralResBaseVo.class);
        readMapList(CommRewardPropBaseVo.class);
        readMapList(WingCoreAdvanceBaseVo.class);
        readMap(WingCoreBaseVo.class);
        readMapList(LotteryBaseVo.class);
        readMap(HeroTagBaseVo.class);
        readMapList(DragonQualityBaseVo.class);
        readMap(EmperorBaseVo.class);
        readMapList(EmperorQualityBaseVo.class);
        readMap(EmperorLevelBaseVo.class);
        readMap(EmperorLevelUpBaseVo.class);
        readMapList(EmperorSealBaseVo.class);
        readMapList(EmperorAdvanceBaseVo.class);
        readMapList(EmperorAchieveBaseVo.class);
        readMap(RealmBaseVo.class);
        readMapList(RealmDanBaseVo.class);
        readMapList(RealmDanGasBaseVo.class);
        readMapList(RealmCycloneBaseVo.class);
        readMapList(RealmCycloneUpBaseVo.class);
        readMapList(RealmDanSoulBaseVo.class);
        userLevelMax=  UserUpdateBaseMap.get(1).max;
        gangMaxLevel = (byte)Model.GangbuildBaseMap.get(1).size();
//        for(int id : Model.DropBaseMap.keySet()){
//            System.out.println("======="+id);
//            BaseModel.getDropProps(id,0);
//        }

    }
    private static <T> void readMap(Class<T> t)throws Exception {
        readMap(t,"ID");
    }
    private static<T> void readMap(Class<T> t, String keyName)
            throws Exception {
        ByteBuffer bytes = JkTools.file2Bytebuff(dataPath+t.getName().replace("table.",
                "data/")
                + ".tbl");
        int size = bytes.getInt();

        HashMap<Integer, T> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            IBytes vo = (IBytes) t.newInstance();
            vo.fromBytes(bytes);
            map.put(t.getField(keyName).getInt(vo), (T) vo);
        }
        Model.class.getField(t.getName().replace("table.","").replace("Vo", "Map")).set(null, map);
    }



    private static<T> void fillMap(Map<Integer, T> map ,int start,int end)
    {

        T vo=null;
        for (int i = start; i <= end; i++) {
            if(map.containsKey(i)){
                vo=map.get(i);
            }else {
                map.put(i, vo);
            }
        }
    }


    private static<T> T[] readArray(Class<T> t) throws Exception {
        ByteBuffer bytes = JkTools.file2Bytebuff(dataPath+t.getName().replace("table.",
                "data/")
                + ".tbl");
        int size = bytes.getInt();

        T[] voArray = (T[]) Array.newInstance(t, size);
        for (int i = 0; i < size; i++) {
            IBytes vo = (IBytes) t.newInstance();

            vo.fromBytes(bytes);

            voArray[i] = (T) vo;
        }
        return ((T[]) voArray);
    }
    private static <T> void readMapList(
            Class<T> t) throws Exception {
        readMapList(t,"ID",null);
    }
    private static <T> void readMapList(
            Class<T> t,String keyName) throws Exception {
        readMapList(t,keyName,null);
    }
    private static <T> void readMapList(
            Class<T> t, String keyName,String fieldName) throws Exception {
        T[] mapcells = readArray(t);
        Field keyField = t.getField(keyName);
        HashMap<Integer, ArrayList<T>> map = new HashMap<>();
        for (T vo : mapcells) {
            ArrayList<T> v = map.get(keyField.getInt(vo));
            if (v == null) {
                v = new ArrayList<T>();
                map.put(keyField.getInt(vo), v);
            }
            v.add(vo);
        }
        if(fieldName==null){
            fieldName=t.getName().replace("table.","").replace("Vo", "Map");
        }
        Model.class.getField(fieldName).set(null, map);
    }

    public static    int[] readRoleAttributes (int[] attributesMap)
    {
        int [] values = new int[AttributeEnum.MAX];
        for (int i = 0,len= attributesMap.length; i <len; i+=2) {
            values [attributesMap[i]]=attributesMap[i+1];
        }
        return values;
    }
    public  static  int[] getDataInRange(int type,int value){
        RangeConvertBaseVo lastVo=null;
        for (RangeConvertBaseVo vo:RangeConvertBaseMap.get(type)) {
            if(value>=vo.rangeMix){
                lastVo=vo;
            }else{
                break;
            }
        }
        if(lastVo!=null&&lastVo.targetData!=null){
            return lastVo.targetData;
        }
        return null;
    }
}
