package gluffy.utils;


import gluffy.comm.AbsClient;
import gluffy.comm.IBytes;
import gluffy.comm.IProtocol;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class JkTools {
	public static Charset UTF8 = Charset.forName("UTF-8");

	public static int getRandRange(int[] rates, int baseRate, int step) {
		int r = (int) (Math.random() * baseRate), c = 0;

		for (int i = 0; i < rates.length; i += step) {
			c += rates[i];
			if (r < c)
				return i;

		}
		return -1;
	}

	public static boolean getResult(int rate,int baseRate) {
		int r = (int) (Math.random() * baseRate);
		if (r < rate)return true;
		return false;
	}
	public static int getRandRange(int[] rates, int baseRate) {
		return getRandRange(rates, baseRate, 1);
	}
    public static int getRandBetween(int mix, int max,int exclude) {
        while (true){
            int v=getRandBetween(mix,max);
            if(v!=exclude)return v;
        }

    }
    public static int getRandBetween(int mix, int max) {
        int r = (int)(Math.random()*(max-mix)+mix);

        return r;
    }
	public static float getRandBetweenf(float mix, float max) {
		float r = (float)(Math.random()*(max-mix)+mix);

		return r;
	}
	public static int getRandAverage(int[] rates) {
		return rates[(int) (Math.random() * rates.length)];
	}

	public static int getFac(int dx, int dy) {
		return getFac(dx, dy, 8);
	}

	public static int getFac(int dx, int dy, int dirCount) {
		double rot = Math.atan2(dy, dx);
		if (rot < 0)
			rot += Math.PI * 2;
		return (int) (Math.round(rot * dirCount / (2 * Math.PI)) + dirCount / 4)
				% dirCount;
	}

	public static String readString(ByteBuffer bb) {
		return readString(bb, Short.MAX_VALUE);

	}
 
	public static String readString(ByteBuffer bb, int sizeMax) {
		if (bb.remaining() < 2)
			return "";
		short len = bb.getShort();
		if (len <= 0) {
			return "";

		}
		if (len > bb.remaining())
			return "";
		if (len > sizeMax)
			len = (short) sizeMax;
		byte[] bytes = new byte[len];
		bb.get(bytes);
		return new String(bytes, UTF8);
	}

	public static void writeString(ByteBuffer bb, String str) {

		if (str != null) {
			byte[] bytes = str.getBytes(UTF8);
			bb.putShort((short) bytes.length);
			bb.put(bytes);
		} else {
			bb.putShort((short) 0);
		}

	}

	public static int getStringLength(String str) {

		if (str != null) {
			byte[] bytes = str.getBytes(UTF8);
			return bytes.length;
		} else {
			return 0;
		}
	}

	 

	public static void writeArray(ByteBuffer bb, int[] array) {

		bb.putInt(array.length);
		for (Integer i : array) {
			bb.putInt(i);
		}

	}
	public static void writeArray(ByteBuffer bb, byte[] array) {

		bb.putInt(array.length);
		for (byte i : array) {
			bb.put(i);
		}

	}
	public static ArrayList<Integer> readIntList(ByteBuffer bytes) {
		int size=bytes.getInt();
		ArrayList<Integer> array=new ArrayList<Integer>();
		for (int i=0 ;i<size;i++) {
			array.add(bytes.getInt());
			}
		return array;
	}
	public static int[] readIntArray(ByteBuffer bytes) {
		int size=bytes.getInt();
		int [] array=new int[size];
		for (int i=0 ;i<size;i++) {
			array[i]=(bytes.getInt());
			}
		return array;
	}
	public static byte[] readByteArray(ByteBuffer bytes) {
		int size=bytes.getInt();
		byte [] array=new byte[size];
		for (int i=0 ;i<size;i++) {
			array[i]=(bytes.get());
			}
		return array;
	}
	public static void writeMap(ByteBuffer bytes, Map<?,?> map) {
		if(map==null){
			bytes.putInt(0);
			return;
		}
		bytes.putInt(map.size());
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			writeValue(bytes,entry.getKey());
			writeValue(bytes,entry.getValue());
		}
		
	}
	public static void writeValue(ByteBuffer bytes, Object value) {
		if (value instanceof Long) {
			bytes.putLong((Long) value);
		}	else if (value instanceof Integer) {
			bytes.putInt((Integer) value);
		} else if (value instanceof Short) {
			bytes.putShort((Short) value);
		} else if (value instanceof Byte) {
			bytes.put((Byte) value);
		} else if (value instanceof IBytes) {
			((IBytes)value).toBytes(bytes);
	}else {
			  new Exception("������������������"+value.getClass()).printStackTrace();
		}
		
	}
	public static void writeArray(ByteBuffer bytes, Collection<?> array) {
		if(array==null){
			bytes.putInt(0);
			return;
		}
		bytes.putInt(array.size());
		for (Object i : array) {
			writeValue(bytes, i);
		}
	}

	public static <T> ArrayList<T> readArray(ByteBuffer bytes, Class<T> ct)
	{
		int size = bytes.getInt();

		ArrayList<T> array = new ArrayList<>();
		if (size == 0) {

			return array;
		}

		// t.getClass().newInstance();
		for (int i = 0; i < size; i++) {
			// array.add(readValue(bytes,t));
			T value = null;
			if (ct == Long.class) {
				value = (T) (Long) bytes.getLong();
			} else if (ct == Integer.class) {
				value = (T) (Integer) bytes.getInt();
			} else if (ct == Short.class) {
				value = (T) (Short) bytes.getShort();
			} else if (ct == Byte.class) {
				value = (T) (Byte) bytes.get();
			} else {

				try {
					value = (T) ct.newInstance();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				((IBytes) value).fromBytes(bytes);

			}
			array.add(value);
		}
		return array;
	}
 

	public static byte[] obj2Bytes(ByteBuffer buffer, IBytes obj) {
		buffer.position(0);
		obj.toBytes(buffer);
		byte[] bytes = new byte[buffer.position()];
		buffer.position(0);
		buffer.get(bytes);
		return bytes;
	}

	public static boolean isSameDay(Calendar time1,Calendar time2) {
		return time1.get(Calendar.DAY_OF_YEAR) == time2.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * ������������������������������������
	 * 
	 * */
	public static boolean isSameDay(long time1, long time2) {

		Calendar dt1 = Calendar.getInstance();
		dt1.setTimeInMillis(time1);
		Calendar dt2 = Calendar.getInstance();
		dt2.setTimeInMillis(time2);
		return isSameDay(dt1,dt2);
	}

 
	 
	/**
	 * ������yyyy-MM-dd HH:mm:ss������������������������������
	 * 
	 * @param date
	 * @return
	 */
	public static Date str2Date(String date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;

	}
 
	public static int getRange(int value) {

		if (value == 0)
			return 0;
		return value > 0 ? 1 : -1;

	}

	public static ByteBuffer file2Bytebuff(String filename) {
		FileInputStream fs;
		byte[] byteArr = null;

		try {
			fs = new FileInputStream(filename);
			byteArr = new byte[fs.available()];
			fs.read(byteArr);
			fs.close();
		} catch (Exception e) {
			// LogManager.printNormal("filename = "+filename+" not founded!");
			return null;

		}

		ByteBuffer bytes = ByteBuffer.wrap(byteArr);
		return bytes;
	}

	public static boolean bytebuff2File(ByteBuffer bytes, String filename) {
		FileOutputStream fs;

		try {
			fs = new FileOutputStream(filename);
			fs.write(bytes.array());

			fs.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	public static int indexOf(int[] arr, int val, boolean descMode) {
		if (descMode) {
			for (int i = 0; i < arr.length; i++) {
				if (val >= arr[i])
					return i;
			}
		} else {
			for (int i = 0; i < arr.length; i++) {
				if (val < arr[i])
					return i;
			}

		}
		return arr.length;
	}

	public static int indexOf(int[] arr, int val) {
		for (int i = 0; i < arr.length; i++) {
			if (val == arr[i])
				return i;
		}
		return -1;
	}

	public static byte indexOf(byte[] arr, byte val) {
		for (byte i = 0; i < arr.length; i++) {
			if (val == arr[i])
				return i;
		}
		return -1;
	}

	public static int[] readArray(ByteBuffer bytes) {
		return readArray(readString(bytes));
	}

	public static int[] readArray(String str) {
		return readArray(str, "_|\\|");
	}

	public static int[] readArray(String str, String tag) {
		if (str==null||str.isEmpty()||str.equals(""))
			return null;
		String[] vals = str.split(tag);

        int[] intVals = new int[vals.length];
		for (int i = 0; i < intVals.length; i++) {
if(COMPARE_MAP.containsKey(vals[i])) {
    intVals[i] = COMPARE_MAP.get(vals[i]);
}else {
    intVals[i] = Integer.parseInt(vals[i]);
}
		}
		return intVals;
	}

	public static boolean isInTime(int startHour, int lastMinutes) {
		if (lastMinutes <= 0)
			return false;
		long ct = JkTools.getCalendar().getTimeInMillis();
		Calendar openTime = JkTools.getCalendar();
		openTime.set(Calendar.HOUR_OF_DAY, startHour);
		openTime.set(Calendar.MINUTE, 0);
		openTime.set(Calendar.SECOND, 0);
		long startTime = openTime.getTimeInMillis();
		return ct >= startTime && ct < startTime + lastMinutes * 60000;
	}
    public static final HashMap<String,Integer> COMPARE_MAP=new HashMap<String,Integer>(){
        {
            put("=",COMPARE_EQUAL);
            put(">=",COMPARE_EQUAL_OR_BIG);
            put(">",COMPARE_BIG);
            put("<=",COMPARE_EQUAL_OR_LESS);
            put("<",COMPARE_LESS);

        }
    };
	public static final int COMPARE_TRUE = -1, COMPARE_EQUAL = 0,
			COMPARE_EQUAL_OR_BIG = 1,COMPARE_BIG = 2, COMPARE_EQUAL_OR_LESS = 3,COMPARE_LESS = 4;

	public static boolean compare(int a, int b, int mode) {
		switch (mode) {
		case COMPARE_TRUE:
			return true;
		case COMPARE_EQUAL:
			return a == b;
		case COMPARE_EQUAL_OR_BIG:
			return a >= b;
		case COMPARE_EQUAL_OR_LESS:
			return a <= b;
            case COMPARE_BIG:
                return a > b;
            case COMPARE_LESS:
                return a < b;
		}
		return false;
	}

	public static int addInMap(Map<Integer, Integer> map, int key, int addValue) {
		Integer v = map.get(key);
		if (v == null)
			v = 0;
		v += addValue;
		map.put(key, v);
		return v;
	}

	public static int getRandAverage(ArrayList<Integer> rates) {
		return rates.get( (int) (Math.random() * rates.size()));
	}
	//������������������
	public static int getSectionIndex (int[] arr,int val,int step) {
		if(val<arr[0])return -1; 
		for (int i = 0,len= arr.length-step;i<len; i+=step) {
			if(arr[i+step]>val)return i;
		}
		 return  arr.length-step;
	}

	public static ArrayList<?extends IBytes> createProtocolArray(
		ArrayList<?extends IProtocol> srcList) {
		ArrayList<IBytes> arrayList=new ArrayList<>();
		for (IProtocol pvo : srcList) {
			arrayList.add(pvo.createProtocolVo());
		}
		return arrayList;
	}
	 public static <T> List<T> asListTrimNull(T... a) {
	        ArrayList<T> list=new ArrayList<>();
	        for (T t : a) {
				if(t!=null)list.add(t);
			}
	        return list;
	    }
    public  static List intArrayAsList(  int[] a){
        if(a == null)
           return null;

        return new IntArray2List(a);

    }

	public  static int[] List2IntArray(Collection<Integer> list){
		if(list == null)
			return null;
		return ((IntArray2List)list).ary;

	}
	public  static int[] intListAsArray(ArrayList<Integer> list){
		if(list == null)
			return null;
int []rst=new int [list.size()];
		for (int i = 0; i <list.size() ; i++) {
			rst[i]=list.get(i);
		}
		return rst;

	}
    public  static void  arrayAdd(int[] src,int[] des){
    for (int i = 0,len=src.length; i <len ; i++) {
        des[i]+=src[i];
    }
}

    public static void writeBool(ByteBuffer bytes,boolean b) {
          bytes.put(b?(byte)1:(byte)0);
    }
    public  static  int getGameServerTime(AbsClient client){
		if(client == null){
			return (int)(System.currentTimeMillis()/1000-1451491200)+Model.addMinute * 60;
		}
		return (int)(System.currentTimeMillis()/1000-1451491200+client.addHours* Model.ONE_HOURS_TIME+Model.addMinute * 60);
	}
    public  static  int getGameServerTime(long times){
			return (int)(times/1000-1451491200);
	}
    public  static  int getGameServerDaysWith(AbsClient client,int serverTime){

        Calendar d2 = JkTools.getCalendar();
		if(client != null){
			d2.add(Calendar.HOUR_OF_DAY,client.addHours);
		}
        Calendar d1 = JkTools.getCalendar();
        d1.setTimeInMillis(((long)serverTime + 1451491200) * 1000);
       // return (int)(System.currentTimeMillis()/1000/86400-((long)serverTime+1451491200)/86400);

        int days = d2.get(Calendar.DAY_OF_YEAR)- d1.get(Calendar.DAY_OF_YEAR);
         int y2 = d2.get(Calendar.YEAR);
        if (d1.get(Calendar.YEAR) != y2) {
//          d1 = (Calendar) d1.clone();
            do {
                days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);
                d1.add(Calendar.YEAR, 1);
            } while (d1.get(Calendar.YEAR) != y2);
        }
        return days;
 //        aCalendar.set(Calendar.HOUR,0);
//        aCalendar.set(Calendar.MINUTE,0);
//        aCalendar.set(Calendar.SECOND,0);
//        aCalendar.set(Calendar.MILLISECOND,0);
//
//        bCalendar.set(Calendar.HOUR,0);
//        bCalendar.set(Calendar.MINUTE,0);
//        bCalendar.set(Calendar.SECOND,0);
//        bCalendar.set(Calendar.MILLISECOND,0);
 //       return (int) (aCalendar.getTimeInMillis()-bCalendar.getTimeInMillis())/(24*3600000);

    }

    public static HashMap<Integer, int[]> readIntGroup(ByteBuffer bytes) {
        String  str=readString(bytes);
        if(str==null||str.length()==0)return new HashMap<>();
       String[] items= str.split("_");
        HashMap<Integer, int[]> hashMap=new HashMap<>();
        for (  String item :items){
          int [] intVals=  readArray(item,"\\|");
            for (int i = 0; i <intVals.length ; i++) {
                hashMap.put(intVals[i],intVals);
            }
        }
return hashMap;
    }
    public static HashMap<Integer,Integer> readIntIntMap(ByteBuffer bytes) {
        HashMap<Integer,Integer> map=new HashMap<>();

        int [] array=readArray(bytes);
        for (int i=0 ;i<array.length;i+=2) {
            map.put(array[i],array[i+1]);
        }
        return map;
    }

	public static int getBaseDay(int year){
		return (year-2017)*365;
	}


	public static int getRunday(AbsClient client){
		Calendar calendar = JkTools.getCalendar();
		calendar.add(Calendar.HOUR_OF_DAY,client.addHours);
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		if(hours < 6){
			calendar.add(Calendar.DATE,-1);
		}
		return  calendar.get(Calendar.DAY_OF_YEAR);
	}

	public static int getRundayTime(AbsClient client){
		Calendar calendar = JkTools.getCalendar();
		if(client != null){
			calendar.add(Calendar.HOUR_OF_DAY,client.addHours);
		}
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		if(hours < 6){
			calendar.add(Calendar.DATE,-1);
		}
		calendar.set(Calendar.HOUR_OF_DAY,6);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		return  JkTools.getGameServerTime(calendar.getTimeInMillis());
	}

	public static int getMondayBeginTime(AbsClient client){
		Calendar calendar = JkTools.getCalendar();
		if(client != null){
			calendar.add(Calendar.HOUR_OF_DAY,client.addHours);
		}
		calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);
		return  JkTools.getGameServerTime(calendar.getTimeInMillis());
	}

	public static Calendar getCalendar(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE,Model.addMinute);
		return calendar;
	}

	public static Date getDate(String string){
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date date = null;
		try {
			date = format.parse(string);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static int to2017Day(){
		Date date = JkTools.getDate("2017/1/1");
		Calendar calendar = Calendar.getInstance();
		long now = calendar.getTimeInMillis();
		return (int)((now - date.getTime())/ Model.ONE_DAY_TIME/1000);
	}

	public static int getTodayMinute(AbsClient client){
		Calendar calendar = JkTools.getCalendar();
		calendar.add(Calendar.HOUR_OF_DAY,client.addHours);
		int minute = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		return minute;
	}

	public static boolean canCompleteParams(int[] arr, int sub1, int sub2) {
		if (arr != null) {
			if (JkTools.compare(sub1, arr[1], arr[0]) == false) return false;
			if (arr.length >= 4 && JkTools.compare(sub2, arr[3], arr[2]) == false)
				return false;
		}
		return true;
	}

    public static void writeArray2(ByteBuffer bb, int[][] array) {
		bb.putInt(array.length);
		for (int[] iary : array) {
			writeArray(bb,iary);
		}
	}
	public static int[][] readArray2(ByteBuffer bb) {
   int[][] intVals = new int[bb.getInt()][];
		for (int i = 0; i <intVals.length ; i++) {
			intVals[i]=readIntArray(bb);
		}

		return intVals;
	}

	static class IntArray2List extends AbstractList{
int [] ary;
		IntArray2List(int [] ary){
	this.ary=ary;
}
		@Override
		public Object get(int index) {
			return new Integer( ary[index]);
		}

		@Override
		public int size() {
			return ary.length;
		}
	}
}
