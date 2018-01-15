package giftCode;

import comm.*;
import gluffy.comm.BaseRqst;
import gluffy.utils.HexConverter;
import gluffy.utils.JkTools;
import org.jdom2.Element;
import protocol.AwardShowRspd;
import protocol.GiftCodeRqst;
import protocol.PropPVo;
import protocol.ServerTipRspd;
import sdk.utils.MD5;
import table.GiftBaseVo;
import table.ReasonTypeEnum;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by admin on 2017/5/11.
 */
public class GiftCodeCmd extends BaseRqstCmd{
    @Override
    public void execute(Client client, User user, BaseRqst baseRqst) {
        GiftCodeRqst rqst = (GiftCodeRqst)baseRqst;
        String code = rqst.code;
        Element rootEl = Model.configXmlDoc.getRootElement();
        Connection conn = null;
        ResultSet query = null;
        try {
            conn = DriverManager.getConnection(rootEl.getChild("sqlCmd").getValue(), rootEl.getChild("sqlUser").getValue(),rootEl.getChild("sqlPwd").getValue());
            Statement state = conn.createStatement();
            query = state.executeQuery("select * from gift_code where giftCode= \'"+  code+"\' limit 1");
            if(query.next()==true){
                new ServerTipRspd(client,(short)306,null);
                return;
            }else{
                String hex = code.substring(0,code.length()-4);
                String codeMD5 = code.substring(code.length()-4);
                String md5 = MD5.encode(hex).substring(0,4);
                if(!codeMD5.equals(md5)){
                    new ServerTipRspd(client,(short)306,null);
                    return;
                }
                String codeValue = String.valueOf(HexConverter.parseString16ToLong(hex));
                int gameID = Integer.parseInt(codeValue.substring(0,1));
                if(gameID != Model.gameID){
                    new ServerTipRspd(client,(short)306,null);
                    return;
                }
                int platformID = Integer.parseInt(codeValue.substring(5,7));
                if(platformID != Model.platformID){
                    new ServerTipRspd(client,(short)306,null);
                    return;
                }
                int deadDay = Integer.parseInt(codeValue.substring(10,13));
                if(JkTools.to2017Day() >= deadDay){
                    new ServerTipRspd(client,(short)306,null);
                    return;
                }
                int giftID = Integer.parseInt(codeValue.substring(7,10));
                if(user.giftCodeModel.usedgift.contains(giftID)){
                    new ServerTipRspd(client,(short)307,null);
                    return;
                }
                GiftBaseVo giftBaseVo = Model.GiftBaseMap.get(giftID);
                if(giftBaseVo == null){
                    new ServerTipRspd(client,(short)306,null);
                    return;
                }
                user.propModel.addListToBag(giftBaseVo.awards, ReasonTypeEnum.GIFT_CODE);
                ArrayList<PropPVo> list = new ArrayList<>();
                int[] arr = giftBaseVo.awards;
                for(int i=0;i<arr.length;i+=2){
                    PropPVo propPVo = new PropPVo();
                    propPVo.baseID = arr[i];
                    propPVo.count = arr[i+1];
                    list.add(propPVo);
                }
                new AwardShowRspd(client,list);
                user.giftCodeModel.usedgift.add(giftID);
                user.giftCodeModel.saveSqlData();
                state.executeUpdate("insert into gift_code(giftCode) values(\'"+code+"\')");
            }
            query.close();
            state.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            SqlPool.release(conn);
        }
    }
}
