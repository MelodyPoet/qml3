package comm;

import java.util.HashSet;
import java.util.Set;

public class Room {
    public  Room(int ID){this.ID=ID;}
    public  int ID;
     public Set<User> users=new HashSet<>();

    public  int size(){return users.size();}

}
