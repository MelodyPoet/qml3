package navigation;

import java.util.ArrayList;

public class Block {
    private Rect rect = Rect.MinMaxRect(0, 0, 0, 0);
    public ArrayList<Integer> pointIndexList;
    public ArrayList<Edge> sharedEdgeList;
    public ArrayList<Edge> outEdgeList;
    //	public	Dictionary<Block,Edge> linkedEdgeMap;
    public Vector2 center;
    ArrayList<Vector2> pointList;
    public Block(int[] ptList,ArrayList<Vector2> pointList) {
        this.pointList=pointList;
        pointIndexList = new ArrayList<Integer>();
        rect.xMin = rect.xMax = pointList.get(ptList[0]).x;
        rect.yMin = rect.yMax = pointList.get(ptList[0]).y;
        outEdgeList = new ArrayList<>();
        for (int i = 0, len = ptList.length; i < len; i++) {
            outEdgeList.add(new Edge(ptList[i], ptList[(i + 1) % len],pointList));
        }

        for (int i : ptList) {
            pointIndexList.add(i);
            Vector2 pt = pointList.get(i);
            if (rect.xMin > pt.x) {
                rect.xMin = pt.x;
            } else if (rect.xMax < pt.x) {
                rect.xMax = pt.x;
            }
            if (rect.yMin > pt.y) {
                rect.yMin = pt.y;
            } else if (rect.yMax < pt.y) {
                rect.yMax = pt.y;
            }

        }
        center =  rect.center();
        sharedEdgeList = new ArrayList<>();
    }

    public void addEdge(Edge e) {
        sharedEdgeList.add(e);
    }

    public static float Angle(Vector2 cen, Vector2 first, Vector2 second) {
        float M_PI = (float) Math.PI;

        float ma_x = first.x - cen.x;
        float ma_y = first.y - cen.y;
        float mb_x = second.x - cen.x;
        float mb_y = second.y - cen.y;
        float v1 = (ma_x * mb_x) + (ma_y * mb_y);
        float ma_val = (float) Math.sqrt(ma_x * ma_x + ma_y * ma_y);
        float mb_val = (float) Math.sqrt(mb_x * mb_x + mb_y * mb_y);
        float cosM = v1 / (ma_val * mb_val);
        float angleAMB = (float) Math.acos(cosM);//* 180 / M_PI;

        return angleAMB;
    }

    public Edge getClosedEdge(Vector2 pt) {

        Edge edge = null;
        float maxDis = Float.MAX_VALUE;

        for (Edge item : outEdgeList) {
            //	if(item.inRect(pt)==false)continue;
            float n = Vector2.Distance(pointList.get(item.pt1), pt);
            float r = Angle(pointList.get(item.pt1), pt, pointList.get(item.pt2));

            float dis = (float) Math.abs(n * Math.sin(r));
            if (dis < maxDis) {
                maxDis = dis;
                edge = item;
            }
        }


        return edge;
    }

    public boolean inRect(int x, int y, int dis) {
        //	trace("point:",x,y);
        if (!(x >= rect.xMin && x <= rect.xMax && y >= rect.yMin && y <= rect.yMax)) return false;
        Vector2 cen = new Vector2(x, y);

        float rot = 0;
        for (int i = 0; i < pointIndexList.size(); i++) {
            Vector2 p1 = pointList.get(pointIndexList.get(i));
            Vector2 p2 = pointList.get(pointIndexList.get((i + 1) % pointIndexList.size()));

            rot += Angle(cen, p1, p2);
        }
        boolean isIn = Math.abs(rot * 180 / Math.PI - 360) < 0.1;
        if (isIn && dis > 0) {
            for (Edge e : outEdgeList) {
                if (dis > PointLine_Disp(x, y, pointList.get(e.pt1).x, pointList.get(e.pt1).y, pointList.get(e.pt2).x, pointList.get(e.pt2).y)) {
                    return false;
                }
            }
        }
        return isIn;


    }

    public double PointLine_Disp(double xx, double yy, double x1, double y1, double x2, double y2) {
        double a, b, c, ang1, ang2, ang, m;
        double result = 0;
        //分别计算三条边的长度
        a = Math.sqrt((x1 - xx) * (x1 - xx) + (y1 - yy) * (y1 - yy));
        if (a == 0)
            return -1;
        b = Math.sqrt((x2 - xx) * (x2 - xx) + (y2 - yy) * (y2 - yy));
        if (b == 0)
            return -1;
        c = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        //如果线段是一个点则退出函数并返回距离
        if (c == 0) {
            result = a;
            return result;
        }
        //如果点(xx,yy到点x1,y1)这条边短
        if (a < b) {
            //如果直线段AB是水平线。得到直线段AB的弧度
            if (y1 == y2) {
                if (x1 < x2)
                    ang1 = 0;
                else
                    ang1 = Math.PI;
            } else {
                m = (x2 - x1) / c;
                if (m - 1 > 0.00001)
                    m = 1;
                ang1 = Math.acos(m);
                if (y1 > y2)
                    ang1 = Math.PI * 2 - ang1;//直线(x1,y1)-(x2,y2)与折X轴正向夹角的弧度
            }
            m = (xx - x1) / a;
            if (m - 1 > 0.00001)
                m = 1;
            ang2 = Math.acos(m);
            if (y1 > yy)
                ang2 = Math.PI * 2 - ang2;//直线(x1,y1)-(xx,yy)与折X轴正向夹角的弧度

            ang = ang2 - ang1;
            if (ang < 0) ang = -ang;

            if (ang > Math.PI) ang = Math.PI * 2 - ang;
            //如果是钝角则直接返回距离
            if (ang > Math.PI / 2)
                return a;
            else
                return a * Math.sin(ang);
        } else//如果(xx,yy)到点(x2,y2)这条边较短
        {
            //如果两个点的纵坐标相同，则直接得到直线斜率的弧度
            if (y1 == y2)
                if (x1 < x2)
                    ang1 = Math.PI;
                else
                    ang1 = 0;
            else {
                m = (x1 - x2) / c;
                if (m - 1 > 0.00001)
                    m = 1;
                ang1 = Math.cos(m);
                if (y2 > y1)
                    ang1 = Math.PI * 2 - ang1;
            }
            m = (xx - x2) / b;
            if (m - 1 > 0.00001)
                m = 1;
            ang2 = Math.cos(m);//直线(x2-x1)-(xx,yy)斜率的弧度
            if (y2 > yy)
                ang2 = Math.PI * 2 - ang2;
            ang = ang2 - ang1;
            if (ang < 0) ang = -ang;
            if (ang > Math.PI) ang = Math.PI * 2 - ang;//交角的大小
            //如果是对角则直接返回距离
            if (ang > Math.PI / 2)
                return b;
            else
                return b * Math.sin(ang);//如果是锐角，返回计算得到的距离
        }
    }
}

