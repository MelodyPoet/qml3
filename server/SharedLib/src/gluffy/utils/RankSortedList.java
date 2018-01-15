package gluffy.utils;


import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by jackie on 14-5-3.
 */


public  class RankSortedList<T extends AbsRankListItem> {

    public ArrayList<T> list=new ArrayList<>();
    public void addEnd(T t) {

        t.orderIndex=list.size();
        list.add(t);
    }
    public T get(int index) {
        if(index>=list.size())return null;
        return  list.get(index);
    }
    public T getPrev(AbsRankListItem t){
        if(t.orderIndex-1<0)return null;
        return list.get(t.orderIndex-1);
    }
    public T getNext(AbsRankListItem t){
        if(list.size()<=t.orderIndex+1)return null;
        return list.get(t.orderIndex+1);
    }
    public void swap(AbsRankListItem item1,AbsRankListItem item2){

        Collections.swap(list, item1.orderIndex,  item2.orderIndex);
        int old= item1.orderIndex;
        item1.orderIndex= item2.orderIndex;
        item2.orderIndex=old;
    }
    public void swap(int index1,int index2){

        Collections.swap(list, index1, index2);
        list.get(index1).orderIndex=index1;
        list.get(index2).orderIndex=index2;
    }

    public synchronized  void sortItem(AbsRankListItem item,int newScore){
if(item==null)return;
        if(item.orderScore() == newScore)return;
        AbsRankListItem prev = getPrev(item);
        AbsRankListItem next = getNext(item);

      if((prev!=null&&newScore<=prev.orderScore())&&(next!=null&&newScore>=next.orderScore())){

          return;
      }
        insertHash(item,newScore);
//        check();
    }
private  void check(){
    for (int i = 0; i <list.size() ; i++) {
        if(list.get(i).orderIndex!=i){
            new Exception("fssaf").printStackTrace();

        }
    }
}
    private   void insertHash(AbsRankListItem item,int newScore){

        int mixIndex=list.size()-1;
        int maxIndex=0;
        int mixVal=list.get(mixIndex).orderScore();
        int maxVal=list.get(maxIndex).orderScore();
        int index=0;//比我小的最考前的位置 就是item最终的位置
        int tim=0;
        int time = 0;
//        while (mixVal+1<maxVal){
        while (mixVal<maxVal){
            time++;
            if(time > 100000){
                System.out.println("mixVal : " +mixVal +" maxVal : "+maxVal+" newScore : "+newScore +" index : "+index);
                throw new RuntimeException("Cycle more than 100000 times");
            }

            index=(maxIndex+mixIndex)/2;

             int indexVal=list.get(index).orderScore();
            if(newScore>=indexVal){
                if(index==0||newScore<=list.get(index-1).orderScore()){

                    break;
                }
                mixIndex=index;
                mixVal=indexVal;
            }else {
                if(newScore>=list.get(index+1).orderScore()){
                    index++;
                    break;
                }
                if(maxIndex == index){
                    index = list.size();
                    break;
                }
                maxIndex=index;
                maxVal=indexVal;
            }

tim++;
        }

        int oldIndex=item.orderIndex;

        System.out.println("index" + index);
           if(index<oldIndex){
               list.add(index,list.remove(oldIndex));
               for (int i = index; i <=oldIndex ; i++) {
                 list.get(i).orderIndex=i;
               }
           }else if(index>oldIndex){

               list.add(index-1,list.remove(oldIndex));
               int max = Math.min(index,list.size()-1);
               for (int i = oldIndex; i <=max ; i++) {
                   list.get(i).orderIndex=i;
               }
           }


    }

    public int size() {
        return list.size()  ;
    }
    public void remove(AbsRankListItem item,byte type){
        list.remove(item.orderIndex);
        for (int i = 0; i <list.size() ; i++) {
            list.get(i).orderIndex = i;
        }
        item.onRemove(type);

    }

    public void SortWithLimit(T t,int limit,byte type,int newScore) {
        if(limit<=0)return;
        if(size()<limit){
            if(t.orderIndex == -1){
                addEnd(t);
            }
            sortItem(t,newScore);
           if( size()>limit){
               remove(get(limit),type);
           }
        }else{
            int value = get(size()-1).orderScore();
            if(value<newScore){
                if(t.orderIndex == -1){
                    addEnd(t);
                }
                sortItem(t,newScore);
                if( size()>limit){
                    remove(get(limit),type);
                }
            }else if(value >newScore){
                if(t.orderIndex != -1){
                    remove(t,type);
                }
            }
        }

    }
}




