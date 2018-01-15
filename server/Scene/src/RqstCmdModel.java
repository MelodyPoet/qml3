import comm.BaseRqstCmd;
import commCommand.MltSceneAttackCmd;
import commCommand.MltSceneEnterCmd;
import commCommand.MltSceneHurtCmd;
import commCommand.MltSceneMoveToCmd;
import gate.ServerClientQuitCmd;
import gluffy.comm.BaseRqst;
import gluffy.comm.RqstPool;

/**
 * Created by jackie on 14-4-16.
 */
public class RqstCmdModel {
  public  static   Class<?>[] cmdClassList=new Class<?>[]{
          MltSceneEnterCmd.class,
          MltSceneMoveToCmd.class,
          ServerClientQuitCmd.class,
          MltSceneHurtCmd.class,
          MltSceneAttackCmd.class

  };

    public static void init() throws ClassNotFoundException {
        for (Class<?> classCmd :RqstCmdModel.cmdClassList) {

            Class<?> classRqst=Class.forName("protocol."+classCmd.getSimpleName().substring(0,classCmd.getSimpleName().length()-3)+"Rqst");
            RqstPool.regCmdClass((Class<? extends BaseRqst>) classRqst, (Class<? extends BaseRqstCmd>) classCmd);

        }
    }
}
