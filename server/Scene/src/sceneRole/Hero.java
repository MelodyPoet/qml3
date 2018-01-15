package sceneRole;

import comm.Client;
 import protocol.PropPVo;
import protocol.SkillPVo;
import protocol.SkillUserPVo;
import table.SkillBaseVo;

import java.util.Collection;

public class Hero  extends BaseRole {
    SkillUserPVo protoVo=new SkillUserPVo();
    public  PropPVo weapon;
    public Collection<SkillPVo>equipSkills;

    public short skin;
    public byte portrait;
public Client client;
    public SkillBaseVo currentSkill;

    public Hero(Client client, SkillUserPVo user) {
        this.client=client;
        baseID =user.baseID;
        tempID =user.tempID;
        posx=user.posx;
        posz=user.posz;
        dir=user.dir;
        level=user.level;
       skin= user.skin;
        weapon=user.weapon;
       equipSkills= user.equipSkills;
     portrait=   user.portrait;
    }

    public SkillUserPVo makProtoVo(){
        protoVo.baseID=(byte)baseID;
        protoVo.tempID=tempID;
        protoVo.posx=posx;
        protoVo.posz=posz;
        protoVo.dir=dir;
        protoVo.level=level;
        protoVo.skin=skin;
        protoVo.weapon=weapon;
        protoVo.equipSkills=equipSkills;
        protoVo.portrait=portrait;

        return protoVo;
    }

    @Override
    public void calculateBaseAttribute() {
        super.calculateBaseAttribute();

    }
}
