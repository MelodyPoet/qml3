package prop;

import airing.AiringModel;
import base.EquipQualityPVoJoin;
import comm.Client;
import comm.Model;
import comm.User;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseBlobDeal;
import gluffy.utils.JkTools;
import protocol.*;
import snatch.SnatchModel;
import sqlCmd.AllSql;
import table.*;

import java.nio.ByteBuffer;
import java.util.*;

public class PropModel extends BaseBlobDeal{
    private User user;
    public HashMap<Long, PropPVo> bagItems;
    public PropPVo refinedItem;
    public ArrayList<Short> refinedData;
    public int propShopFlushCD;
    public HashMap<Byte,PropShopPVo> propShopMap;
    public PropShopPVo trampShop;
//    public static final int GROUP_SIZE = 8;
    //public PropPVo[] equipItems;
    public  PropModel(User user){
        this.user=user;
        bagItems = new HashMap<>();
        propShopMap = new HashMap<>();
        //equipItems = new PropPVo[9];
    }
	public static boolean isEquip(int baseID) {
		 
		return baseID>1000&&baseID<=10000;
	}
    public static boolean isAttributeItem(int baseID) {

        return baseID<=100;
    }
    public static boolean isMissionItem(int baseID) {

        return baseID>11000&&baseID<=12000;
    }
    public static boolean isSnatchItem(int baseID) {

        return baseID>=12400&&baseID<12700;
    }
    public static boolean isDragonEgg(int baseID) {

        return baseID>=12800&&baseID<=12809;
    }
    public static boolean isDragonStone(int baseID) {

        return baseID>=13000&&baseID<14000;
    }
    public static boolean isDragon(int baseID){
        return baseID>14000&&baseID<=17000;
    }
    public static boolean isSoul(int baseID){
        return baseID>17000&&baseID<=18000;
    }
    public static boolean isIntegral(int baseID){
        return  baseID >= 20000&& baseID <22000;
    }

    public PropPVo getEquipByIndex(byte index) {
        return user.cacheUserVo.equipItems[index];
    }

    public boolean setEquipByIndex(byte index, PropPVo pvo) {

         user.cacheUserVo.equipItems[index]=pvo;
        return true;
    }
    public PropPVo addInBag(int baseID ,int count,boolean updateDB,boolean sendClient) {
        PropPVo tempVo = new PropPVo();
        tempVo.baseID = baseID;
        tempVo.count=count;
     tempVo= addInBag(tempVo, true);
        if(sendClient)        new AddPropRspd(user.client, PropCellEnum.BAG,tempVo);
return tempVo;
    }

    public PropPVo addInBag(PropPVo propPVo,boolean updateDB,boolean log,int reasonType){
        if(log){
            PropLogVo propLogVo = new PropLogVo();
            propLogVo.userID = user.guid;
            propLogVo.propID = propPVo.baseID;
            propLogVo.count = propPVo.count;
            propLogVo.rensonType = reasonType;
            propLogVo.createTime = JkTools.getGameServerTime(null);
            AllSql.propLogSql.insertNew(propLogVo);
        }
        return addInBag(propPVo,updateDB);
    }

    public PropPVo addInBag(PropPVo propPVo,boolean updateDB) {
        if(propPVo.count==0)propPVo.count=1;


        boolean _isEquip=isEquip(propPVo.baseID);
        if(_isEquip) {
            if (updateDB) {
                byte quality = (byte) Model.PropBaseMap.get(propPVo.baseID).quality;
                if(user.equipQualityMap.containsKey(quality)){
                    ByteIntPVo byteIntPVo = user.equipQualityMap.get(quality);
                    byteIntPVo.value += propPVo.count;
                }else{
                    ByteIntPVo byteIntPVo = new ByteIntPVo();
                    byteIntPVo.key = quality;
                    byteIntPVo.value = propPVo.count;
                    user.equipQualityMap.put(quality,byteIntPVo);
                }
                AllSql.userSql.update(user, AllSql.userSql.FIELD_EQUIP_QUALITY,"'"+EquipQualityPVoJoin.instance.joinCollection(user.equipQualityMap.values())+"'");
                new UpdateEquipQualityRspd(user.client,user.equipQualityMap.values());
                AllSql.propSql.insertNew(user, propPVo, PropCellEnum.BAG);
            }

            bagItems.put(propPVo.tempID, propPVo);
            return propPVo;
        }else{

            PropPVo oldProp=null;
            for (PropPVo p : bagItems.values()) {
                if(p.baseID==propPVo.baseID){
                    oldProp=p;
                    break;
                }
            }
             if(oldProp==null) {
                 //获得新的可抢夺的物品 要加入 被抢数据里
                 if(isSnatchItem(propPVo.baseID)){
                     SnatchModel.addOwnner(propPVo.baseID,user.cacheUserVo);
                 }
                 int maxCount=Model.PropBaseMap.get(propPVo.baseID).maxCount;
                 if(propPVo.count>maxCount){
                     propPVo.count=maxCount;
                 }
                 if (updateDB) {
                     AllSql.propSql.insertNew(user, propPVo, PropCellEnum.BAG);
                 }
                 bagItems.put(propPVo.tempID, propPVo);

                 return propPVo;
             }else{
                 oldProp.count+=propPVo.count;
                 int maxCount=Model.PropBaseMap.get(oldProp.baseID).maxCount;
                 if(oldProp.count>maxCount){
                     oldProp.count=maxCount;
                 }
                 if (updateDB) {
                     AllSql.propSql.update(AllSql.propSql.FIELD_COUNT,oldProp.count+"", oldProp.tempID);
                 }
                 return oldProp;
             }

        }

    }

    public PropPVo getPropInBag(long tempID) {
        return bagItems.get(tempID);
    }

    public void deleteBag(long tempID) {
        bagItems.remove(tempID);
    }

    public void deleteBag(long tempID,boolean log,int reasonType) {
        if(log){
            PropPVo propPVo = bagItems.get(tempID);
            PropLogVo propLogVo = new PropLogVo();
            propLogVo.userID = user.guid;
            propLogVo.propID = propPVo.baseID;
            propLogVo.count = -propPVo.count;
            propLogVo.rensonType = reasonType;
            propLogVo.createTime = JkTools.getGameServerTime(null);
            AllSql.propLogSql.insertNew(propLogVo);
        }
        deleteBag(tempID);
    }

    public int [] getAttributes(PropPVo propPVo){
      PropBaseVo baseVo=Model.PropBaseMap.get(propPVo.baseID);
        int []attributes = Model.readRoleAttributes(baseVo.attributes);
        if(propPVo.intensify>0) {
          //  float addPer = 1 + Model.EquipIntensifyBaseMap.get((int)baseVo.level).addAttributeMax * 0.01f * propPVo.intensify / 6f;
            for (int i = 0; i < attributes.length; i++) {
            //    attributes[i] = (int) (attributes[i] * addPer);
            }
        }
        return attributes;
    }
    public long deleteBag(int baseID, int stuffCount,boolean sendClient) {
        for (PropPVo pvo :bagItems.values()){
            if(pvo.baseID!=baseID)continue;
deleteBag(pvo,stuffCount,sendClient);
            return pvo.tempID;
        }
        return 0;
    }
    public long deleteBag(PropPVo stuff, int stuffCount,boolean sendClient) {
        if(stuffCount<0||stuff.count<stuffCount)return  0;
        if(stuff.count==stuffCount){
            user.propModel.deleteBag(stuff.tempID,false,0);
            if(sendClient){
                AllSql.propSql.delete(stuff.tempID);

                new DeletePropRspd(user.client,stuff.tempID);
            }
            return stuff.tempID;
        }
        stuff.count-=stuffCount;

        if(sendClient){
            AllSql.propSql.update(AllSql.propSql.FIELD_COUNT,stuff.count+"", stuff.tempID);
            new AddPropRspd(user.client,PropCellEnum.BAG,stuff);
        }
        return stuff.tempID;
    }
    public  void  addListToBag(int [] items,int reasonType){
        if(items==null)return;
        ArrayList<PropPVo> getProps=new ArrayList<>();
        ArrayList<AiringMessagePVo> equipAiringList = new ArrayList<>();
        ArrayList<DragonPVo> dragonList = new ArrayList<>();
        StringBuffer sb = new StringBuffer(user.dragonStoneOwnLevel);
        for (int i =0;i<items.length;i+=2) {

            if(items[i]<=0)continue;
            int count=items[i+1];
            if(count<=0)continue;
            if(items[i]<1000) {
                if(items[i]<100){
                    if (items[i] == UserDataEnum.EXP) {
                        user.addExpInGaming(count);
                    }else if(items[i] == UserDataEnum.RED_NESS_MONEY){
                        user.updataRedness(count);
                        user.addUserData((byte)items[i], count, true,reasonType);
                    } else {
                        user.addUserData((byte)items[i], count, true,reasonType);
                    }
                }else if(items[i]<300){
                    GangVo gangVo = user.cacheUserVo.gang.gangVo;
                    if(gangVo == null)continue;
                    GangUserVo gangUserVo = gangVo.users.get(user.guid);
                    if(gangUserVo == null)return;
                    if(items[i] == 200){
                        gangVo.addExp(user.client,count);
                    }else{
                        gangUserVo.addGangUserData((byte) (items[i]-200),count,true);
                    }
                }
             }else{
                PropBaseVo propBaseVo = Model.PropBaseMap.get(items[i]);
                if(isIntegral(propBaseVo.ID))continue;
                if(isEquip(propBaseVo.ID)){
                    items[i] = items[i]%1000+user.baseID*1000;
                    for(int j=0;j<count;j++){
                        PropPVo pvo=new PropPVo();
                        pvo.baseID=items[i];
                        pvo.count=1;
                        getProps.add(user.propModel.addInBag(pvo,true,true,reasonType));
                    }
                    equipAiring(user,items[i],equipAiringList);
                    continue;
                }
                if(propBaseVo.type == PropTypeEnum.DRAGON){
                   DragonPVo dragonPVo = user.dragonModel.addDragonUseProp(count,propBaseVo);
                    if(dragonPVo == null)continue;
                    dragonList.add(dragonPVo);
                    continue;
                }
                if(propBaseVo.type == PropTypeEnum.EMPEROR){
                    user.emperorModel.addEmperorUseProp(count,propBaseVo);
                    continue;
                }
                if(PropModel.isSoul(items[i])) {
//                     user.illustratedModel.addSoul(user.currentMap.ID,pvo);
                    continue;
                }
                if(PropModel.isDragonEgg(items[i])){
                    user.cacheUserVo.dragonEggModel.addDragonEgg(propBaseVo);
                    continue;
                }
                if(propBaseVo.type == PropTypeEnum.SKIN_PROP){
                    items[i] += (user.baseID - items[i]%1000/100)*100;
                }
                if(propBaseVo.type == PropTypeEnum.HUNSHI){
                    int level = propBaseVo.ID%100+1;
                    if(user.dragonStoneOwnLevel.indexOf(String.valueOf(level)) == -1){
                        if(sb.length() == 0){
                            sb.append(level);
                        }else{
                            sb.append("," + level);
                        }
                    }
                    user.addUserData(UserDataEnum.LJ_DRAGON_STONE_COUNT,count,true);
                }
//                if(propBaseVo.type == PropTypeEnum.DragonEgg){
//                    user.cacheUserVo.dragonEggModel.addDragonEgg(user.client,propBaseVo);
//                    continue;
//                }
                PropPVo pvo=new PropPVo();
                pvo.baseID=items[i];
                pvo.count=count;
                getProps.add(user.propModel.addInBag(pvo,true,true,reasonType));
            }
        }
if(getProps.size()>0)
        new AddPropListRspd(user.client, PropCellEnum.BAG,getProps);
        if(dragonList.size() > 0)
            for(DragonPVo dragonPVo : dragonList){
                new AddDragonRspd(user.client,dragonPVo);
            }
        if(equipAiringList.size()>0)
            AiringModel.broadcast(equipAiringList);
        if(sb.length() > user.dragonStoneOwnLevel.length()){
            AllSql.userSql.update(user,AllSql.userSql.FIELD_OWN_DRAGON_STONE_LEVEL,"'"+sb.toString()+"'");
            new DragonStoneOwnLevelRspd(user.client,sb.toString());
        }
    }

    public void addListToBag(Collection<PropPVo> addList,int reasonType) {

        if(addList.size()==0)return;
        ArrayList<PropPVo> getProps=new ArrayList<>();
        ArrayList<AiringMessagePVo> equipAiringList = new ArrayList<>();
        ArrayList<DragonPVo> dragonList = new ArrayList<>();
        StringBuffer sb = new StringBuffer(user.dragonStoneOwnLevel);
        for (PropPVo pVo:addList){
            if(pVo.baseID<=0)continue;
            if(pVo.baseID<1000) {
                if(pVo.baseID<200){
                    if (pVo.baseID == UserDataEnum.EXP) {
                        user.addExpInGaming(pVo.count);
                    }else if(pVo.baseID == UserDataEnum.RED_NESS_MONEY){
                        user.updataRedness(pVo.count);
                        user.addUserData((byte)pVo.baseID, pVo.count, true,reasonType);
                    } else {
                        user.addUserData((byte)pVo.baseID, pVo.count, true,reasonType);
                    }
                }else if(pVo.baseID<300){
                    GangVo gangVo = user.cacheUserVo.gang.gangVo;
                    if(gangVo == null)continue;
                    GangUserVo gangUserVo = gangVo.users.get(user.guid);
                    if(gangUserVo == null)return;
                    if(pVo.baseID == 200){
                        gangVo.addExp(user.client,pVo.count);
                    }else{
                        gangUserVo.addGangUserData((byte) (pVo.baseID-200),pVo.count,true);
                    }
                }
            }else{
                if(isIntegral(pVo.baseID))continue;
                if(isEquip(pVo.baseID)){
                    pVo.baseID = pVo.baseID%1000+user.baseID*1000;
                    for(int j=0;j<pVo.count;j++){
                        PropPVo pvo=new PropPVo();
                        pvo.baseID=pVo.baseID;
                        pvo.count=1;
                        getProps.add(user.propModel.addInBag(pvo,true,true,reasonType));
                    }
                    equipAiring(user,pVo.baseID,equipAiringList);
                    continue;
                }
                PropBaseVo propBaseVo = Model.PropBaseMap.get(pVo.baseID);
                if(propBaseVo.type == PropTypeEnum.DRAGON){
                    DragonPVo dragonPVo = user.dragonModel.addDragonUseProp(pVo.count,propBaseVo);
                    if(dragonPVo == null)continue;
                    dragonList.add(dragonPVo);
                    continue;
                }
                if(propBaseVo.type == PropTypeEnum.EMPEROR){
                    user.emperorModel.addEmperorUseProp(pVo.count,propBaseVo);
                    continue;
                }
                if(PropModel.isSoul(pVo.baseID)) {
//                     user.illustratedModel.addSoul(user.currentMap.ID,pvo);
                    continue;
                }
                if(PropModel.isDragonEgg(pVo.baseID)){
                    user.cacheUserVo.dragonEggModel.addDragonEgg(propBaseVo);
                    continue;
                }
                if(propBaseVo.type == PropTypeEnum.SKIN_PROP){
                    pVo.baseID += (user.baseID - pVo.baseID%1000/100)*100;
                }
                if(propBaseVo.type == PropTypeEnum.HUNSHI){
                    int level = propBaseVo.ID%100+1;
                    if(user.dragonStoneOwnLevel.indexOf(String.valueOf(level)) == -1){
                        if(sb.length() == 0){
                            sb.append(level);
                        }else{
                            sb.append("," + level);
                        }
                    }
                    user.addUserData(UserDataEnum.LJ_DRAGON_STONE_COUNT,pVo.count,true);
                }
//                if(propBaseVo.type == PropTypeEnum.DragonEgg){
//                    user.cacheUserVo.dragonEggModel.addDragonEgg(user.client,propBaseVo);
//                    continue;
//                }
                getProps.add(user.propModel.addInBag(pVo, true,true,reasonType));
            }
        }
        new AddPropListRspd(user.client, PropCellEnum.BAG,getProps);
        if(dragonList.size() > 0)
            for(DragonPVo dragonPVo : dragonList){
                new AddDragonRspd(user.client,dragonPVo);
            }
        if(equipAiringList.size()>0)
            AiringModel.broadcast(equipAiringList);
        if(sb.length() > user.dragonStoneOwnLevel.length()){
            user.dragonStoneOwnLevel = sb.toString();
            AllSql.userSql.update(user,AllSql.userSql.FIELD_OWN_DRAGON_STONE_LEVEL,"'"+user.dragonStoneOwnLevel+"'");
            new DragonStoneOwnLevelRspd(user.client,sb.toString());
        }
    }

    private static void equipAiring(User user,int propID,ArrayList<AiringMessagePVo> list){
        //走马灯
        HonorAiringBaseVo airingVo = Model.HonorAiringBaseMap.get(1);
        if(airingVo==null)return;
        if(airingVo.isUse != 1)return;
        PropBaseVo vo = Model.PropBaseMap.get(propID);
        if(vo==null)return;
        if(vo.quality%airingVo.divisor != 0)return;
        if(JkTools.compare(vo.quality,airingVo.conditionParams[1],airingVo.conditionParams[0]) == false)return;
        if(airingVo.conditionParams.length>=4&& JkTools.compare(vo.quality,airingVo.conditionParams[3],airingVo.conditionParams[2]) == false)return;
        AiringMessagePVo pVo = new AiringMessagePVo();
        pVo.type = 1;
        String quality = "";
        switch (vo.quality){
            case 0: quality = "[D1D1D1]"; break;
            case 1: quality = "[57E157]"; break;
            case 2: quality = "[57BCE1]"; break;
            case 3: quality = "[D248FF]"; break;
            case 4: quality = "[EC9141]"; break;
            case 5: quality = "[FF4848]"; break;
        }
//        pVo.msg = "恭喜 "+user.cacheUserVo.passportVo.name+" 获得 "+quality+" 级"+ vo.name +" 。";
        pVo.msg = airingVo.msg.replace("{1}",user.cacheUserVo.name).replace("{2}",quality+vo.name+"[-]");
        pVo.time = 1;
        list.add(pVo);
    }

    public PropPVo splitInBag(PropPVo pvo,byte toCell) {
        deleteBag(pvo, 1, false);
        PropPVo newVo=new PropPVo();
        newVo.baseID=pvo.baseID;
        newVo.count=1;
        if(toCell>0) {
        AllSql.propSql.insertNew(user, newVo, toCell);

          //  new AddPropRspd(user.client, toCell, newVo);
        }
        return newVo;
    }
    public  ArrayList<Short>  createExtraAttributes(PropBaseVo baseProp, List<Byte> locks){
        if(locks == null){
            locks = new ArrayList<>();
        }
        Random rnd=new Random();

        ArrayList<Short> exAttribute = new ArrayList<>();
        int exAttrCount =baseProp.extraAttributesCount==null?0:  JkTools.getRandRange(baseProp.extraAttributesCount, 100)+1;
        //  exAttrCount=2;
        if(locks.size()>0){
            exAttrCount=Math.max(exAttrCount,locks.size()+1);
        }
        exAttrCount=Math.min(exAttrCount,baseProp.extrAttCountMax);
        if(exAttrCount>0) {
            HashSet<Byte> addSet = new HashSet<>();
            for (int i = 0; i < baseProp.extraAttributes.length / 2; i++) {
                addSet.add((byte) i);
            }
//            //属性分组
//            if(baseProp.extrAttGroup!=null) {
////                for (int i = 0, len = baseProp.extrAttGroup.length; i < len; ) {
////                    int groupCount = baseProp.extrAttGroup[i++];
////                    int keepIndex = rnd.nextInt(groupCount);
////                    for (int j = 0; j < groupCount; j++) {
////                        if (j != keepIndex)
////                            addSet.remove((byte) (baseProp.extrAttGroup[i++]));
////                        else
////                            i++;
////                    }
////                }
//                for (int[] items:  baseProp.extrAttGroup.values() ) {
//                    int keepIndex = rnd.nextInt(items.length);
//                    for (int j = 0; j < items.length; j++) {
//                        if (j != keepIndex) addSet.remove((byte) items[j]);
//                    }
//                }
//            }

            if (exAttrCount < addSet.size()){
                for (int i = 0, len = addSet.size() - exAttrCount; i < len; i++) {
                    byte index = -1;
                    while (index == -1 || locks.contains(index) || !addSet.contains(index)){
                        index = (byte) rnd.nextInt(addSet.size());
                    }
                    addSet.remove(index);

                }
            }
            for (byte i = 0; i < baseProp.extraAttributes.length / 2; i++) {
                if(locks.contains(i)){
                    exAttribute.add(user.propModel.refinedData.get(i));
                } else if (addSet.contains(i)) {
                    exAttribute.add((short)(baseProp.extraAttributes[2*i+1]*(rnd.nextInt(baseProp.rndExtraAttributes) + 1+100)/100));
                }else{
                    exAttribute.add((short) 0);
                }
            }
        }
        return exAttribute;
    }

    public void updateInBag(long tempID, boolean saveSql, boolean sendRspd) {
       PropPVo pvo= bagItems.get(tempID);
        if(pvo==null){
            if(saveSql){
                AllSql.propSql.delete(tempID);
            }
            if(sendRspd){
new DeletePropRspd(user.client,tempID);
            }
        }else {
            if(saveSql){
                AllSql.propSql.update(AllSql.propSql.FIELD_COUNT,pvo.count+"", tempID);

            }
            if(sendRspd){
                new AddPropRspd(user.client,PropCellEnum.BAG,pvo);

            }
        }
    }

    public boolean checkBagIsFull() {
        if (user.propModel.bagItems.size() >= user.getUserData(UserDataEnum.BAG_CELL_HAVE_COUNT)) {
            int bagCount = 0;
            int equipCount = 0;
            for (PropPVo propPVo : user.propModel.bagItems.values()) {
                PropBaseVo propBaseVo = Model.PropBaseMap.get(propPVo.baseID);
                if (PropModel.isEquip(propBaseVo.ID))equipCount++;
                if(JkTools.indexOf(Model.GameSetBaseMap.get(51).intArray,propBaseVo.type)>=0)continue;
                bagCount++;
            }
            if(equipCount >= user.getUserData(UserDataEnum.EQUIP_BAG_COUNT)){
                new ServerTipRspd(user.client, (short) 315, null);
                return true;
            }
            if (bagCount >= user.getUserData(UserDataEnum.BAG_CELL_HAVE_COUNT)) {
                new ServerTipRspd(user.client, (short) 316, null);
                return true;
            }
        }
        return false;
    }

    public void randomPropShopInit(){
        user.propModel.propShopFlushCD = nextEndTime(user.client);
        for(Map.Entry<Integer,ArrayList<PropShopBaseVo>> item : Model.PropShopBaseMap.entrySet()){
            PropShopBaseVo vo = item.getValue().get(0);
            if(vo.type == 0)continue;
            PropShopPVo propShopPVo = new PropShopPVo();
            int type = item.getKey();
            propShopPVo.type = (byte) type;
            propShopPVo.name = vo.name;
            propShopPVo.propList = new ArrayList<>();
            int[] arr = null;
            if(vo.type == 1){
                arr = Model.GameSetBaseMap.get(13).intArray;
            }
            if(vo.type == 2){
                arr = Model.GameSetBaseMap.get(46).intArray;
            }
            propShopPVo.freeTimes = (byte) arr[propShopPVo.type-(vo.type-1)*10-1];
            propShopMap.put(propShopPVo.type,propShopPVo);
            randomPropShop(type,false);
        }
        saveSqlData();
        new RandomPropShopRspd(user.client,propShopFlushCD, propShopMap.values());
    }

    public static int nextEndTime(Client client){
        Calendar calendar = JkTools.getCalendar();
        calendar.add(Calendar.HOUR_OF_DAY,client.addHours);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int nextTime = -1;
        int[] arr = Model.GameSetBaseMap.get(14).intArray;
        for(int t: arr){
            if(hours>=t)continue;
            nextTime = t;
            break;
        }
        if(nextTime == -1){
            calendar.add(Calendar.DAY_OF_YEAR,1);
            nextTime = arr[0];
        }
        calendar.set(Calendar.HOUR_OF_DAY,nextTime);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return JkTools.getGameServerTime(calendar.getTimeInMillis());
    }

    public void randomPropShop(int type,boolean sendClient){
        if(type == 0){
            for(Map.Entry<Integer,ArrayList<PropShopBaseVo>> item : Model.PropShopBaseMap.entrySet()){
                PropShopBaseVo vo = item.getValue().get(0);
                if(vo.type == 0)continue;
                randomPropShop((byte)(int)(item.getKey()),false);
            }
            saveSqlData();
            new RandomPropShopRspd(user.client,propShopFlushCD, propShopMap.values());
            return;
        }
        PropShopBaseVo propShopBaseVo = Model.PropShopBaseMap.get(type).get(0);
        if(propShopBaseVo.type != 0){
            if(propShopMap.size() == 0 || !propShopMap.containsKey((byte)type))return;
            PropShopPVo propShopPVo = propShopMap.get((byte)type);
            propShopPVo.propList.clear();
            randomShopProp(type,propShopPVo);
            if(sendClient){
                ArrayList<PropShopPVo> list = new ArrayList<>();
                list.add(propShopPVo);
                new RandomPropShopRspd(user.client,propShopFlushCD,list);
                saveSqlData();
            }
        }else{
            PropShopPVo propShopPVo = new PropShopPVo();
            propShopPVo.type = (byte)propShopBaseVo.ID;
            propShopPVo.name = propShopBaseVo.name;
            randomShopProp(type,propShopPVo);
            trampShop = propShopPVo;
            new TrampShopRspd(user.client,trampShop);
        }
    }

    public void randomShopProp(int type,PropShopPVo propShopPVo){
        int group = (user.getUserData(UserDataEnum.LEVEL)/10+1)*10;
        int index = 1;
        int rand = 100;
        Random random = new Random();
        Random ran = new Random();
        for(PropShopBaseVo vo : Model.PropShopBaseMap.get(type)){
            if(vo.groupID != group)continue;
            if(vo.index < index){
                continue;
            }else if(vo.index == index){
                if(rand == 100){
                    rand = random.nextInt(100);
                }
                rand = rand - vo.probability;
                if(rand <= 0){
                    ShopPropPVo shopPropPVo = new ShopPropPVo();
                    int r = ran.nextInt(vo.prop.length/2);
                    shopPropPVo.propID = vo.prop[2*r];
                    if(PropModel.isEquip(shopPropPVo.propID)){
                        shopPropPVo.propID = shopPropPVo.propID%1000+user.baseID*1000;
                    }
                    shopPropPVo.count = vo.prop[2*r+1];
                    shopPropPVo.buyType = (byte) vo.costUserdata[0];
                    shopPropPVo.price = vo.costUserdata[1];
                    shopPropPVo.isBuy = false;
                    propShopPVo.propList.add(shopPropPVo);
                    index++;
                    rand = 100;
                }
            }else{
                break;
            }
//            if(index == 1+GROUP_SIZE)break;
        }
    }

    @Override
    protected byte[] saveDataBytes() {
        int size = propShopMap.size()*(3+4+2+1)+2+4;
        for(PropShopPVo propShopPVo : propShopMap.values()){
            size += JkTools.getStringLength(propShopPVo.name);
            size += propShopPVo.propList.size()*14;
        }
        byte[] bytes=new byte[size];
        ByteBuffer buffer=ByteBuffer.wrap(bytes);
        buffer.putShort((short) propShopMap.size());
        for(Map.Entry<Byte,PropShopPVo> item : propShopMap.entrySet()){
            item.getValue().toBytes(buffer);
            buffer.put(item.getKey());
        }
        buffer.putInt(propShopFlushCD);
        return bytes;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {
        if(buffer==null)return;
        int count = buffer.getShort();
        for(int i = 0; i < count; i++){
            PropShopPVo propShopPVo = new PropShopPVo();
            propShopPVo.fromBytes(buffer);
            propShopMap.put(buffer.get(),propShopPVo);
        }
        propShopFlushCD = buffer.getInt();
    }

    @Override
    public void saveSqlData() {
        AllSql.userSql.update(user,AllSql.userSql.FIELD_PROP_SHOP_GROUP,saveData());
    }

    @Override
    public void unloadUser() {
        user = null;
    }
}
