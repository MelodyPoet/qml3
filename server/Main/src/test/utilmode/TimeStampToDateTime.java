package test.utilmode;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by admin on 2017/6/19.
 */
public class TimeStampToDateTime {
    public static void main(String[] args){
        long time = 46991037;//要转换的时间戳
        long value = (time+1451491200)*1000;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(value);
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = s.format(calendar.getTime());
        System.out.println(date);
    }
}
