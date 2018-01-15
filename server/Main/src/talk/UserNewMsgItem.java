package talk;

import comm.User;

/**
 * Created by admin on 2016/7/4.
 */
public class UserNewMsgItem {
    public long lastOperateDay =0;//最后一个运营日(早上六点)
    public long lastReadSystemMsg =0;
    public long lastReadRedPackMsg =0;
    public long lastReadBossMsg =0;
    public int newSystemMsgNum = 0;
    public int newBossMsgNum = 0;
    public int newRedPackMsgNum = 0;
    public boolean isChange= false;

    public UserNewMsgItem(User user) {

    }
}
