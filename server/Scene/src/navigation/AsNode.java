package navigation;

public class AsNode {
    public AsNode parent;
    public int score;
    public boolean closed=false;
    public Edge edge;

    public AsNode (Edge edge,int dis,AsNode parent)
    {


        this.edge=edge;
        score=dis;
        if(parent!=null){
            score+=parent.score;
        }else{
            //score=100000;
        }

        this.parent=parent;
    }

}
