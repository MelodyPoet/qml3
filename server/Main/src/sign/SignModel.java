package sign;

import comm.User;
import gluffy.comm.BaseBlobDeal;
import gluffy.utils.JkTools;
import sqlCmd.AllSql;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

/**
 * Created by jackie on 16-1-28.
 */
public class SignModel extends BaseBlobDeal{
    private User user;
    public short lastClearDay=0;
    public HashSet<Short> checkedDays=new HashSet<>();
    public HashSet<Byte> boxGetDays=new HashSet<>();
    public HashSet<Byte> weekRewardSet=new HashSet<>();
public  SignModel(User user){

    this.user=user;
}

    public void clearLastMonSave(Calendar todayCd) {


            Calendar saveCd= JkTools.getCalendar();
            saveCd.set(Calendar.DAY_OF_YEAR,lastClearDay);
          int thisMon=todayCd.get(Calendar.MONTH);
            if(saveCd.get(Calendar.MONTH)!=thisMon){
                lastClearDay=(short)todayCd.get(Calendar.DAY_OF_YEAR);
                ArrayList<Short> stayLastMon=new ArrayList<>();
                 for (short day : checkedDays){
                     saveCd.set(Calendar.DAY_OF_YEAR, day + 7);
                     if(saveCd.get(Calendar.MONTH)==thisMon){
                         stayLastMon.add(day);
                     }

                }

                 checkedDays.clear();
                for (short day : stayLastMon){
                    checkedDays.add(day);
                }
                boxGetDays.clear();
                user.signModel.saveSqlData();
            }

    }

    @Override
    protected byte[] saveDataBytes() {
        byte[] bytes = new byte[2+3+checkedDays.size()*2+boxGetDays.size()+weekRewardSet.size()];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putShort(lastClearDay);
        buffer.put((byte) checkedDays.size());
        for(Short s : checkedDays){
            buffer.putShort(s);
        }
        buffer.put((byte) boxGetDays.size());
        for(Byte b : boxGetDays){
            buffer.put(b);
        }
        buffer.put((byte) weekRewardSet.size());
        for(Byte b : weekRewardSet){
            buffer.put(b);
        }
        return bytes;
    }

    @Override
    protected void loadDataBytes(ByteBuffer buffer) {
        if(buffer == null)return;
        lastClearDay = buffer.getShort();
        int checkSize = buffer.get();
        for(int i=0;i<checkSize;i++){
            checkedDays.add(buffer.getShort());
        }
        int boxSize = buffer.get();
        for(int i=0;i<boxSize;i++){
            boxGetDays.add(buffer.get());
        }
        int weekSize = buffer.get();
        for(int i=0;i<weekSize;i++){
            weekRewardSet.add(buffer.get());
        }
    }

    @Override
    public void saveSqlData() {
        AllSql.userSql.update(user,AllSql.userSql.FIELD_SIGN_INFO,saveData());
    }

    @Override
    public void unloadUser() {
        user = null;
    }
}
