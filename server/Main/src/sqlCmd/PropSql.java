package sqlCmd;

import comm.Model;
import comm.SqlPool;
import comm.User;
import prop.PropModel;
import protocol.PropPVo;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import table.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class PropSql extends BaseSql {

    public final String FIELD_UID="userID";
    public final String FIELD_BASEID="baseID";
    public final String FIELD_TABLEID="tableID";
    public final String FIELD_INTENSIFY="intensify";
    public final String FIELD_COUNT="count";
    public final String FIELD_ATTRIBUTERND="attributeRnd";
    public final String FIELD_EXATTRIBUTERND="exAttributeRnd";
    public final String FIELD_EXP="exp";
    public final String FIELD_ADVANCE="advance";
    public final String FIELD_PURIFY="purify";

	public PropSql() {
		super("prop");
 	}

    public void isInitDataBase(){
        initDataBase();
    }

public boolean loadone(User user) {
ResultSet set = readSet(FIELD_UID+" = "+user.guid );
if(set==null)return false;
try {
	while (set.next()) {
		 PropPVo pVo=new PropPVo();
		int index=1;
		pVo.tempID=set.getLong(index++);
		index++;
		pVo.baseID=set.getInt(index++);
		 switch (set.getByte(index++)) {
		case PropCellEnum.BAG:
			user.propModel.addInBag(pVo, false);
			break;
		case PropCellEnum.EQUIP:
			user.propModel.setEquipByIndex((byte)Model.PropBaseMap.get(pVo.baseID).type,pVo);
			break;
		default:
			break;
		}
		 pVo.intensify=set.getByte(index++);
        pVo.count=set.getInt(index++);
        pVo.exAttribute=new ArrayList<>();
        pVo.attributeRnd=new ArrayList<>();
        attributeRndSqlData(pVo,false,set.getString(index++));
        exAttributeRndSqlData(pVo,false,set.getString(index++));
        pVo.exp = set.getInt(index++);
        pVo.advance = set.getByte(index++);
        pVo.purify = set.getByte(index++);
    }

} catch (SQLException e) {
 	e.printStackTrace();
	return false;
}finally {
    if(set!=null){
        try {
            set.getStatement().close();
            set.close();
closeSetConn();
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
    }

}

	return true;
}
 
	public boolean insertNew(User user,PropPVo pVo,byte tableID) {
      PropBaseVo baseProp= Model.PropBaseMap.get(pVo.baseID);
        if(baseProp==null)return false;
        long guid= AllSql.propSql.getNewGuid();

        Random rnd=     new Random();
        pVo.tempID=guid;
        pVo.attributeRnd=new ArrayList<>();
        pVo.exAttribute=new ArrayList<>();

        if(PropModel.isEquip(pVo.baseID)) {
            for (int i = 0; i < baseProp.attributes.length; i += 2) {
                pVo.attributeRnd.add((byte) rnd.nextInt(baseProp.rndAttributes));
            }
pVo.exAttribute=user.propModel.createExtraAttributes(baseProp, null);


        }
        insert(FIELD_GUID+","+FIELD_UID+","+FIELD_BASEID+","+FIELD_TABLEID+","+FIELD_COUNT+","+FIELD_ATTRIBUTERND+","+FIELD_EXATTRIBUTERND, guid+","+user.guid +","+pVo.baseID+","+tableID+","+pVo.count+","
                +attributeRndSqlData(pVo,true,null)+","+exAttributeRndSqlData(pVo,true,null));
//        for (int i = 0; i < baseProp.extraAttributes.length; i++) {
//            pVo.attributeRnd.add( (byte)rnd.nextInt(baseProp.rndAttributes));
//        }
//       ;
 		return true;
	}
    public String attributeRndSqlData(PropPVo pVo,boolean forSave,String loadStr) {
        byte[] bytes=null;
        if(forSave) {
            if (pVo.attributeRnd.size() == 0)
                return null;
           bytes = new byte[pVo.attributeRnd.size()];
            for (int i =0;i<bytes.length;i++){
           bytes[i]=((ArrayList<Byte>)pVo.attributeRnd ).get(i);

            }
            return "'" + new BASE64Encoder().encode(bytes) + "'";
        }else{

            if (loadStr == null || loadStr.length() < 1){
                 return null;
            }


            try {
                bytes = new BASE64Decoder().decodeBuffer(loadStr);
            } catch (IOException e) {
                e.printStackTrace();
            }
          //  ByteBuffer buffer = ByteBuffer.wrap(bytes);
            if(pVo.attributeRnd.size()>0)pVo.attributeRnd.clear();
            for (int i =0;i<bytes.length;i++){
                ((ArrayList<Byte>)pVo.attributeRnd ).add(bytes[i]);
            }

return null;

        }
    }
    public String exAttributeRndSqlData(PropPVo pVo,boolean forSave,String loadStr) {
        byte[] bytes=null;
        if(forSave) {
            if (pVo.exAttribute.size() == 0)
                return null;
            bytes = new byte[pVo.exAttribute.size()*2];
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            for (int i =0;i<bytes.length/2;i++){
                buffer.putShort(((ArrayList<Short>)pVo.exAttribute ).get(i));

            }
            return "'" + new BASE64Encoder().encode(bytes) + "'";
        }else{

            if (loadStr == null || loadStr.length() < 1){
                return null;
            }


            try {
                bytes = new BASE64Decoder().decodeBuffer(loadStr);
            } catch (IOException e) {
                e.printStackTrace();
            }
              ByteBuffer buffer = ByteBuffer.wrap(bytes);
            if(pVo.exAttribute.size()>0)pVo.exAttribute.clear();
            for (int i =0;i<bytes.length/2;i++){
                pVo.exAttribute.add(buffer.getShort());
            }

            return null;

        }
    }

    public static HashSet<Long> loadOwners(int baseID,int limit) {

        Connection conn= SqlPool.getConn();
        String cmd;
        ResultSet set=null;
        HashSet<Long> users=new HashSet<>();
        cmd="select userID from  prop where baseID="+baseID+" limit "+limit;

        Statement state=null;
        try {
            conn.setAutoCommit(true);
            state = conn.createStatement();

            set = state.executeQuery(cmd);



        } catch (SQLException e) {
            new Exception("ReconnectError").printStackTrace();

        }


         if(set==null)return null;
        try {
            while (set.next()) {

                users.add(set.getLong(1));

            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }finally {
            if(set!=null){
                try {

                    set.getStatement().close();
                    set.close();

                } catch (SQLException e2) {
                    e2.printStackTrace();
                }
            }

        }

        return users;
    }

    public void initDataBase(){
        Connection conn = SqlPool.getConn();
        String cmd;
        try {
            ArrayList<RobotBaseVo> robotList = Model.RobotBaseMap.get((int) RobotType.ARENA);
            int i =0;
            for(RobotBaseVo robot : robotList){
                for(int propID : robot.equip){
                    long guid=AllSql.propSql.getNewGuid();
                    cmd = "insert into prop("+FIELD_GUID+","+FIELD_UID+","+FIELD_BASEID+","+FIELD_TABLEID+","+FIELD_COUNT+") "+"values("+ guid+","+ ++i +","+propID+","+ PropCellEnum.EQUIP +","+1+")";
                    Statement state = conn.createStatement();
                    state.executeUpdate(cmd);
                }
            }
        } catch (Exception e) {

            new Exception("ReconnectError").printStackTrace();
            e.printStackTrace();
        } finally {
            SqlPool.release(conn);
        }
    }

}
