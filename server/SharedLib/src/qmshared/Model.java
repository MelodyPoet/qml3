package   qmshared ;

import gluffy.comm.IBytes;
import gluffy.utils.JkTools;
import org.jdom2.Document;
import table.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.*;

public class Model  extends gluffy.utils.Model {
    public  static   String GateIp="localhost";
    public  static  int GatePort=19091;
    public  static  int MainPort =19092;
    public  static  int ScenePort =19093;

    public  static    int serverVs=106;

    public static Calendar openDay;
    public static String dataPath;
    public static int  userLevelMax;

     public static Map<Integer, MapBaseVo> MapBaseMap;
//    public static Map<Integer, PropBaseVo> PropBaseMap;
    public static Map<Integer, NpcBaseVo> NpcBaseMap;
 //   public static Map<Integer, RoleBaseVo> RoleBaseMap;
  //  public static Map<Integer, RoleLevelBaseVo> RoleLevelBaseMap;
   // public static Map<Integer, ArrayList<DropBaseVo>> DropBaseMap;
   // public static Map<Integer, ArrayList<SkillBaseVo>> SkillBaseMap;
   // public static Map<Integer, ShopBaseVo> ShopBaseMap;

//    public static Map<Integer,GameSetBaseVo> GameSetBaseMap;

    public static int gameID;
    public static int platformID;
    public static int serverID;
    public static Document configXmlDoc;


    public static void init() throws Exception{


        dataPath="";
       // readMap(GameSetBaseVo.class);
        //readMap(RoleBaseVo.class);
        readMap(MapBaseVo.class);
        readMap(NpcBaseVo.class);
       // readMap(RoleLevelBaseVo.class);

      //  readMap(PropBaseVo.class);
     //   readMapList(DropBaseVo.class, "groupID");
     //   readMapList(SkillBaseVo.class);
    //    readMap(ShopBaseVo.class);
     //   readMap(EnchantBaseVo.class);

        readMapList(NpcLayoutBaseVo.class);

      //  userLevelMax=  UserUpdateBaseMap.get(1).max;
 //        for(int id : Model.DropBaseMap.keySet()){
//            System.out.println("======="+id);
//            BaseModel.getDropProps(id,0);
//        }

    }
    private static <T> void readMap(Class<T> t)throws Exception {
        readMap(t,"ID");
    }
    private static<T> void readMap(Class<T> t, String keyName)
            throws Exception {
        ByteBuffer bytes = JkTools.file2Bytebuff(dataPath+t.getName().replace("table.",
                "data/")
                + ".tbl");
        int size = bytes.getInt();

        HashMap<Integer, T> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            IBytes vo = (IBytes) t.newInstance();
            vo.fromBytes(bytes);
            map.put(t.getField(keyName).getInt(vo), (T) vo);
        }
        Model.class.getField(t.getName().replace("table.","").replace("Vo", "Map")).set(null, map);
    }



    private static<T> void fillMap(Map<Integer, T> map ,int start,int end)
    {

        T vo=null;
        for (int i = start; i <= end; i++) {
            if(map.containsKey(i)){
                vo=map.get(i);
            }else {
                map.put(i, vo);
            }
        }
    }


    private static<T> T[] readArray(Class<T> t) throws Exception {
        ByteBuffer bytes = JkTools.file2Bytebuff(dataPath+t.getName().replace("table.",
                "data/")
                + ".tbl");
        int size = bytes.getInt();

        T[] voArray = (T[]) Array.newInstance(t, size);
        for (int i = 0; i < size; i++) {
            IBytes vo = (IBytes) t.newInstance();

            vo.fromBytes(bytes);

            voArray[i] = (T) vo;
        }
        return ((T[]) voArray);
    }
    private static <T> void readMapList(
            Class<T> t) throws Exception {
        readMapList(t,"ID",null);
    }
    private static <T> void readMapList(
            Class<T> t,String keyName) throws Exception {
        readMapList(t,keyName,null);
    }
    private static <T> void readMapList(
            Class<T> t, String keyName,String fieldName) throws Exception {
        T[] mapcells = readArray(t);
        Field keyField = t.getField(keyName);
        HashMap<Integer, ArrayList<T>> map = new HashMap<>();
        for (T vo : mapcells) {
            ArrayList<T> v = map.get(keyField.getInt(vo));
            if (v == null) {
                v = new ArrayList<T>();
                map.put(keyField.getInt(vo), v);
            }
            v.add(vo);
        }
        if(fieldName==null){
            fieldName=t.getName().replace("table.","").replace("Vo", "Map");
        }
        Model.class.getField(fieldName).set(null, map);
    }

    public static    int[] readRoleAttributes (int[] attributesMap)
    {
        int [] values = new int[AttributeEnum.MAX];
        for (int i = 0,len= attributesMap.length; i <len; i+=2) {
            values [attributesMap[i]]=attributesMap[i+1];
        }
        return values;
    }

}
