package endless.wormNest;

import comm.CacheUserVo;
import comm.RankListItem;
import comm.User;
 import rank.RankModel;
import table.UserDataEnum;

import java.util.Map;

/**
 * Created by admin on 2016/7/11.
 */
public class RankUserWormNestVo extends RankListItem {

    public RankUserWormNestVo(CacheUserVo userVo) {
        cacheUserVo = userVo;
    }

    public static void toRank(User user)    {
        int maxFloor = user.getUserData(UserDataEnum.WormNestMaxFloor);
        int size = RankModel.rankWormNestList.size();
        RankUserWormNestVo wormNestVo = new RankUserWormNestVo(user.cacheUserVo);
        if (WormNestModel.wormNestMap.containsKey(user.guid)) {
            RankUserWormNestVo rankUserWormNestVo = WormNestModel.wormNestMap.get(user.guid);
            if(maxFloor - rankUserWormNestVo.cacheUserVo.wormNestMaxFloor != 1)return;
            int index = 0;
            for(Map.Entry<Integer,Integer> item: WormNestModel.floorUserCountMap.entrySet()){
                if(item.getKey()<maxFloor)continue;
                index += item.getValue();
            }
            RankModel.rankWormNestList.list.remove(rankUserWormNestVo.orderIndex);
            RankModel.rankWormNestList.list.add(index, wormNestVo);
            for (int i = index; i <= rankUserWormNestVo.orderIndex; i++) {
                RankModel.rankWormNestList.list.get(i).orderIndex = i;
            }
            WormNestModel.wormNestMap.put(user.guid,wormNestVo);
            user.cacheUserVo.wormNestMaxFloor = maxFloor;
            wormNestVo.updataFloorUserCount();
        } else {
            if (size < WormNestModel.WORM_NEST_RANK_SIZE) {
                RankModel.rankWormNestList.addEnd(wormNestVo);
                user.cacheUserVo.wormNestMaxFloor = maxFloor;
                wormNestVo.updataFloorUserCount();
                WormNestModel.wormNestMap.put(user.guid, wormNestVo);
            } else if (size == WormNestModel.WORM_NEST_RANK_SIZE) {
                if (maxFloor - RankModel.rankWormNestList.get(size - 1).cacheUserVo.wormNestMaxFloor != 1) return;
                int index = size - WormNestModel.floorUserCountMap.get(maxFloor - 1)-1;
                RankModel.rankWormNestList.list.remove(size - 1);
                RankModel.rankWormNestList.list.add(index, wormNestVo);
                for (int i = index; i < size; i++) {
                    RankModel.rankWormNestList.list.get(i).orderIndex = i;
                }
                user.cacheUserVo.wormNestMaxFloor = maxFloor;
                wormNestVo.updataFloorUserCount();
                WormNestModel.wormNestMap.put(user.guid, wormNestVo);
            }
        }
    }

    public void updataFloorUserCount() {
        int oldMaxFloor = cacheUserVo.wormNestMaxFloor - 1;
        if (WormNestModel.wormNestMap.containsKey(cacheUserVo.guid)) {
            if(!WormNestModel.floorUserCountMap.containsKey(oldMaxFloor))return;
            if (WormNestModel.floorUserCountMap.get(oldMaxFloor) == 1) {
                WormNestModel.floorUserCountMap.remove(oldMaxFloor);
            } else {
                WormNestModel.floorUserCountMap.put(oldMaxFloor, WormNestModel.floorUserCountMap.get(oldMaxFloor) - 1);
            }
        }

        if (WormNestModel.floorUserCountMap.containsKey(cacheUserVo.wormNestMaxFloor)) {
            WormNestModel.floorUserCountMap.put(cacheUserVo.wormNestMaxFloor, WormNestModel.floorUserCountMap.get(cacheUserVo.wormNestMaxFloor) + 1);
        } else {
            WormNestModel.floorUserCountMap.put(cacheUserVo.wormNestMaxFloor, 1);
        }
    }

    @Override
    public int orderScore() {
        return cacheUserVo.wormNestMaxFloor;
    }
}
