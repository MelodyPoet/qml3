
import modules.passport.*;
import comm.BaseRqstCmd;

import gluffy.comm.BaseRqst;
import gluffy.comm.RqstPool;


/**
 * Created by jackie on 14-4-16.
 */
public class RqstCmdModel {
  public  static   Class<?>[] cmdClassList=new Class<?>[]{LoginCmd.class,
            RoleCreateCmd.class,
            StartGameCmd.class,

  };

    public static void init() throws ClassNotFoundException {
        for (Class<?> classCmd :RqstCmdModel.cmdClassList) {

            Class<?> classRqst=Class.forName("protocol."+classCmd.getSimpleName().substring(0,classCmd.getSimpleName().length()-3)+"Rqst");
            RqstPool.regCmdClass((Class<? extends BaseRqst>) classRqst, (Class<? extends BaseRqstCmd>) classCmd);

        }
    }
}
