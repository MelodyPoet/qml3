package rank;

import arena.RankUserArenaVo;
import comm.CachePassportVo;
import comm.CacheUserVo;
import comm.Model;
import endless.wormNest.RankUserWormNestVo;
import endless.wormNest.WormNestModel;
import gluffy.utils.RankSortedList;
import heroTag.HeroTagModel;
import mine.RankMineVo;
import redness.RankRednessVo;
import sqlCmd.AllSql;
import table.GameSetBaseVo;

import java.util.*;

/**
 * Created by jackie on 14-5-2.
 */
public class RankModel {
    public static RankSortedList<RankUserZdlVo> rankZdlList;
    public static RankSortedList<RankUserFbVo> rankFbList;
    public static RankSortedList<RankUserArenaVo> rankArenaList;
    public static RankSortedList<RankUserWormNestVo> rankWormNestList;
    public static RankSortedList<RankRednessVo> rankRednessList;
    public static RankSortedList<RankMineVo> rankMineList;

    public static void init(){

        rankZdlList =new RankSortedList<>();
        rankFbList=new RankSortedList<>();
        rankArenaList=new RankSortedList<>();
        rankWormNestList = new RankSortedList<>();
        rankRednessList = new RankSortedList<>();
        rankMineList = new RankSortedList<>();
        AllSql.userSql.initRank();
        ArrayList<CacheUserVo> tempList=new ArrayList<>();
        for (Iterator<CachePassportVo> iterator = CachePassportVo.guidMap.values().iterator(); iterator.hasNext(); ) {
            for ( CacheUserVo userVo : iterator.next().userMap.values()) {
                tempList.add(userVo);
            }
        }
        Collections.sort(tempList,new Comparator<CacheUserVo>() {
            @Override
            public int compare(CacheUserVo o1, CacheUserVo o2) {
                return o2.zdl-o1.zdl;
            }
        });
        for (CacheUserVo userVo : tempList) {
            rankZdlList.addEnd(new RankUserZdlVo(userVo));
        }
        Collections.sort(tempList,new Comparator<CacheUserVo>() {
            @Override
            public int compare(CacheUserVo o1, CacheUserVo o2) {
                return o2.fbScore-o1.fbScore;
            }
        });
        for (CacheUserVo userVo : tempList) {
            if(userVo.fbScore>0) {
                rankFbList.addEnd(new RankUserFbVo(userVo));
            }
        }

        Collections.sort(tempList,new Comparator<CacheUserVo>() {
            @Override
            public int compare(CacheUserVo o1, CacheUserVo o2) {
                if(o2.wormNestMaxFloor-o1.wormNestMaxFloor==0) {
                    return o1.wormNestMaxFloorTime - o2.wormNestMaxFloorTime;
                }else{
                    return o2.wormNestMaxFloor-o1.wormNestMaxFloor;
                }
            }
        });

        for (CacheUserVo userVo : tempList) {
            if(userVo.wormNestMaxFloor>0) {
                RankUserWormNestVo wormNestVo = new RankUserWormNestVo(userVo);
                rankWormNestList.addEnd(wormNestVo);
                wormNestVo.updataFloorUserCount();
                WormNestModel.wormNestMap.put(userVo.guid,wormNestVo);
//                System.out.println("========= ID : " + userVo.guid + " ========maxFloor : " + userVo.wormNestMaxFloor + " ======time : " + userVo.wormNestMaxFloorTime);
            }
        }
    }

    public static void recordZDLRank(){
        GameSetBaseVo gameSetBaseVo = Model.GameSetBaseMap.get(71);
        for(RankUserZdlVo vo : rankZdlList.list){
            if(vo.orderIndex < gameSetBaseVo.intValue){
                HeroTagModel.addHeroTag(gameSetBaseVo.intArray,vo.orderIndex+1,vo.cacheUserVo);
            }else{
                break;
            }
        }
    }

}


