package base;

import comm.Model;
import gluffy.utils.JkTools;
import prop.PropModel;
import protocol.PropPVo;
import table.DropBaseVo;

import java.util.ArrayList;
import java.util.Random;

public class BaseModel {
    public static void getDropProp(int groupID, int job, ArrayList<PropPVo> dropPropList) {
        if (groupID == 0)
            return ;
//            System.out.println("==========!!!!!!!!!!!!====groupID==========="+groupID);
        ArrayList<DropBaseVo> dropList = Model.DropBaseMap.get(groupID);
        int rate = new Random().nextInt(10000);
        for (DropBaseVo dropBaseVo : dropList) {
            rate -= dropBaseVo.rate;
            if (rate < 0) {
                int whiteID = dropBaseVo.propID;
                //-1为与组合
                if(whiteID==-1){
                    for (int newGroupID:dropBaseVo.groupIDList) {
                        getDropProp(newGroupID,job,dropPropList);
                    }
                    return ;
                }
                //-2为 或组合
                if(whiteID==-2){
                  int index=  JkTools.getRandRange(dropBaseVo.groupIDList,10000,2);
                    if(index>-1){
                        getDropProp(dropBaseVo.groupIDList[index+1],job,dropPropList);
                    }

                    return ;
                }
                if(dropBaseVo.propID == 0)return;
                int count = 1;
                if(dropBaseVo.count != null){
                    if(dropBaseVo.count.length == 1){
                        count = dropBaseVo.count[0];
                    }else if(dropBaseVo.count.length == 2){
                        count = JkTools.getRandBetween(dropBaseVo.count[0],dropBaseVo.count[1]);
                    }else{
                        int index = JkTools.getRandRange(dropBaseVo.count,100,2);
                        if(index > -1){
                            count = dropBaseVo.count[index+1];
                        }
                    }
                }
//                System.out.println("==========!!!!!!!!!!!!=whiteID : "+whiteID+"===count==========="+count);
                if (dropBaseVo.qualityRate==null) {
                    if(job>0&&PropModel.isEquip(whiteID)){
                        whiteID=whiteID%1000+job*1000;
                    }
                    addPropToTempList(whiteID,count,dropPropList);
                    return ;
                }
                int qualityRate = new Random().nextInt(10000);
                for (int i = dropBaseVo.qualityRate.length-1; i > 0; i--) {
                    qualityRate -= dropBaseVo.qualityRate[i];
                    if (qualityRate < 0) {
                        whiteID= whiteID + i;
                        if(job>0&& PropModel.isEquip(whiteID)){
                            whiteID=whiteID%1000+job*1000;
                        }
                        addPropToTempList(whiteID,count,dropPropList);
                        return   ;
                    }

                }
                if(job>0&&PropModel.isEquip(whiteID)){
                    whiteID=whiteID%1000+job*1000;
                }
                addPropToTempList(whiteID,count,dropPropList);
                return ;
            }

        }

    }
    private  static void addPropToTempList(int baseID,int count, ArrayList<PropPVo> dropPropList){
        if(baseID<=0)return  ;
        if(count<=0)count=1;
        PropPVo propPVo = new PropPVo();
        propPVo.count=count;
        propPVo.baseID = baseID;

        dropPropList.add(propPVo);
    }
    public static ArrayList<PropPVo> getDropProps(int groupID,int job) {
        return getDropProps(new int[]{groupID},job);
    }
    public static ArrayList<PropPVo> getDropProps(int[] groups,int job) {
        ArrayList<PropPVo> dropList = new ArrayList<>();
        if(groups==null)return dropList;
for (int i=0,len=groups.length;i<len;i++){
             int dropID =groups[i];
//    if(PropModel.isAttributeItem(dropID)) {
//
//        count=groups[++i];
//
//    }
                if (dropID == 0)
                    break;
                getDropProp(dropID,job,dropList);

            }
        return dropList;
    }
}