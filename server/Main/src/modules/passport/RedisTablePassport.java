package modules.passport;

import qmshared.RedisTableBase;

public class RedisTablePassport extends RedisTableBase {
    public static String uname="uname";
    public static String level="level";
    public static String mapID="mapID";
    public RedisTablePassport(long guid) {
        super(guid,"passport");
    }
}
