package modules.scene;

import qmshared.RedisTableBase;

public class RedisTableFightMap extends RedisTableBase {
    public  static  String layoutIndex="layoutIndex";
    public  static  String fightGroupIndex="fightGroupIndex";
    public RedisTableFightMap(long guid) {
        super(guid, "fightMap");
    }
}
