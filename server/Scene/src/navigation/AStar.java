package navigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AStar {
    public static int scale = 10;
    public static int edgeDistanse = 20;
    private  	BinaryHeap openList;
    private   HashMap<Edge,AsNode> gridMap;

    private   boolean inited = false;

    public   ArrayList<Vector2> pointList;
    //点索引


    public	   ArrayList<Block> blockList;
    //区域索引
    private    HashMap<Block,HashMap<Block,Edge>> edgeMap;

    private   Block endBlock;

    public   void initMap(ArrayList<Vector2> pointList, int[][] rectList)
    {
         this.pointList = pointList;

        blockList = new ArrayList<Block>();
        for (int [] item : rectList)
        {
            blockList.add(new Block(item,pointList));
        }

        edgeMap = new HashMap<>();
        ArrayList edge;
        ArrayList<Edge> allEdges = new ArrayList();
        for (Block srcb : blockList)
        {
            edgeMap.put(srcb,new HashMap<>());
            for (Block desb : blockList)
            {
                if (srcb == desb)
                    continue;
                edge = new ArrayList();
                for (int i : srcb.pointIndexList)
                {
                    if (desb.pointIndexList.indexOf(i) > -1)
                    {
                        edge.add(i);

                    }
                }
                if (edge.size() == 2)
                {
                    Edge e = null;
                    if (edgeMap.containsKey(desb))
                    {
                        e = edgeMap.get(desb).get(srcb);
                    }
                    if (e == null)
                    {
                        e = new Edge((int)edge.get(0), (int)edge.get(1), srcb, desb,pointList);
                        allEdges.add(e);

                    }
                    srcb.addEdge(e);

                    edgeMap.get(srcb).put(desb,  e);
                }

            }



        }
        for (Edge j : allEdges)
        {
            j.calculateLinkEdges();
        }
        inited = true;

    }

    public   void clear()
    {
        inited = false;
    }


    public   ArrayList<Vector2> find(Vector2 startPt, Vector2 endPt)
    {
        gridMap=null;
        openList=null;
       // finalEndPt = endPt;
        if (inited == false)
            return null;
        Block startBlock = getBlock((int)startPt.x, (int)startPt.y,0);
        if (startBlock == null)
        {

            return null;
        }
        ArrayList<Vector2> rstList;
        //endBlock=getBlock(endPt.x,endPt.y);
        endBlock = getBlock((int)endPt.x, (int)endPt.y,0);
        //out   walkable rect
        if (endBlock == null)
        {

            Vector2 pt = endPt;
            Vector2[] dirs = new Vector2[]{ new Vector2(1, 0), new Vector2(0, 1), new Vector2(-1, 0), new Vector2(0, -1) };
            int dircount = 0;
            int step = 1;
            int total = 100;
            float stepLength = 10;
            for (int i = 1; i < total; i++)
            {
                if (i == (step + 1) * (step + 1) - 1)
                    step++;
                if (i % step == 0)
                {
                    Vector2 v=new Vector2();
                    Vector2.multiply( dirs[dircount++ % 4], stepLength,v);
                    Vector2.add(pt, v,pt);
                }
                else
                {
                    Vector2 v=new Vector2();
                    Vector2.multiply( dirs[dircount% 4], stepLength,v);
                    Vector2.add(pt, v,pt);
                }
                endBlock = getBlock((int)pt.x, (int)pt.y, 1);
                if (endBlock != null)
                {
                      endPt.copyFrom( pt);

                    break;
                }
            }


        }

        if (endBlock == null)
        {
         //   Debug.Log("point end null" + (int)endPt.x + "," + (int)endPt.y);
            return null;
        }

        if (startBlock == endBlock)
        {
            rstList = new ArrayList<>();
            rstList.add(new Vector2(endPt));
            return rstList;
        }
        //	Debug.Log("endBlock "+blockList.IndexOf( endBlock));
        openList = new  BinaryHeap();
        gridMap = new HashMap<>();
        for (Edge i : 	startBlock.sharedEdgeList)
        {
            open(i, null, (int)Vector2.Distance(startPt, i.centerPoint), true);

        }


        while (true)
        {
            int len = openList.length();

            if (len == 0)
            {

                return null;
            }
            AsNode c_grid = openList.popMix();

            if (c_grid.edge.belong(endBlock))
            {
                List<Edge> rst =	getRst(c_grid);
                if (rst != null)
                {
                    Vector2 p1 = startPt;
                    Vector2 p2;
                    for (int k = 0, lenk = rst.size(); k < lenk; k++)
                    {
                        p2 = k + 1 == lenk ? endPt : rst.get(k + 1).adjustPoint;
                        rst.get(k).adjust(p1, p2);
                        p1 = rst.get(k).adjustPoint;
                    }


                }
                rstList = new ArrayList<>();
                for (Edge item : rst)
                {
                    rstList.add(new Vector2(item.adjustPoint.x, item.adjustPoint.y));
                }
                rstList.add(new Vector2(endPt));
                return rstList;
            }
            //c_grid.closed=true;
            Edge edge = c_grid.edge;
            //Debug.Log("edge "+edge.ToString());
            int dis;
            for (Edge j : edge.linkedEdges)
            {

                //if(j==edge){continue;}
                AsNode newGrid = null;

                if (gridMap.containsKey(j) == false)
                {

                    dis = edge.distanceToEdge(j);
                    open(j, c_grid, dis, true);
                }
                else
                {

                    newGrid = gridMap.get(j);
                    if (newGrid.closed)
                        continue;
                    dis = edge.distanceToEdge(j);
                    if (newGrid.score > c_grid.score + dis)
                    {

                        open(j, c_grid, dis,false);
                    }
                }
            }

        }//for over
        // return null;
    }


    private   void open(Edge edge, AsNode parent, int dis, boolean create )
    {
        //	Debug.LogWarning("open"+edge+","+create);
        AsNode grid;
        if (create)
        {
            grid = new  AsNode(edge, dis, parent);
            gridMap.put(edge , grid);
            openList.push(grid);
        }
        else
        {
            grid = gridMap.get(edge);
            grid.parent = parent;
            openList.updata(grid, parent.score + dis);
        }

        grid.closed = true;

    }

    private   ArrayList<Edge> getRst(AsNode grid)
    {
        ArrayList<Edge> return_dt = new ArrayList<>();


        do
        {
            return_dt.add(0, grid.edge);
            grid = grid.parent;
        } while(grid != null);

        return (return_dt.size() > 0 ? return_dt : null);
    }
    public   Block getBlock(int px, int py){
       return  getBlock( px,  py, 0);
    }
    public   Block getBlock(int px, int py, int dis)
    {

        for (Block b : blockList)
        {
            if (b.inRect(px, py, dis))
                return b;
        }
        return null;
    }
}
