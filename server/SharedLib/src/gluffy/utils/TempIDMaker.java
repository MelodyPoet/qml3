package gluffy.utils;

/**
 * Created by jackie on 16-3-2.
 */
public class TempIDMaker {
    long tempID=1;
    public void init(){
        tempID=1;
    }
    public long getNext(){
        return tempID++;
    }
    public int getNextInt(){
        return (int)getNext();
    }
    public short getNextShort(){
        return (short)getNext();
    }
    public byte getNextByte(){
        return (byte)getNext();
    }

    public void used(int tempID) {
        if(this.tempID<=tempID){
            this.tempID=tempID++;
        }
    }
}
