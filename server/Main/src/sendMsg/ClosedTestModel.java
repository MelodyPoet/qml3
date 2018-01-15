package sendMsg;

import comm.Client;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 2016/8/25.
 */
public class ClosedTestModel {

    public static final int LIMIT_TIME = 3 * 60;
    public static final String IDENTIFY_SUCCESS = "9999999999";
    public static final int CAN_GET_TIME = 3;

    public static String createIdentifyCode(Client client){
        String identifyCode = getRandNum(4);
        //保存
        return identifyCode;
    }

    public static String getRandNum(int charCount) {
        String charValue = "";
        for (int i = 0; i < charCount; i++) {
            char c = (char) (randomInt(0, 10) + '0');
            charValue += String.valueOf(c);
        }
        return charValue;
    }
    public static int randomInt(int from, int to) {
        Random r = new Random();
        return from + r.nextInt(to - from);
    }

    public static boolean isMobileNO(String mobiles){
        Pattern cm = Pattern.compile("^1(3[4-9]|4[7]|5[0-27-9]|7[08]|8[2-478])\\d{8}$");
        Matcher m = cm.matcher(mobiles);
        if(m.matches()){
            return true;
        }
        Pattern cu = Pattern.compile("^1(3[0-2]|4[5]|5[256]|7[016]|8[56])\\d{8}$");
        m = cu.matcher(mobiles);
        if(m.matches()){
            return true;
        }
        Pattern ct = Pattern.compile("^1(3[34]|53|7[07]|8[019])\\d{8}$");
        m = ct.matcher(mobiles);
        if(m.matches()){
            return true;
        }
        return false;
    }
}
