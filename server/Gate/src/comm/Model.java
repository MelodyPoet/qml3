package comm;

public class Model {
    public static int GatePort = 19091;
    public  static  int MainPort =19092;
    public  static  int ScenePort =19093;

    public  static  int OfflineState_Online =0;//在线状态
    public  static  int OfflineState_Leave =1;//离开 比如 后台 或网络 20秒没通 不再发消息 状态
    public  static  int OfflineState_Quit =2;// 退出 关闭游戏 或太久没通
}
