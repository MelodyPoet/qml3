package redness;

import comm.CacheUserVo;
import protocol.SimpleUserPVo;
import protocol.SkillUserPVo;
import utils.UserVoAdapter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by jackie on 2017/3/22.
 */
public class RP_RoomVo {
    public int roomID;
    public CacheUserVo ownner;
    public CacheUserVo guest;
    public byte isInGame;
    public  String pwd;
    public boolean guestReady;
    public int mapID;
    public int needRobotTime;
    public CacheUserVo getTeamer(CacheUserVo self){
        if(self==ownner)return guest;
        else return ownner;
    }


    public Collection<SimpleUserPVo> getUsers() {
        ArrayList<SimpleUserPVo > users=new ArrayList<>();
        if(ownner!=null)
        users.add(UserVoAdapter.toSimpleUserPVo (ownner));
        if(guest!=null)
            users.add(UserVoAdapter.toSimpleUserPVo (guest));
        return users;
    }
    public ArrayList<SkillUserPVo> getSkillUsers() {
        ArrayList<SkillUserPVo > users=new ArrayList<>();
        if(ownner!=null)
            users.add(UserVoAdapter.toSkillUserPVo (ownner));
        if(guest!=null)
            users.add(UserVoAdapter.toSkillUserPVo (guest));
        return users;
    }
}
