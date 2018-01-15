package sendMsg;

import com.cloopen.rest.sdk.CCPRestSDK;
import comm.Client;
import protocol.ServerTipRspd;

import java.util.HashMap;

/**
 * Created by admin on 2016/9/29.
 */
public class SendMsgModel {
    public static void sendSMS(Client client, String telphone, String identifyCode){
        HashMap<String, Object> result = null;

        CCPRestSDK restAPI = new CCPRestSDK();
        restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount("8a216da856b0dca20156b55d9da90478", "bacebffaaa5c408fabf6674e49f8ec5a");// 初始化主帐号和主帐号TOKEN
        restAPI.setAppId("8a216da856b0dca20156b55d9e2a047f");// 初始化应用ID
        try{
            result = restAPI.sendTemplateSMS(telphone,"175206" ,new String[]{identifyCode,telphone,"1"});
//            System.out.println("SDKTestSendTemplateSMS result=" + result);
        }catch (Exception e){
            e.printStackTrace();
            if("112314".equals(result.get("statusCode")) || "160021".equals(result.get("statusCode")) || "160022".equals(result.get("statusCode")) || "160039".equals(result.get("statusCode")) || "160040".equals(result.get("statusCode")) || "160041".equals(result.get("statusCode"))){
                new ServerTipRspd(client,(short)116,"");
                return;
            }else{
                new ServerTipRspd(client,(short)117,"");
                return;
            }
        }
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
//            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
//            Set<String> keySet = data.keySet();
//            for(String key:keySet){
//                Object object = data.get(key);
//                System.out.println(key +" = "+object);
//            }
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }
    }

    public static void sendMessage(Client client,String telphone,String id,String str1,String str2,String str3){
        HashMap<String, Object> result = null;

        CCPRestSDK restAPI = new CCPRestSDK();
        restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount("8a216da856b0dca20156b55d9da90478", "bacebffaaa5c408fabf6674e49f8ec5a");// 初始化主帐号和主帐号TOKEN
        restAPI.setAppId("8a216da856b0dca20156b55d9e2a047f");// 初始化应用ID
        try{
            result = restAPI.sendTemplateSMS(telphone,id ,new String[]{str1,str2,str3});
//            System.out.println("SDKTestSendTemplateSMS result=" + result);
        }catch (Exception e){
            e.printStackTrace();
            if("112314".equals(result.get("statusCode")) || "160021".equals(result.get("statusCode")) || "160022".equals(result.get("statusCode")) || "160039".equals(result.get("statusCode")) || "160040".equals(result.get("statusCode")) || "160041".equals(result.get("statusCode"))){
                new ServerTipRspd(client,(short)116,"");
                return;
            }else{
                new ServerTipRspd(client,(short)117,"");
                return;
            }
        }
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
//            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
//            Set<String> keySet = data.keySet();
//            for(String key:keySet){
//                Object object = data.get(key);
//                System.out.println(key +" = "+object);
//            }
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }
    }
    public static void sendInvite(Client client,String telphone,String inviteCode){
        HashMap<String, Object> result = null;

        CCPRestSDK restAPI = new CCPRestSDK();
        restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount("8a216da856b0dca20156b55d9da90478", "bacebffaaa5c408fabf6674e49f8ec5a");// 初始化主帐号和主帐号TOKEN
        restAPI.setAppId("8a216da856b0dca20156b55d9e2a047f");// 初始化应用ID
        try{
            result = restAPI.sendTemplateSMS(telphone,"111111111",new String[]{inviteCode});
//            System.out.println("SDKTestSendTemplateSMS result=" + result);
        }catch (Exception e){
            e.printStackTrace();
            if("112314".equals(result.get("statusCode")) || "160021".equals(result.get("statusCode")) || "160022".equals(result.get("statusCode")) || "160039".equals(result.get("statusCode")) || "160040".equals(result.get("statusCode")) || "160041".equals(result.get("statusCode"))){
                new ServerTipRspd(client,(short)116,"");
                return;
            }else{
                new ServerTipRspd(client,(short)117,"");
                return;
            }
        }
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
//            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
//            Set<String> keySet = data.keySet();
//            for(String key:keySet){
//                Object object = data.get(key);
//                System.out.println(key +" = "+object);
//            }
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }
    }
}
