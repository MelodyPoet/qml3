package navigation;

import java.util.ArrayList;

public class BinaryHeap {
    private ArrayList<AsNode> list=new  ArrayList<AsNode>() ;

    public void push(AsNode val){
        list.add(val);
        tryUpAt(list.size()-1);
    }
    public AsNode popMix(){
        AsNode val=list.get(0);
        swapAt(0,list.size()-1);
        list.remove(list.size()-1);
        tryDownAt(0);
        return val;
    }
    public void updata(AsNode grid,int newScore){
        int index=list.indexOf(grid);
        if(grid.score>newScore){
            grid.score=newScore;
            tryDownAt(index);
        }else if(grid.score<newScore){
            grid.score=newScore;
            tryUpAt(index);
        }
    }
		/*	public function updataAt(index:int,newVal:AsNode):void{

		}*/

    private void tryDownAt(int index){
        int childIndex=index*2+1;
        if(childIndex>=list.size()){return;}

        if(childIndex+1<list.size()&&list.get(childIndex+1).score<list.get(childIndex).score){
            if(list.get(index).score>list.get(childIndex+1).score){
                swapAt( index,childIndex+1);
                tryDownAt(childIndex+1);

            }
        }else{

            if(list.get(index).score>list.get(childIndex).score){
                swapAt( index,childIndex);
                tryDownAt(childIndex);

            }
        }
    }
    public int  length(){

      return list.size();
    }
    private void tryUpAt(int index){
        if(index<=0)return;
        int headIndex=(index-1)/2;
        if(list.get(index).score<list.get(headIndex).score){
            swapAt(index,headIndex);
            tryUpAt(headIndex);
        }
    }
    private void swapAt(int pos1 ,int pos2){
        AsNode c=list.get(pos1);
        list.set(pos1,list.get(pos2));
        list.set(pos2,c);

    }

}
