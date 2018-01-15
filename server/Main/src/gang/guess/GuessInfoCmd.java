package gang.guess;

import comm.BaseRqstCmd;
import comm.Client;
import comm.User;
import gameset.GameSetModel;
import gang.GangVo;
import gluffy.comm.BaseRqst;
import protocol.GuessInfoRspd;

/**
 * Created by admin on 2017/4/19.
 */
public class GuessInfoCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GangVo gangVo = user.cacheUserVo.gang.gangVo;
        if(gangVo == null)return;
        GuessModel guessModel = gangVo.guessModel;
        if(!GameSetModel.checkTime(3)){
//            new GuessInfoRspd(client,0, Model.GameSetBaseMap.get(38).intArray[1],(byte) 1,(byte) 0,new ArrayList<GangGuessLogPVo>(),guessModel.lastWinner,guessModel.isGetKey);
            new GuessInfoRspd(client,guessModel.guessMin,guessModel.guessMax,guessModel.guessDouble,guessModel.guessTimes,guessModel.logList,guessModel.lastWinner,guessModel.isGetKey);
            return;
        }
        new GuessInfoRspd(client,guessModel.guessMin,guessModel.guessMax,guessModel.guessDouble,guessModel.guessTimes,guessModel.logList,guessModel.lastWinner,guessModel.isGetKey);
        user.cacheUserVo.addGuessLogList.clear();
    }
}
