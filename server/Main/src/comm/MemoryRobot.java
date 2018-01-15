package comm;

import prop.PropModel;
import protocol.PropPVo;
import table.PropBaseVo;
import table.PropTypeEnum;
import table.RobotBaseVo;
import table.RobotType;

import java.util.ArrayList;

/**
 * Created by jackie on 14-4-18.
 */
public class MemoryRobot {
    public static ArrayList<CacheUserVo>[]  allSantchUsers;
    public static void initAll()  {
        initForSnatch();
    }
    private static void initForSnatch(){
        allSantchUsers =new  ArrayList[Model.userLevelMax+1];
        for (int i = 1; i <= Model.userLevelMax; i++) {
            allSantchUsers[i]=new ArrayList<>();
//            for (int j = 0; j <10 ; j++) {
//
//            }
        }
        for (RobotBaseVo vo:Model.RobotBaseMap.get((int)RobotType.SNATCH)){
            CacheUserVo uvo=new CacheUserVo();
                 uvo.guid=0;
                uvo.level=(byte)vo.level;
               uvo.baseID= (byte)vo.baseID;
            uvo.zdl=vo.zdl;
                uvo.passportVo=new CachePassportVo();
            uvo.portrait = (byte)vo.portrait;
               for(int propId: vo.equip){
                   if(!PropModel.isEquip(propId))continue;
                   PropBaseVo propBaseVo = Model.PropBaseMap.get(propId);
                   PropPVo pVo = new PropPVo();
                   pVo.baseID = propId;
                   pVo.count = 1;

                   switch (propBaseVo.type){
                       case PropTypeEnum.MAIN_WEAPON: uvo.equipItems[PropTypeEnum.MAIN_WEAPON] = pVo;break;
                       case PropTypeEnum.BODY: uvo.equipItems[PropTypeEnum.BODY] = pVo;break;
                       case PropTypeEnum.HEAD: uvo.equipItems[PropTypeEnum.HEAD] = pVo;break;
                       case PropTypeEnum.HAND: uvo.equipItems[PropTypeEnum.HAND] = pVo;break;
                       case PropTypeEnum.FEET: uvo.equipItems[PropTypeEnum.FEET] = pVo;break;
                       case PropTypeEnum.PANTS: uvo.equipItems[PropTypeEnum.PANTS] = pVo;break;
                       case PropTypeEnum.RING: uvo.equipItems[PropTypeEnum.RING] = pVo;break;
                       case PropTypeEnum.NECK: uvo.equipItems[PropTypeEnum.NECK] = pVo;break;
                   }
               }
                uvo.name=vo.name!=null?vo.name: Model.NameBaseMap.get((int)(Math.random()*Model.NameBaseMap.size())).name;

            allSantchUsers[vo.level].add(uvo);
        }

    }
}
