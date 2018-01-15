package prop;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.EquipAttrReplaceRqst;
import protocol.EquipAttrReplaceRspd;
import protocol.PropPVo;
import sqlCmd.AllSql;
import table.PropBaseVo;

import java.util.ArrayList;

/**
 * Created by admin on 2017/7/6.
 */
public class EquipAttrReplaceCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        EquipAttrReplaceRqst rqst = (EquipAttrReplaceRqst)baseRqst;
        PropPVo mainPVo = user.propModel.getEquipByIndex(rqst.mainType);
        if(mainPVo == null)return;
        PropPVo equipPVo = user.propModel.getPropInBag(rqst.equipTempID);
        if(equipPVo == null)return;
        PropBaseVo mainBase = Model.PropBaseMap.get(mainPVo.baseID);
        if(mainBase == null)return;
        PropBaseVo equipBase = Model.PropBaseMap.get(equipPVo.baseID);
        if(equipBase == null)return;
        if(mainBase.type != equipBase.type)return;
        if(mainBase.quality <2 || equipBase.quality < 1)return;
        if(mainBase.level < equipBase.level){
            return;
        }else if(mainBase.level == equipBase.level){
            if(mainBase.quality < equipBase.quality)return;
        }
        int mainCount = 0;
        for(short value: mainPVo.exAttribute){
            if(value <= 0)continue;
            mainCount++;
        }
        int equipCount = 0;
        for(short value: equipPVo.exAttribute){
            if(value <= 0)continue;
            equipCount++;
        }
        ArrayList<Short> tempList = null;
        if(mainCount > equipBase.extrAttCountMax){
            tempList = new ArrayList<>(mainPVo.exAttribute);
            for(int i=equipBase.extrAttCountMax;i<tempList.size();i++){
                tempList.set(i,(short)0);
            }
            mainPVo.exAttribute = equipPVo.exAttribute;
            equipPVo.exAttribute = tempList;
        }else if(equipCount > mainBase.extrAttCountMax){
            if(rqst.locks.size() > mainBase.extrAttCountMax)return;
            tempList = new ArrayList<>(equipPVo.exAttribute);
            for(byte i=0;i<tempList.size();i++){
                if(!rqst.locks.contains(i)){
                    tempList.set(i,(short)0);
                }
            }
            equipPVo.exAttribute = mainPVo.exAttribute;
            mainPVo.exAttribute = tempList;
        }else{
            tempList = new ArrayList<>(mainPVo.exAttribute);
            mainPVo.exAttribute = equipPVo.exAttribute;
            equipPVo.exAttribute = tempList;
        }
        AllSql.propSql.update(AllSql.propSql.FIELD_EXATTRIBUTERND, AllSql.propSql.exAttributeRndSqlData(mainPVo, true, null), mainPVo.tempID);
        AllSql.propSql.update(AllSql.propSql.FIELD_EXATTRIBUTERND, AllSql.propSql.exAttributeRndSqlData(equipPVo, true, null), equipPVo.tempID);
        new EquipAttrReplaceRspd(client,equipPVo.exAttribute,mainPVo.exAttribute);
    }
}
