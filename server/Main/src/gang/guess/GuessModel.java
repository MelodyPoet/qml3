package gang.guess;

import comm.Model;
import gang.Gang;
import gang.GangVo;
import protocol.GangGuessLogPVo;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by admin on 2017/4/19.
 */
public class GuessModel {
    public int guessRandom;
    public int guessMin;
    public int guessMax = Model.GameSetBaseMap.get(38).intArray[1];
    public ArrayList<GangGuessLogPVo> logList = new ArrayList<>();
    public byte guessDouble = 1;
    public byte guessTimes;
    public byte isGetKey;
    public GangGuessLogPVo lastWinner = new GangGuessLogPVo();

    public static void openAll(){
        int[] arr = Model.GameSetBaseMap.get(38).intArray;
        for(GangVo gangVo : Gang.allGangList){
            gangVo.guessModel.open(arr,gangVo.users.size());
        }
    }

    public void open(int[] arr,int count){
        Random random = new Random();
        guessMax = Math.min(count * arr[0],arr[1]);
        guessRandom = random.nextInt(guessMax-1)+1;//开区间
        guessDouble = 1;
        isGetKey = 0;
        logList.clear();
        guessMin = 0;
        guessTimes = (byte)Model.GameSetBaseMap.get(37).intArray[1];
    }

    public void over(){
        isGetKey = 1;
    }
}
