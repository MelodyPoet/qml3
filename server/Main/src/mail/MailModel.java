package mail;

import comm.CacheUserVo;
import comm.Client;
import comm.Model;
import comm.User;
import gluffy.comm.AbsClient;
import gluffy.utils.JkTools;
import protocol.AnnexPropPVo;
import protocol.MailListRspd;
import protocol.MailPVo;
import sqlCmd.AllSql;
import table.MailBaseVo;

import java.util.*;

/**
 * Created by admin on 2016/7/27.
 */
public class MailModel {
    public User user;
    public Map<Long, MailPVo> unReadMailMap = new HashMap<>();
    public Map<Long, MailPVo> readMailMap = new HashMap<>();
    public ArrayList<MailPVo> addMailList = new ArrayList<>();
    public static final int LIMIT_DAYS = 30;
    public static final int LIMIT_COUNT = 100;
    public boolean flag= false;

    public MailModel(User user) {
        this.user = user;
    }

    public MailPVo getMail(Long mailID) {
        MailPVo mailPVo = null;
        if (unReadMailMap.containsKey(mailID)) {
            mailPVo = unReadMailMap.get(mailID);
        } else if (readMailMap.containsKey(mailID)) {
            mailPVo = readMailMap.get(mailID);
        }
        return mailPVo;
    }

    public void addMail(MailPVo mailPVo,boolean insertNew) {
        if (mailPVo == null) return;
        if(unReadMailMap.size() + readMailMap.size() >= LIMIT_COUNT){
            MailPVo delMail = new MailPVo();
            if(readMailMap.size()>0){
                for(MailPVo pVo : readMailMap.values()){
                    if(mailPVo.prop.size() > 0){
//                    if(mailPVo.prop.size() > 0 || mailPVo.money > 0 || mailPVo.diamond > 0){
                        continue;
                    }
                    if(delMail.guid == 0){
                        delMail = pVo;
                        continue;
                    }
                    if(pVo.receiverTime < delMail.receiverTime){
                        delMail = pVo;
                    }
                }
                if(delMail.guid == 0){
                    for(MailPVo pVo : readMailMap.values()){
                        if(mailPVo.prop.size() <= 0){
//                        if(mailPVo.prop.size() <= 0 && mailPVo.money <= 0 && mailPVo.diamond <= 0){
                            continue;
                        }
                        if(delMail.guid == 0){
                            delMail = pVo;
                            continue;
                        }
                        if(pVo.receiverTime < delMail.receiverTime){
                            delMail = pVo;
                        }
                    }
                }
                readMailMap.remove(delMail.guid);
            }else{
                for(MailPVo pVo : unReadMailMap.values()){
                    if(mailPVo.prop.size() > 0){
//                    if(mailPVo.prop.size() > 0 || mailPVo.money > 0 || mailPVo.diamond > 0){
                        continue;
                    }
                    if(delMail.guid == 0){
                        delMail = pVo;
                        continue;
                    }
                    if(pVo.receiverTime < delMail.receiverTime){
                        delMail = pVo;
                    }
                }
                if(delMail.guid == 0){
                    for(MailPVo pVo : unReadMailMap.values()){
                        if(mailPVo.prop.size() <= 0){
//                        if(mailPVo.prop.size() <= 0 && mailPVo.money <= 0 && mailPVo.diamond <= 0){
                            continue;
                        }
                        if(delMail.guid == 0){
                            delMail = pVo;
                            continue;
                        }
                        if(pVo.receiverTime < delMail.receiverTime){
                            delMail = pVo;
                        }
                    }
                }
                unReadMailMap.remove(delMail.guid);
            }
            if(mailPVo.receiverID > 0){
                AllSql.mailSql.update(mailPVo,AllSql.mailSql.FIELD_FLAG,1);
            }
        }
        if(insertNew){
            AllSql.mailSql.insertNew(mailPVo);
        }
        if(mailPVo.isRead == 0){
            unReadMailMap.put(mailPVo.guid, mailPVo);
        }else{
            readMailMap.put(mailPVo.guid, mailPVo);
        }

    }

    public void createMailListRspd() {
        ArrayList<MailPVo> list = new ArrayList<>();
        for (MailPVo unReadMail : unReadMailMap.values()) {
            list.add(unReadMail);
        }
        for (MailPVo readMail : readMailMap.values()) {
            list.add(readMail);
        }
        new MailListRspd(user.client,(short)unReadMailMap.size(), list);
    }


    public void sendMail(int id) {
        MailBaseVo mailBaseVo = Model.MailBaseMap.get(id);
        if (mailBaseVo == null)return;
        MailPVo mailPVo = new MailPVo();
        mailPVo.senderID = id;
        mailPVo.senderName = mailBaseVo.senderName;
        mailPVo.receiverID = user.guid;
        mailPVo.receiverTime = JkTools.getGameServerTime(null);
        mailPVo.isRead = 0;
        mailPVo.title = mailBaseVo.title;
        mailPVo.msg = mailBaseVo.msg;
        mailPVo.prop = getAnnexPropList(mailBaseVo.prop);
//        mailPVo.money = mailBaseVo.money;
//        mailPVo.diamond = mailBaseVo.diamond;
        Calendar calendar = JkTools.getCalendar();
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        mailPVo.deadDay = day + JkTools.getBaseDay(year) + LIMIT_DAYS;
        sendMail(mailPVo,true);
    }

    public static MailPVo createMail(int id,Long userID) {
        MailBaseVo mailBaseVo = Model.MailBaseMap.get(id);
        if (mailBaseVo == null)return new MailPVo();
        MailPVo mailPVo = new MailPVo();
        mailPVo.senderID = id;
        mailPVo.senderName = mailBaseVo.senderName;
        mailPVo.receiverID = userID;
        mailPVo.receiverTime = JkTools.getGameServerTime(null);
        mailPVo.isRead = 0;
        mailPVo.title = mailBaseVo.title;
        mailPVo.msg = mailBaseVo.msg;
        mailPVo.prop = getAnnexPropList(mailBaseVo.prop);
//            mailPVo.money = mailBaseVo.money;
//            mailPVo.diamond = mailBaseVo.diamond;
        Calendar calendar = JkTools.getCalendar();
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);
        mailPVo.deadDay = day + JkTools.getBaseDay(year) + LIMIT_DAYS;
        return mailPVo;
    }

    public void sendMail(MailPVo mailPVo,boolean isSave) {
        addMail(mailPVo,isSave);
        if(mailPVo.receiverID == 0){
            addMailList.add(mailPVo);
        }else if (CacheUserVo.allMap.containsKey(mailPVo.receiverID)) {
            CacheUserVo cacheUserVo = CacheUserVo.allMap.get(mailPVo.receiverID);
            if (cacheUserVo == null) return;
            Client client = Client.getOne(cacheUserVo);
            if(client != null){
                addMailList.add(mailPVo);
            }
        }
    }

    public static void sendMailToOnLine(MailPVo mailPVo){
        if(mailPVo.senderID > LoopSendMailModel.BASEID){
            if(!LoopSendMailModel.rankUsers.containsKey(mailPVo.guid))return;
           for(long userID : LoopSendMailModel.rankUsers.get(mailPVo.guid).keySet()){
               CacheUserVo cacheUserVo = CacheUserVo.allMap.get(userID);
               Client client = Client.getOne(cacheUserVo);
               MailPVo oneMail = new MailPVo();
               oneMail.guid = mailPVo.guid;
               oneMail.type = mailPVo.type;
               oneMail.senderID = mailPVo.senderID;
               oneMail.receiverTime = mailPVo.receiverTime;
               oneMail.isRead = 0;
               oneMail.title = mailPVo.title;
               oneMail.msg = mailPVo.msg;
               oneMail.senderName = mailPVo.senderName;
               oneMail.prop = mailPVo.prop;
               oneMail.deadDay = mailPVo.deadDay;
               oneMail.receiverID = mailPVo.receiverID;
               if(client == null || !changeMail(client.currentUser.guid,oneMail))continue;
               client.currentUser.mailModel.sendMail(oneMail,false);
           }
        }else{
            for(AbsClient absClient : Client.allOnline.values()){
                Client client=(Client)absClient;
                if(client != null && client.currentUser != null){
                    MailPVo oneMail = new MailPVo();
                    oneMail.guid = mailPVo.guid;
                    oneMail.type = mailPVo.type;
                    oneMail.senderID = mailPVo.senderID;
                    oneMail.receiverTime = mailPVo.receiverTime;
                    oneMail.isRead = 0;
                    oneMail.title = mailPVo.title;
                    oneMail.msg = mailPVo.msg;
                    oneMail.senderName = mailPVo.senderName;
                    oneMail.prop = mailPVo.prop;
                    oneMail.deadDay = mailPVo.deadDay;
                    oneMail.receiverID = mailPVo.receiverID;
                    client.currentUser.mailModel.sendMail(oneMail,false);
                }
            }
        }
    }

    public static boolean changeMail(long userID,MailPVo mailPVo){
        HashMap<Long,Integer> map = LoopSendMailModel.rankUsers.get(mailPVo.guid);
        if(!map.containsKey(userID))return false;
        int rank = map.get(userID);
        ArrayList<AnnexPropPVo> list = new ArrayList<>();
        int[] data = null;
        switch ((int)mailPVo.senderID){
            case 100001:
                changeMail(mailPVo,1,rank,list);
                break;
            case 100002:
                changeMail(mailPVo,4,rank,list);
                break;
            case 100003:
                int[] prop = Model.GameSetBaseMap.get(40).intArray;
                for(int i=0;i<prop.length;i+=2){
                    int id = prop[i];
                    if(id<0)continue;
                    AnnexPropPVo pVo = new AnnexPropPVo();
                    pVo.propID = id;
                    pVo.count = prop[i+1]*rank;
                    list.add(pVo);
                }
                mailPVo.prop = list;
                break;
            case 100004:
                changeMail(mailPVo,2,rank,list);
                break;
            case 100005:
                break;
            case 100006:
                changeMail(mailPVo,5,rank,list);
                break;
            case 100007:
                changeMail(mailPVo,6,rank,list);
                break;
        }
        return true;
}

    private static void changeMail(MailPVo mailPVo,int type,int rank,ArrayList<AnnexPropPVo> list){
        rank+=1;
        int[] data = Model.getDataInRange(type,rank);
        mailPVo.msg = mailPVo.msg.replace("{1}", String.valueOf(rank));
        if(data != null){
            for (int i = 0; i < data.length; i += 2) {
                int id = data[i];
                if (id < 0) continue;
                AnnexPropPVo pVo = new AnnexPropPVo();
                pVo.propID = id;
                pVo.count = data[i + 1];
                list.add(pVo);
            }
        }
        mailPVo.prop = list;
    }

    public static ArrayList<AnnexPropPVo> getAnnexPropList(String propsStr) {
        ArrayList<AnnexPropPVo> list = new ArrayList<>();
        if("".equals(propsStr))return list;
        String[] propArr = propsStr.split("_");
            for (String propStr : propArr) {
                String[] arr = propStr.split("\\|");
                if(arr.length<2)break;
                AnnexPropPVo pVo = new AnnexPropPVo();
                pVo.propID = Integer.parseInt(arr[0]);
                pVo.count = Integer.parseInt(arr[1]);
                list.add(pVo);
            }
        return list;
    }

    public static void delMailProp(MailPVo mailPVo,User user){
        mailPVo.prop = new ArrayList<>();
        if(mailPVo.receiverID <= 0){
            if(LoopSendMailModel.getUsers.containsKey(mailPVo.guid)){
                LoopSendMailModel.getUsers.get(mailPVo.guid).add(user.guid);
            }else{
                HashSet<Long> set = new HashSet<>();
                set.add(user.guid);
                LoopSendMailModel.getUsers.put(mailPVo.guid,set);
            }
            LoopSendMailModel.saveMailUser(mailPVo.guid,LoopSendMailModel.GET_USERS);
        }else {
            AllSql.mailSql.update(mailPVo, AllSql.mailSql.FIELD_IS_GET, 1);
//            AllSql.mailSql.update(mailPVo, AllSql.mailSql.FIELD_PROP, "''");
        }
    }

    public static MailPVo readMail(User user,Long mailID,boolean isRemove){
        MailPVo mailPVo = user.mailModel.getMail(mailID);
        if(mailPVo == null)return null;
        if(mailPVo.isRead != 1){
            mailPVo.isRead = 1;
            if(mailPVo.receiverID <= 0){
                if(LoopSendMailModel.readUsers.containsKey(mailID)){
                    LoopSendMailModel.readUsers.get(mailID).add(user.guid);
                }else{
                    HashSet<Long> set = new HashSet<>();
                    set.add(user.guid);
                    LoopSendMailModel.readUsers.put(mailID,set);
                }
                LoopSendMailModel.saveMailUser(mailID,LoopSendMailModel.READ_USERS);
            }else {
                AllSql.mailSql.update(mailPVo, AllSql.mailSql.FIELD_IS_READ, mailPVo.isRead);
            }
            if(isRemove){
                user.mailModel.unReadMailMap.remove(mailPVo.guid);
                user.mailModel.readMailMap.put(mailPVo.guid,mailPVo);
            }
        }
        return mailPVo;
    }

    public static String getAnnexPropStr(Collection<AnnexPropPVo> list) {
        StringBuffer sb = new StringBuffer();
        if (list != null) {
            for (AnnexPropPVo pVo : list) {
                if (pVo.propID < 0 || pVo.count <= 0 || !Model.PropBaseMap.containsKey(pVo.propID)) {
                    continue;
                }
                sb.append("_" + pVo.propID + "|" + pVo.count);
            }
            if(sb.length()>0){
                sb.deleteCharAt(0);
            }
        }
        return sb.toString() == null ? "" : sb.toString();
    }

    public static int[] getAnnexPropArr(Collection<AnnexPropPVo> list) {
        int[] propArr;
        if (list == null){
            propArr = new int[0];
            return propArr;
        }
        propArr = new int[list.size() * 2];
        Iterator<AnnexPropPVo> it = list.iterator();
        for (int i = 0; i < list.size(); i++) {
            AnnexPropPVo pVo = it.next();
            if (pVo == null || pVo.propID < 0 || pVo.count <= 0 || !Model.PropBaseMap.containsKey(pVo.propID)) {
                continue;
            }
            propArr[2*i] = pVo.propID;
            propArr[2*i + 1] = pVo.count;
        }
        return propArr;
    }

    public static ArrayList<AnnexPropPVo> getAnnexPropList(int[] propArr){
        ArrayList<AnnexPropPVo> list = new ArrayList<>();
        if(propArr == null)return list;
        if(propArr.length == 1 && propArr[0] == 0)return list;
        for(int i=0;i<propArr.length;i+=2){
            AnnexPropPVo pVo = new AnnexPropPVo();
            pVo.propID = propArr[i];
            pVo.count = propArr[i+1];
            list.add(pVo);
        }
        return list;
    }
}
