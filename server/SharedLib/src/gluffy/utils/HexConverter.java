package gluffy.utils;

/**
 * Created by admin on 2017/5/11.
 */
public class HexConverter {
    //10进制转16进制
    public static String IntToHex(String str){
        int n = Integer.parseInt(str);
        char[] ch = new char[20];
        int nIndex = 0;
        while ( true ){
            int m = n/16;
            int k = n%16;
            if ( k == 15 )
                ch[nIndex] = 'F';
            else if ( k == 14 )
                ch[nIndex] = 'E';
            else if ( k == 13 )
                ch[nIndex] = 'D';
            else if ( k == 12 )
                ch[nIndex] = 'C';
            else if ( k == 11 )
                ch[nIndex] = 'B';
            else if ( k == 10 )
                ch[nIndex] = 'A';
            else
                ch[nIndex] = (char)('0' + k);
            nIndex++;
            if ( m == 0 )
                break;
            n = m;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(ch, 0, nIndex);
        sb.reverse();
        return sb.toString();
    }

    //10进制转16进制
    public static String LongToHex(String str){
        long n = Long.parseLong(str);
        char[] ch = new char[20];
        int nIndex = 0;
        while ( true ){
            long m = n/16;
            long k = n%16;
            if ( k == 15 )
                ch[nIndex] = 'F';
            else if ( k == 14 )
                ch[nIndex] = 'E';
            else if ( k == 13 )
                ch[nIndex] = 'D';
            else if ( k == 12 )
                ch[nIndex] = 'C';
            else if ( k == 11 )
                ch[nIndex] = 'B';
            else if ( k == 10 )
                ch[nIndex] = 'A';
            else
                ch[nIndex] = (char)('0' + k);
            nIndex++;
            if ( m == 0 )
                break;
            n = m;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(ch, 0, nIndex);
        sb.reverse();
        return sb.toString();
    }

    //16进制转10进制
    public static int HexToInt(String strHex){
        int nResult = 0;
        if ( !IsHex(strHex) )
            return nResult;
        String str = strHex.toUpperCase();
        if ( str.length() > 2 ){
            if ( str.charAt(0) == '0' && str.charAt(1) == 'X' ){
                str = str.substring(2);
            }
        }
        int nLen = str.length();
        for ( int i=0; i<nLen; ++i ){
            char ch = str.charAt(nLen-i-1);
            try {
                nResult += (GetHex(ch)*GetPower(16, i));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return nResult;
    }

    //16进制转10进制
    public static long HexToLong(String strHex){
        long nResult = 0;
        if ( !IsHex(strHex) )
            return nResult;
//        String str = strHex.toUpperCase();
        String str = strHex;
        int nLen = str.length();
        for ( int i=0; i<nLen; ++i ){
            char ch = str.charAt(nLen-i-1);
            try {
                nResult += ((long)GetHex(ch)*(long)GetPower(16, i));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return nResult;
    }

    //计算16进制对应的数值
    public static int GetHex(char ch) throws Exception{
        if ( ch>='0' && ch<='9' )
            return (int)(ch-'0');
        if ( ch>='a' && ch<='f' )
            return (int)(ch-'a'+10);
        if ( ch>='A' && ch<='F' )
            return (int)(ch-'A'+10);
        throw new Exception("error param");
    }

    //计算幂
    public static int GetPower(int nValue, int nCount) throws Exception{
        if ( nCount <0 )
            throw new Exception("nCount can't small than 1!");
        if ( nCount == 0 )
            return 1;
        int nSum = 1;
        for ( int i=0; i<nCount; ++i ){
            nSum = nSum*nValue;
        }
        return nSum;
    }
    //判断是否是16进制数
    public static boolean IsHex(String strHex){
        for (int i = 0; i<strHex.length(); ++i ){
            char ch = strHex.charAt(i);
            if ( (ch>='0' && ch<='9') || (ch>='A' && ch<='F') || (ch>='a' && ch<='f') )
                continue;
            return false;
        }
        return true;
    }

   //将16位的md5转化为long值
    public static long parseMd5L16ToLong(String md5L16){
        if (md5L16 == null) {
            throw new NumberFormatException("null");
        }
        byte[] bA = md5L16.getBytes();
        long re = 0L;
        for (int i = 0; i < bA.length; i++) {
            //加下一位的字符时，先将前面字符计算的结果左移4位
            re <<= 4;
            //0-9数组
            byte b = (byte) (bA[i] - 48);
            //A-F字母
            if (b > 9) {
                b = (byte) (b - 39);
            }
            //非16进制的字符
            if (b > 15 || b < 0) {
                throw new NumberFormatException("For input string '" + md5L16);
            }
            re += b;
        }
        return re;
    }

    //将16进制的字符串转化为long值
    public static long parseString16ToLong(String str16){
        if (str16 == null) {
            throw new NumberFormatException("null");
        }
        //先转化为小写
        str16 = str16.toLowerCase();
        //如果字符串以0x开头，去掉0x
        str16 = str16.startsWith("0x") ? str16.substring(2) : str16;
        if (str16.length() > 19) {
            throw new NumberFormatException("For input string '" + str16 + "' is to long");
        }
        return parseMd5L16ToLong(str16);
    }

}
