package heroTag;

import comm.CacheUserVo;
import comm.Model;
import comm.User;
import gluffy.utils.JkTools;
import protocol.HeroTagPVo;
import sqlCmd.AllSql;
import table.HeroTagBaseVo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by admin on 2017/8/2.
 */
public class HeroTagModel{
    public User user;
    public HashMap<Short,HeroTagVo> zdlTagMap = new HashMap<>();
    public HashMap<Short,HeroTagVo> activeTagMap = new HashMap<>();

    public HeroTagModel(User user) {
        this.user = user;
    }

    public static void addHeroTag(int[] arr, int index, CacheUserVo cacheUserVo){
        for(int tagID : arr){
            HeroTagBaseVo heroTagBaseVo = Model.HeroTagBaseMap.get(tagID);
            if(JkTools.canCompleteParams(heroTagBaseVo.conditionParams,index,index) == true){
                HeroTagVo heroTagVo = new HeroTagVo();
                heroTagVo.userID = cacheUserVo.guid;
                heroTagVo.tagID = (short)tagID;
                heroTagVo.status = 1;
                heroTagVo.deadTime = JkTools.getGameServerTime(null) + heroTagBaseVo.endTime*Model.ONE_DAY_TIME;
                if(cacheUserVo.onlineUser != null){
                    cacheUserVo.onlineUser.heroTagModel.activeTagMap.put(heroTagVo.tagID,heroTagVo);
                    HeroTagPVo heroTagPVo = new HeroTagPVo();
                    heroTagPVo.id = heroTagVo.tagID;
                    heroTagPVo.status = heroTagVo.status;
                    heroTagPVo.endTime = heroTagVo.deadTime;
                    cacheUserVo.addHeroTagList.add(heroTagPVo);
                }
                AllSql.heroTagSql.insertNew(heroTagVo);
            }
        }
    }

    public void flushHeroTag(){
        int now = JkTools.getGameServerTime(user.client);
        Iterator<Map.Entry<Short,HeroTagVo>> itActive = activeTagMap.entrySet().iterator();
        while (itActive.hasNext()){
            HeroTagVo heroTagVo = itActive.next().getValue();
            if(heroTagVo.deadTime == 0)continue;
            if(now > heroTagVo.deadTime){
                itActive.remove();
            }
        }
    }
}
