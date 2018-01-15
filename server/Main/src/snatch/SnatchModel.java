package snatch;

import comm.CacheUserVo;
import comm.Model;
import comm.User;
import gluffy.comm.BaseBlobDeal;
import prop.PropModel;
import sqlCmd.PropSql;
import table.PropBaseVo;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by jackie on 14-5-2.
 */
public class SnatchModel extends BaseBlobDeal {

    public static int needLvl=1;
    public  static HashMap<Integer,HashSet<CacheUserVo>[]> allItemOwnners=new HashMap<>();

     public ArrayList<CacheUserVo> targets;
    public int propBaseID;

    public SnatchModel(User user) {
        this.user=  user;
targets=new ArrayList<>();
    }

    public static void init(){
        //loadAll();
    }
public  static void loadAll(){
 long   tim =System.currentTimeMillis();
    for (int i = 0; i <100 ; i++) {
        int baseID=12400+i;
        if(Model.PropBaseMap.containsKey(baseID)==false)continue;
   PropBaseVo baseVo= Model.PropBaseMap.get(baseID);
        HashSet<Long> usersID= PropSql.loadOwners(baseID, 100);
        HashSet<CacheUserVo>[] userLvls=  allItemOwnners.get(baseID);
        if(userLvls==null){
            userLvls=new HashSet[Model.userLevelMax];
            for (int j = 0; j < Model.userLevelMax; j++) {
                userLvls[j]=new HashSet<>();
            }
            for (long userID :usersID   ) {
                CacheUserVo cuserVo= CacheUserVo.allMap.get(userID);
                 userLvls[cuserVo.level].add(cuserVo);

            }
            //
            allItemOwnners.put(baseID,userLvls);
        }
    }
    if(Model.devCode == 1) {
        System.out.println("snatchTime" + (System.currentTimeMillis() - tim));
    }
}
    public static  void addOwnner(int baseID,CacheUserVo ownner) {
        if(PropModel.isSnatchItem(baseID)==false)return;
        HashSet<CacheUserVo>[] userLvls = allItemOwnners.get(baseID);
        if (userLvls == null) {

            userLvls = new HashSet[Model.userLevelMax+1];
            allItemOwnners.put(baseID,userLvls);
            for (int j = 0; j < Model.userLevelMax+1; j++) {
                userLvls[j] = new HashSet<>();
            }


        }
        userLvls[ownner.level].add(ownner);
    }
    public static  void deleteOwnner(int baseID,CacheUserVo ownner){
        if(PropModel.isSnatchItem(baseID)==false)return;
        HashSet<CacheUserVo>[] userLvls = allItemOwnners.get(baseID);
        userLvls[ownner.level].remove(ownner);
    }

     @Override
    protected byte[] saveDataBytes() {
         return null;
      //  if(user.cacheUserVo.arenaScore<=0)return null;
//        byte[] bytes=new byte[1+4+8+4];
//        ByteBuffer buffer=ByteBuffer.wrap(bytes);
//        buffer.put(canFightCount);
//        buffer.putInt(0);
//        buffer.putLong(0);
//        buffer.putInt(user.cacheUserVo.arenaScore);
//        return bytes;

    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {

        if(buffer==null)return;
//        canFightCount=buffer.get();
//        buffer.getInt();
//        buffer.getLong();
//
//        user.cacheUserVo.arenaScore=buffer.getInt();
    }

    @Override
    public void saveSqlData() {
       // AllSql.userSql.update(user, AllSql.userSql.FIELD_ARENA, saveData());

    }

    @Override
    public void unloadUser() {
        user=null;
    }



}


