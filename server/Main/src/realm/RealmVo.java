package realm;

import gluffy.utils.JkTools;
import protocol.CycloneUpPVo;
import protocol.DanPVo;
import protocol.RealmPVo;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;

public class RealmVo {
    public byte realm = 1;
    public byte dan = 1;
    public byte cyclone;
    public byte cycloneUpDan = 1;
    public HashSet<Byte> cycloneUp = new HashSet<>();
    public HashMap<Byte,DanPVo> danMap = new HashMap<>();

    public RealmPVo toRealmPVo(){
        RealmPVo realmPVo = new RealmPVo();
        realmPVo.realm = realm;
        realmPVo.dan = dan;
        realmPVo.cyclone = cyclone;
        realmPVo.cycloneUpPVo = new CycloneUpPVo();
        realmPVo.cycloneUpPVo.dan = cycloneUpDan;
        realmPVo.cycloneUpPVo.cycloneUp = cycloneUp;
        realmPVo.danPVo = danMap.values();
        return realmPVo;
    }

    public void fromBytes(ByteBuffer bytes) {
        realm=bytes.get();
        dan=bytes.get();
        cyclone=bytes.get();
        cycloneUpDan=bytes.get();
        int cycloneUpCount = bytes.get();
        for(int i=0;i<cycloneUpCount;i++){
            cycloneUp.add(bytes.get());
        }
        int danCount = bytes.get();
        for(int i=0;i<danCount;i++){
            DanPVo danPVo = new DanPVo();
            danPVo.fromBytes(bytes);
            danMap.put(danPVo.dan,danPVo);
        }
    }

    public void toBytes(ByteBuffer bytes) {
        bytes.put(realm);
        bytes.put(dan);
        bytes.put(cyclone);
        bytes.put(cycloneUpDan);
        bytes.put((byte) cycloneUp.size());
        for(byte index : cycloneUp){
            bytes.put(index);
        }
        bytes.put((byte)danMap.size());
        for(DanPVo danPVo : danMap.values()){
            danPVo.toBytes(bytes);
        }
    }
}
