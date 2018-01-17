package qmshared;

import qmshared.RedisClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RedisTableBase {
    public String tableName;
    public long guid;

public RedisTableBase(long guid,String tableName){
    this.guid=guid;
    this.tableName=tableName;
}
    public  String getField(String fieldName){
        return RedisClient.getOne().hget(tableName+":"+guid,fieldName);
    }

    public List<String> getField(String... fieldName){
        return RedisClient.getOne().hmget(tableName+":"+guid,fieldName);
    }
    public  void setField(String fieldName,String value){
        RedisClient.getOne().hset(tableName+":"+guid,fieldName,value);
    }

    public  void setField(String... keyValueAry){
        HashMap<String,String> keyValueMap=new HashMap<>();
        for (int i = 0; i <keyValueAry.length ; i+=2) {
            keyValueMap.put(keyValueAry[i],keyValueAry[i+1]);
        }
        setField(keyValueMap);
    }
    public  void setField( HashMap<String,String> keyValueMap){
        RedisClient.getOne().hmset(tableName+":"+guid,keyValueMap);
    }

    /*int 處理*/
    public  int getFieldInt(String fieldName){
        return Integer.parseInt(getField(fieldName));
    }
    public  void setFieldInt(String fieldName,int value){
        RedisClient.getOne().hset(tableName+":"+guid,fieldName,value+"");
    }
    public List<Integer> getFieldInt(String... fieldName){
        List<String> list = getField(fieldName);
        List<Integer> listInt=new ArrayList<>();
        for (String value:list
             ) {
            if(value==null)
                listInt.add(0);
                else
            listInt.add(Integer.parseInt(value));
        }
        return listInt;
    }


    public boolean existTable() {
        return RedisClient.getOne().exists(tableName+":"+guid);
    }
}
