package gluffy.comm;



import java.util.HashMap;

 public class RqstPool {


     private static HashMap<Short, BaseRqst> RqstMap=new HashMap<>();
    public static void regCmdClass(Class<?extends BaseRqst> rqstClass, Class<?extends AbsBaseRqstCmd> cmdClass) {
        try {
            AbsBaseRqstCmd cmd=  cmdClass.newInstance();
            short id=-1;
         

                BaseRqst baseRqst = rqstClass.newInstance();
                baseRqst.cmd = cmd;
            if(id==-1) {
                id = rqstClass.getField("PRO_ID").getShort(baseRqst);
 }
           
            RqstMap.put(id, baseRqst);
   
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    public static BaseRqst getNextInstance(short id) {
    	BaseRqst rqst = RqstMap.get(id);
        if(rqst == null)return null;
    	 BaseRqst newRqst=null;
		try {
			newRqst = rqst.getClass().newInstance();
			 newRqst.cmd=rqst.cmd;
		} catch (InstantiationException e) {
 			e.printStackTrace();
		} catch (IllegalAccessException e) {
 			e.printStackTrace();
		}
    	
        return newRqst;
    }
 


}
