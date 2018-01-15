package gluffy.utils;

import java.util.HashMap;
import java.util.Map;

public class TimeManager {
	public  Map<Integer, Long> timeMap=new HashMap<Integer, Long>();
public  void setSecondsPass(int key,int time) {
	timeMap.put(key, time*1000+System.currentTimeMillis());
}
public  void setEndTime(int key,long time) {
	timeMap.put(key, time);
}
public  int getSeconds(int key) {
	Long tim= timeMap.get(key);
	return tim==null?-1:(int)((tim.longValue()-System.currentTimeMillis())/1000);
}
public  long getEndTime(int key) {
	Long tim= timeMap.get(key);
	return tim==null?-1:tim.longValue();
}
}
