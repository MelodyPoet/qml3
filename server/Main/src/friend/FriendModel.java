package friend;

import comm.CacheUserVo;
import comm.Client;
import comm.Model;
import comm.User;
import gang.GangUserVo;
import gang.GangVo;
import gluffy.comm.BaseBlobDeal;
import gluffy.utils.JkTools;
import sqlCmd.AllSql;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by admin on 2017/4/5.
 */
public class FriendModel extends BaseBlobDeal{
    public static HashMap<Long,HashMap<Long,RelationVo>> myAttentionMap = new HashMap<>();
    public static HashMap<Long,HashMap<Long,RelationVo>> attentionMeMap = new HashMap<>();
    public HashSet<Long> recommendList;
    public HashSet<Long> teamedSet;
    public ArrayList<Long> teamedList;
    public ArrayList<FriendSysInfoVo> sysInfoList;
    public HashMap<Long,Integer> userTimeMap;
    public int lastNewAttentionCount;
    public int lastNewSysInfoCount;
    public int lastAttentionCount;
    public int lastNewGiftCount;
    public long lastSysInfoID;
    public byte canPickCount;
    public byte canReceiveCount;
    public HashSet<Long> pickUser = new HashSet<>();
    public HashSet<Long> giveUser = new HashSet<>();
    public ArrayList<FriendGiftVo> giftList = new ArrayList<>();
    public static int TYPE_COUNT = 3;
    public boolean loadSql;
    public User user;

    public FriendModel(User user){
        this.user = user;
        recommendList = new HashSet<>();
        teamedSet = new HashSet<>();
        teamedList = new ArrayList<>();
        sysInfoList = new ArrayList<>();
        userTimeMap = new HashMap<>();
        loadSql = false;
    }

    public static Set<Long> getMyAttention(long userID){
        Set<Long> myAttention = new HashSet<>();
        if(myAttentionMap.containsKey(userID)){
            HashMap<Long,RelationVo> map = myAttentionMap.get(userID);
            myAttention = map.keySet();
        }
        return myAttention;
    }

    public static Set<Long> getAttentionMe(long userID){
        Set<Long> attentionMe = new HashSet<>();
        if(attentionMeMap.containsKey(userID)){
            HashMap<Long,RelationVo> map = attentionMeMap.get(userID);
            attentionMe = map.keySet();
        }
        return attentionMe;
    }

    public static void addAttention(long meId, long otherId, RelationVo relationVo){
        if(myAttentionMap.containsKey(meId)){
            HashMap<Long,RelationVo> map = FriendModel.myAttentionMap.get(meId);
            map.put(otherId,relationVo);
        }else{
            HashMap<Long,RelationVo> map = new HashMap<>();
            map.put(otherId,relationVo);
            FriendModel.myAttentionMap.put(meId,map);
        }
        if(attentionMeMap.containsKey(otherId)){
            HashMap<Long,RelationVo> map = FriendModel.attentionMeMap.get(otherId);
            map.put(meId,relationVo);
        }else{
            HashMap<Long,RelationVo> map = new HashMap<>();
            map.put(meId,relationVo);
            FriendModel.attentionMeMap.put(otherId,map);
        }
    }

    public static void delAttention(long meId, long otherId){
        if(myAttentionMap.containsKey(meId)){
            HashMap<Long,RelationVo> map = FriendModel.myAttentionMap.get(meId);
            if(map.containsKey(otherId)){
                map.remove(otherId);
            }
        }

        if(attentionMeMap.containsKey(otherId)){
            HashMap<Long,RelationVo> map = FriendModel.attentionMeMap.get(otherId);
            if(map.containsKey(meId)){
                map.remove(meId);
            }
        }
    }

    public static boolean isMyAttention(long me,long other){
        if(myAttentionMap.containsKey(me)){
            HashMap<Long,RelationVo> map = myAttentionMap.get(me);
            if(map.containsKey(other))return true;
        }
        return false;
    }

    public static boolean isAttentionMe(long me,long other){
        if(attentionMeMap.containsKey(me)){
            HashMap<Long,RelationVo> map = attentionMeMap.get(me);
            if(map.containsKey(other))return true;
        }
        return false;
    }



    public static RelationVo getRelationVo(HashMap<Long,HashMap<Long,RelationVo>> userMap,long me,long other){
        RelationVo relationVo = null;
        if(userMap.containsKey(me)){
            HashMap<Long,RelationVo> map = userMap.get(me);
            if(map.containsKey(other)){
                relationVo = map.get(other);
            }
        }
        return relationVo;
    }

    public void getRecommend(boolean isClear){
        if(isClear){
            recommendList.clear();
            for(int i=0;i<FriendModel.TYPE_COUNT;i++){
                recommend(i,FriendModel.TYPE_COUNT);
            }
        }
        recommend(-1, Model.GameSetBaseMap.get(31).intArray[3] - recommendList.size());
        saveSqlData();
    }

    private synchronized void recommend(int type,int count){
        if(count <= 0)return;
        switch (type){
            case -1:
            case 0:
                if(user.cacheUserVo.gang.gangVo != null){
                    GangVo gangVo = user.cacheUserVo.gang.gangVo;
                    for(GangUserVo gangUserVo : gangVo.users.values()){
                        if(!fitRecommend(type,gangUserVo.cacheUserVo))continue;
                        recommendList.add(gangUserVo.cacheUserVo.guid);
                        count--;
                        if(count <= 0)break;
                    }
                }
                if(type >= 0) break;
            case 1:
                Set<Long> attentionMe = getAttentionMe(user.guid);
                if(attentionMe.size() > 0){
                    for(long guid : attentionMe){
                        CacheUserVo cacheUserVo = CacheUserVo.allMap.get(guid);
                        if(cacheUserVo == null)continue;
                        if(!fitRecommend(type,cacheUserVo))continue;
                        recommendList.add(cacheUserVo.guid);
                        count--;
                        if(count <= 0)break;
                    }
                }
                if(type >= 0) break;
            case 2:
                if(teamedList.size() > 0){
                    for(long guid : teamedList){
                        CacheUserVo cacheUserVo = CacheUserVo.allMap.get(guid);
                        if(cacheUserVo == null)continue;
                        if(!fitRecommend(type,cacheUserVo))continue;
                        recommendList.add(cacheUserVo.guid);
                        count--;
                        if(count <= 0)break;
                    }
                }
                if(type >= 0) break;
            default:
                for(;type>=-6&&count>0;type--){
                    for(CacheUserVo cacheUserVo : CacheUserVo.allMap.values()){
                        if(!fitRecommend(type,cacheUserVo))continue;
                        recommendList.add(cacheUserVo.guid);
                        count--;
                        if(count <= 0)break;
                    }
                }
        }
    }

    private boolean fitRecommend(int type, CacheUserVo cacheUserVo){
        if(cacheUserVo.guid == user.guid)return false;
        if(recommendList.contains(cacheUserVo.guid))return false;
        if(isMyAttention(user.guid,cacheUserVo.guid))return false;
        if(!Client.isOnLine(cacheUserVo))return false;
        if(type < 0){
            int x = (int)((cacheUserVo.zdl/(user.zdl*1f)-1)*100);
            switch (type){
                case -1:
                    if(x<10)return false;
                    break;
                case -2:
                    if(x<5)return false;
                    break;
                case -3:
                    if(x<0)return false;
                    break;
                case -4:
                    if(x<-5)return false;
                    break;
                case -5:
                    if(x<-10)return false;
                    break;
                case -6:
                    break;
            }
        }
        return true;
    }

    public void addTeamedList(long guid){
        CacheUserVo cacheUserVo = user.cacheUserVo;
        if(cacheUserVo.gang.gangVo == null)return;
        if(cacheUserVo.gang.gangVo.users.containsKey(guid))return;
        if(isAttentionMe(user.guid,guid))return;
        if(teamedSet.contains(guid))return;
        teamedList.add(0,guid);
        teamedSet.add(guid);
        if(teamedList.size() > Model.GameSetBaseMap.get(31).intArray[1]){
            long removeID = teamedList.get(teamedList.size()-1);
            teamedList.remove(teamedList.size()-1);
            teamedSet.remove(removeID);
        }
        saveSqlData();
    }

    public static void addSysInfo(byte type,CacheUserVo active,CacheUserVo cacheUserVo){
        FriendSysInfoVo friendSysInfoVo = new FriendSysInfoVo();
        friendSysInfoVo.attentionID = active.guid;
        friendSysInfoVo.attentionedID = cacheUserVo.guid;
        friendSysInfoVo.uname = active.name;
        friendSysInfoVo.type = type;
        friendSysInfoVo.createTime = JkTools.getGameServerTime(null);
        AllSql.friendLogSql.insertNew(friendSysInfoVo);
        Client client = Client.getOne(cacheUserVo);
        if(client != null){
            ArrayList<FriendSysInfoVo> sysInfoList = client.currentUser.friendModel.sysInfoList;
            int newSysInfoCount = 0;
            Iterator it = sysInfoList.iterator();
            while(it.hasNext()){
                FriendSysInfoVo vo = (FriendSysInfoVo) it.next();
                if(vo.guid > client.currentUser.friendModel.lastSysInfoID)newSysInfoCount++;
                if(vo.attentionID != friendSysInfoVo.attentionID)continue;
                if(vo.guid > client.currentUser.friendModel.lastSysInfoID)newSysInfoCount--;
                it.remove();
                AllSql.friendLogSql.delete(vo.guid);
            }
            sysInfoList.add(0,friendSysInfoVo);
            newSysInfoCount++;
            if(sysInfoList.size() >= Model.GameSetBaseMap.get(31).intArray[2]){
                FriendSysInfoVo vo = sysInfoList.get(sysInfoList.size() - 1);
                if(vo.guid > client.currentUser.friendModel.lastSysInfoID)newSysInfoCount--;
                sysInfoList.remove(sysInfoList.size() - 1);
                AllSql.friendLogSql.delete(vo.guid);
            }
            cacheUserVo.newSysInfoCount = newSysInfoCount;
        }
    }
    @Override
    protected byte[] saveDataBytes() {
        byte[] bytes = new byte[(teamedList.size()+recommendList.size()+pickUser.size()+giveUser.size())*8+21];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.put((byte)recommendList.size());
        for(long guid : recommendList){
            buffer.putLong(guid);
        }
        buffer.putShort((short) teamedList.size());
        for(long guid : teamedList){
            buffer.putLong(guid);
        }
        buffer.putInt(lastAttentionCount);
        buffer.putLong(lastSysInfoID);
        buffer.put(canPickCount);
        buffer.put(canReceiveCount);
        buffer.putShort((short)pickUser.size());
        for(long userID : pickUser){
            buffer.putLong(userID);
        }
        buffer.putShort((short)giveUser.size());
        for(long userID : giveUser){
            buffer.putLong(userID);
        }
        return bytes;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {
        if(buffer == null)return;
        byte recommendSize = buffer.get();
        for(int i=0;i<recommendSize;i++){
            recommendList.add(buffer.getLong());
        }
        short teamedSize = buffer.getShort();
        for(int i=0;i<teamedSize;i++){
            long guid = buffer.getLong();
            teamedList.add(guid);
            teamedSet.add(guid);
        }
        lastAttentionCount = buffer.getInt();
        lastSysInfoID = buffer.getLong();
        canPickCount = buffer.get();
        canReceiveCount = buffer.get();
        int pickSize = buffer.getShort();
        for(int i=0;i<pickSize;i++){
            pickUser.add(buffer.getLong());
        }
        int giveSize = buffer.getShort();
        for(int i=0;i<giveSize;i++){
            giveUser.add(buffer.getLong());
        }
    }

    @Override
    public void saveSqlData() {
        AllSql.userSql.update(user,AllSql.userSql.FIELD_FRIEND,saveData());
    }

    @Override
    public void unloadUser() {
        user = null;
    }
}
