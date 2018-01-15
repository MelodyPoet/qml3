package redness;

import comm.CacheUserVo;
import comm.Model;
import comm.User;
import gluffy.comm.BaseBlobDeal;
import gluffy.utils.JkTools;
import protocol.*;
import table.MapBaseVo;
import table.UserDataEnum;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static comm.MemoryRobot.allSantchUsers;

public class RednessModel extends BaseBlobDeal  {
    public static  int tempID;
    public static   Map<Integer, RP_RoomVo> allRooms=new HashMap<>();//  Map<roomid, RP_RoomVo>
    public static CampUserPVo campUserPVo = null;
    public static ArrayList<RednessWinnerPVo> lastRednessRank = new ArrayList<>();
    public static long lastMailID;
    public static short lastResetDay;
    public static int[] rednessRankRewardArr;
    private User user;
public  RP_RoomVo myRoom;
    public boolean udpisReady;
    public int startGameTime;
    public ArrayList<InviteMePVo> inviteMeList;
    public static int NEED_ROBOT_TIME = 20;
    public static int NEED_ROBOT_READY = 10;

    public RednessModel(User user) {


        this.user = user;
        inviteMeList = new ArrayList<>();
    }

    public boolean isCanEnter(int mapID){
        MapBaseVo mapBaseVo = Model.MapBaseMap.get(mapID);
        if(mapBaseVo == null)return false;
        if(mapBaseVo.countLimit!=0) {
            int keyMapID =  mapBaseVo.ID;
            if (mapBaseVo.countLimit < 0) {
                keyMapID = (short) -mapBaseVo.countLimit;
            }
            MapEnteredPVo sspvo = user.mapEnteredMap.get((short)keyMapID);
            if(sspvo==null)return true;
            MapBaseVo groupMap=mapBaseVo;
            if(mapBaseVo.countLimit<0)groupMap=Model.MapBaseMap.get(keyMapID);
            if(groupMap.countLimit<=sspvo.value)return false;
        }
        return true;
    }
 public   void  checkForRobot(){
     if(myRoom==null)return;
     if(myRoom.guest==null){
         if( myRoom.needRobotTime>0&&JkTools.getGameServerTime(user.client)>myRoom.needRobotTime) {
             int targetLvl=JkTools.getRandBetween( Math.max(user.getUserData(UserDataEnum.LEVEL)-5,5), Math.min(user.getUserData(UserDataEnum.LEVEL)+5,allSantchUsers.length));
             myRoom.guest=  allSantchUsers[targetLvl].get(JkTools.getRandBetween(0, allSantchUsers[targetLvl].size()));
             myRoom.guestReady=true;
             new RP_RoomEnterRspd(user.client,myRoom.roomID,myRoom.mapID,myRoom.ownner.guid,myRoom.pwd, myRoom.getUsers());

             new RP_RoomGameReadyRspd(user.client,true);
         }
     }else{
         CacheUserVo teamer = myRoom.getTeamer(user.cacheUserVo);
         if(teamer != null){
             if(teamer.isRobot()&&myRoom.needRobotTime>0&&JkTools.getGameServerTime(user.client)>myRoom.needRobotTime) {
                 myRoom.guestReady=true;
                 myRoom.needRobotTime = 0;
                 new RP_RoomGameReadyRspd(user.client,true);
             }
         }
     }
 }

    @Override
    protected byte[] saveDataBytes() {
        return null;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {

    }

    @Override
    public void saveSqlData() {

    }

    @Override
    public void unloadUser() {

    }

}
