package gang.guess;

import comm.BaseRqstCmd;
import comm.Client;
import comm.Model;
import comm.User;
import gameset.GameSetModel;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.GuessDoubleRspd;
import protocol.ServerTipRspd;
import table.ReasonTypeEnum;
import table.UserDataEnum;

/**
 * Created by admin on 2017/4/19.
 */
public class GuessDoubleCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(gangVo == null)return;
        GuessModel guessModel = gangVo.guessModel;
        if(!GameSetModel.checkTime(3))return;
        if(guessModel.guessTimes <= 0){
            new ServerTipRspd(client,(short)54,null);
            new GuessDoubleRspd(client,guessModel.guessDouble,guessModel.guessTimes);
            return;
        }
        int[] arr = Model.GameSetBaseMap.get(37).intArray;
        if(user.costUserDataAndProp(UserDataEnum.DIAMOND,arr[0],true, ReasonTypeEnum.GUESS_DOUBLE) == false)return;
        guessModel.guessDouble = (byte)arr[arr.length - guessModel.guessTimes];
        guessModel.guessTimes--;
        user.propModel.addListToBag(Model.GameSetBaseMap.get(39).intArray,ReasonTypeEnum.GUESS_DOUBLE);
        user.cacheUserVo.guessDouble = guessModel.guessDouble;
        new GuessDoubleRspd(client,guessModel.guessDouble,guessModel.guessTimes);
    }
}
