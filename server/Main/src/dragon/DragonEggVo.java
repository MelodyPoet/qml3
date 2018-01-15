package dragon;

import protocol.DragonEggPVo;

import java.nio.ByteBuffer;

/**
 * Created by admin on 2016/11/17.
 */
public class DragonEggVo {
    public byte tempID;
    public byte eggID;
    public byte count;
    public byte total;
    public int time;

    public DragonEggPVo DragonEggVoToPVo(){
        DragonEggPVo dragonEggPVo = new DragonEggPVo();
        dragonEggPVo.tempID = tempID;
        dragonEggPVo.eggID = eggID;
        dragonEggPVo.count = count;
        dragonEggPVo.total = total;
        dragonEggPVo.time = time;
        dragonEggPVo.isTouch = 0;
        return dragonEggPVo;
    }

    public void fromBytes(ByteBuffer bytes) {
        tempID=bytes.get();
        eggID=bytes.get();
        count=bytes.get();
        total=bytes.get();
        time=bytes.getInt();
    }

    public void toBytes(ByteBuffer bytes) {
        bytes.put(tempID);
        bytes.put(eggID);
        bytes.put(count);
        bytes.put(total);
        bytes.putInt(time);
    }
}
