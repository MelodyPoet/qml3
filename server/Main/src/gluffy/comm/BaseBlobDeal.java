package gluffy.comm;

import comm.User;

import java.nio.ByteBuffer;

public class BaseBlobDeal extends AbsBaseBlobDeal{
    public User user;
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

    @Override
    public void initWithCache(AbsUser user) {
        super.initWithCache(user);
        this.user=(User) user;
    }
}
