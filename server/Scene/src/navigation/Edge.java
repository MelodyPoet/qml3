package navigation;

import java.util.ArrayList;
import java.util.HashMap;

public class Edge {
    public int pt1;
    public int pt2;
    public Block block1;
    public Block block2;
    public Vector2 centerPoint=new Vector2();
    public Vector2 adjustPoint=new Vector2();
    public ArrayList<Edge> linkedEdges;
    public HashMap<Edge,Integer> disEdges;
    private float xMix,xMax,yMix,yMax;
    ArrayList<Vector2> pointList;
    //temp edge
    public   Edge()
    {}
    // outline edge
    public Edge(int pt1,int pt2,    ArrayList<Vector2> pointList){
        this.pointList=pointList;
        this.pt1=pt1;
        this.pt2=pt2;
        xMix=Math.min(pointList.get(pt1).x,pointList.get(pt2).x);
        xMax=Math.max(pointList.get(pt1).x,pointList.get(pt2).x);
        yMix=Math.min(pointList.get(pt1).y,pointList.get(pt2).y);
        yMax=Math.max(pointList.get(pt1).y,pointList.get(pt2).y);
    }
    //shared edge
    public   Edge(int pt1 ,int pt2 ,Block block1, Block block2, ArrayList<Vector2> pointList)
    {
        this.pointList=pointList;
        this.pt1=pt1;
        this.pt2=pt2;
        this.block1=block1;
        this.block2=block2;
        Vector2.add( pointList.get(pt1),pointList.get(pt2),centerPoint);
        centerPoint.x/=2;
        centerPoint.y/=2;
        adjustPoint.copyFrom(centerPoint);

        //Debug.Log(this);
    }
    public void calculateLinkEdges(){
        linkedEdges=new ArrayList<Edge>();
        for (Edge item : block1.sharedEdgeList) {
            if(item!=this)linkedEdges.add(item);
        }
        for (Edge item : block2.sharedEdgeList) {
            if(item!=this)linkedEdges.add(item);
        }


        //Debug.LogWarning(linkedEdges.Count);
        disEdges=new HashMap<>();
        for (Edge i : linkedEdges)
        {
            disEdges.put(i,(int)Vector2.Distance(centerPoint,i.centerPoint));
        }

    }



    public boolean belong(Block b)
    {

        return block1==b||block2==b;
    }
    public int distanceToEdge(Edge target){
        return disEdges.get(target);
        //return Point.distance(centerPoint,target.centerPoint);
    }
    public boolean inRect(Vector2 pt){
        if(pt.x<xMix||pt.x>xMax)return false;
        if(pt.y<yMix||pt.y>yMax)return false;
        return true;
    }
    public float getRot(){
        Vector2 point1=pointList.get(pt1);
        Vector2 point2=pointList.get(pt2);
        return  (float) Math.atan2(point2.y-point1.y,point2.x-point1.x);
    }
    public int getRealCrossPoint(Vector2 pt){
        if(pt.y<yMix||pt.y>yMax)return 1;
        Vector2 point1=pointList.get(pt1);
        Vector2 point2=pointList.get(pt2);
        Vector2 cross=getCross(point1,point2,new Vector2(-1000,pt.y),new Vector2(1000,pt.y));
        //Debug.Log(pt1+","+pt2+"---cross="+cross+",pt="+pt);
        return  cross.x<pt.x?2:-2;

    }
    public Vector2 getCross(Vector2 line2A,Vector2 line2B){
        Vector2 point1=pointList.get(pt1);
        Vector2 point2=pointList.get(pt2);
        return getCross(point1,point2,line2A,line2B);
    }
    private   Vector2 getCross(Vector2 line1A,Vector2 line1B,Vector2 line2A,Vector2 line2B){
        float line2_k;
        float line2_b;
        if(line1A.x==line1B.x){

            line2_k=(line2A.y-line2B.y)/(line2A.x-line2B.x);
            line2_b=line2B.y- line2_k*line2B.x;
            return new Vector2(line1A.x,line1A.x* line2_k+line2_b);
        }else if(line2A.x==line2B.x){
            return new Vector2(line2A.x,line2A.x*(line1A.y-line1B.y)/(line1A.x-line1B.x));
        }
        float line1_k=(line1A.y-line1B.y)/(line1A.x-line1B.x);
        float line1_b=line1B.y- line1_k*line1B.x;
        line2_k=(line2A.y-line2B.y)/(line2A.x-line2B.x);
        line2_b=line2B.y- line2_k*line2B.x;
        return new Vector2((line1_b-line2_b)/(line2_k-line1_k),
                (line2_k*line1_b-line1_k*line2_b)/(line2_k-line1_k));

    }
    public void adjust(Vector2 p1,Vector2 p2){
        Vector2 point1=pointList.get(pt1);
        Vector2 point2=pointList.get(pt2);
        Vector2 cross=getCross(point1,point2,p1,p2);
        //trace(cross)
        float ds0=Vector2.Distance(cross,point1);
        float ds1=Vector2.Distance(cross,point2);
        //trace(ds0,ds1,Point.distance(parr[0],parr[1]))
        if(ds0>AStar.edgeDistanse&&ds1>AStar.edgeDistanse&&ds0+ds1<=Vector2.Distance(point1,point2)+AStar.edgeDistanse){
            adjustPoint=cross;
        }else{
            if(ds0<ds1)
                Vector2.MoveTowards(point1,point2,AStar.edgeDistanse,adjustPoint);
            else
            Vector2.MoveTowards(point2,point1,AStar.edgeDistanse,adjustPoint);

        }

    }
}
