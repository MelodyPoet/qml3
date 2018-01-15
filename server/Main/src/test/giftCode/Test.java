package test.giftCode;


import gluffy.utils.HexConverter;
import gluffy.utils.JkTools;
import sdk.utils.MD5;

import java.io.*;

public class Test {


    public static void main(String[] args) throws Exception {
        getCode();
    }

    public static void getCode(){
        int count = 10000;
        int index = 0;
        int gift = 9;
        int platform = 3;
        int gameID = 1;
        int deadDay = JkTools.to2017Day()+365;

        String path = "C:/Users/admin/Desktop/激活码";
        File f = new File(path);
        if(!f.exists()){
            f.mkdirs();
        }
        StringBuffer sb = new StringBuffer();
        sb.append(intToString(2, gameID));
        sb.append(intToString(3, platform));
        sb.append(intToString(4, gift));
        sb.append(intToString(5, index));
        sb.append("-");
        sb.append(count);
        sb.append(".txt");
        String fileName=sb.toString();
        File file = new File(f,fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter pw = new PrintWriter(file);
            for (int i = 0; i < count; i++) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(intToString(2, gameID));
                stringBuffer.append(intToString(5, index + i));
                stringBuffer.append(intToString(3, platform));
                stringBuffer.append(intToString(4, gift));
                stringBuffer.append(intToString(3, deadDay));
            System.out.println(stringBuffer.toString());
                String hex = HexConverter.LongToHex(stringBuffer.toString());
//                System.out.println(hex);
                String code = hex+ MD5.encode(hex).substring(0,4);
//                System.out.println(code);
                pw.println(code);
            }
            pw.close();
            System.out.println("SUCCESS WRITER TO TXT : "+fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String intToString(int num, int value) {
        StringBuffer platformSB = new StringBuffer();
        for (int i = 0; i < num; i++) {
            platformSB.append(value % 10);
            value = value / 10;
        }
        platformSB.reverse();
        return platformSB.toString();
    }
}

