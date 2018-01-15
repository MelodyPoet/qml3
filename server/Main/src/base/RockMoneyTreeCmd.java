package base;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.BaseRqst;
import protocol.RockMoneyTreeRqst;
import protocol.RockMoneyTreeRspd;
import table.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by admin on 2017/1/18.
 */
public class RockMoneyTreeCmd extends BaseRqstCmd{

    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        RockMoneyTreeRqst rqst = (RockMoneyTreeRqst) baseRqst;
        rockMoneyTree(client,user);
    }

    private void rockMoneyTree(Client client,User user){
        int count = user.getUserData(UserDataEnum.MONEY_TREE_COUNT);
        if(count<=0)return;
        VipBaseVo vipBaseVo = Model.VipBaseMap.get((int)client.passportVo.vip);
        if(vipBaseVo==null)return;
        int used = vipBaseVo.moneytreeCount - count;
        ArrayList<CommCostBaseVo> costList = Model.CommCostBaseMap.get((int)ModuleUIEnum.MONEYTREE);
        if(costList == null)return;
        CommCostBaseVo costBaseVo = costList.get(used);
        if(costBaseVo == null)return;
        if(user.costUserDataAndPropList(costBaseVo.costUserdata,true,ReasonTypeEnum.ROCK_MONEY_TREE,null)==false)return;
        byte rate=1;
        byte group=0,index=0;
        byte size = 10;//倍率单个数组长度
        int rateFlag = user.getUserData(UserDataEnum.MONEY_TREE_NEXT_TIME);
        int[] rateArr = Model.GameSetBaseMap.get(19).intArray;
        int groupCount = rateArr.length/10;
        if(rateFlag == 0){
            Random random = new Random();
            group = (byte) random.nextInt(groupCount);
            rate = (byte) rateArr[group*size];
            rateFlag = group + index * size + 1;
        }else{
            group = (byte)(rateFlag%10-1);//保存时加1，取的时候-1
            index = (byte)(rateFlag/10+1);//取下一个
            rateFlag = group + index * size + 1;
            if(groupCount<=group){
                group=0;
                index=0;
                rateFlag = 0;
            }
            rate = (byte) rateArr[group*size+index];
            if(index == size -1){//本组最后一个
                rateFlag = 0;
            }
        }
        user.setUserData(UserDataEnum.MONEY_TREE_COUNT,count-1,true);
        user.setUserData(UserDataEnum.MONEY_TREE_NEXT_TIME, rateFlag,true);
        user.addUserData(UserDataEnum.MONEY,Model.GameSetBaseMap.get(16).intValue*rate,true,ReasonTypeEnum.ROCK_MONEY_TREE);
        user.activationModel.progressBuyAct(MissionConditionEnum.MONEY_TREE,0);
        new RockMoneyTreeRspd(client,rate);
    }
}
