package recharge;

import comm.User;
import gluffy.comm.BaseBlobDeal;

import java.nio.ByteBuffer;
import java.util.HashSet;

/**
 * Created by admin on 2017/3/16.
 */
public class RechargeModel extends BaseBlobDeal{
    public HashSet<Short> rechageSet = new HashSet<>();
    public User user;

    public RechargeModel(User user) {
        this.user = user;
    }

    @Override
    protected byte[] saveDataBytes() {
        return new byte[0];
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {

    }

    @Override
    public void saveSqlData() {

    }

    @Override
    public void unloadUser() {

    }
}
