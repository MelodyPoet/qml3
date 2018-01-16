package qmshared;

import redis.clients.jedis.Jedis;

public class RedisClient {
  private  static   Jedis jedis ;
    public  static Jedis getOne(){
        if(jedis==null){
            jedis=    new Jedis("localhost");
        }
        return jedis;
    }
}
