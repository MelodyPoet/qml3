package navigation;

public class Vector2 {
    public float x;
   public float y;
    public Vector2() {
x=0;
y=0;
    }
    public Vector2(Vector2 target) {
x=target.x;
y=target.y;
    }
    public Vector2(float x, float y) {
        this.x=x;
        this.y=y;
    }
    public void copyFrom(Vector2 target) {
        x=target.x;
        y=target.y;
    }

    public  static void  add(Vector2 v1,Vector2 v2,Vector2 result){

        result.x=v1.x+v2.x;
        result.y=v1.y+v2.y;

   }
public boolean isZero(){return x==0&&y==0;}
    public static float Distance(Vector2 pt1, Vector2 pt2) {
    return (float) Math.sqrt( (pt2.x-pt1.x)*(pt2.x-pt1.x)+(pt2.y-pt1.y)*(pt2.y-pt1.y));
    }

    public static void MoveTowards(Vector2 pt1, Vector2 pt2, float edgeDistanse,Vector2 result) {
         float dis=Distance(pt1,pt2);
         if(edgeDistanse>=dis){
             result.copyFrom(pt2);
         }else if(edgeDistanse<=0){
             result.copyFrom(pt1);
         }else{
             float percent=edgeDistanse/dis;
             result.x=pt1.x+(pt2.x-pt1.x)*percent;
             result.y=pt1.y+(pt2.y-pt1.y)*percent;
         }
    }

    public static void multiply(Vector2 pt, float value, Vector2 result) {
        result.x=pt.x*value;
        result.y=pt.y*value;
    }
    public   float   magnitude(){
        return (float) Math.sqrt( x*x+y*y);
    }
    public boolean equalsWith(Vector2 tgPos) {
        return  x==tgPos.x&&y==tgPos.y;
    }

    public void set(float x, float y) {
        this.x=x;
        this.y=y;
    }

    public void clear() {
        x=y=0;
    }

    public static float Magnitude(float dx, float dy) {
        return (float)Math.sqrt(dx*dx+dy*dy);
    }
}
