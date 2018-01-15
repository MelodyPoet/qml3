package comm;

/**
 * Created by jackie on 14-8-15.
 */
//public class DynamicUserGroup {
//    public  static  DynamicUserGroup instance=new DynamicUserGroup();
//    private Room[] roomList;
//    private int startCount;
//    private int stepCount;
//      public  void  init(int roomMax,int startCount,int stepCount) throws Exception {
//
//        this.startCount = startCount;
//        this.stepCount = stepCount;
//
//         roomList =new Room[roomMax];
//          for (int i = 0; i < roomList.length; i++) {
//              roomList[i]=new Room(i);
//          }
//    }
//     public Room onEnter(User u){
//         if(u.roomID!=-1){
//             return roomList[u.roomID];
//         }
//
//         Room room= findRoom();
//
//         room.users.add(u);
//          u.roomID=room.ID;
//         return room;
//     }
//    public Room onExit(User u,Room room){
//        room.users.remove(u);
//        u.roomID=-1;
//        return room;
//    }
//
//public Room findRoom(){
//
//    int acceptCount=startCount;
//    while (true) {
//        for (Room ts : roomList) {
//            if (ts.size() < acceptCount) return ts;
//        }
//        acceptCount+=stepCount;
//    }
//
//    }
//
//    public Room getRoom(User user) {
//        if(user.roomID<0)return null;
//        return roomList[user.roomID];
//    }
//}
