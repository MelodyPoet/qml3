package navigation;

public class Rect {
    //
    // 摘要:
    //     ///
    //     The maximum Y coordinate of the rectangle.
    //     ///
    public float yMax ;
    //
    // 摘要:
    //     ///
    //     The maximum X coordinate of the rectangle.
    //     ///
    public float xMax ;
    //
    // 摘要:
    //     ///
    //     The minimum Y coordinate of the rectangle.
    //     ///
    public float yMin ;
    //
    // 摘要:
    //     ///
    //     The minimum X coordinate of the rectangle.
    //     ///
    public float xMin ;
    private Vector2 _center=new Vector2();
    public static Rect MinMaxRect(float xmin, float ymin, float xmax, float ymax){
        Rect rect=new Rect();
        rect.xMax=xmax;
        rect.xMin=xmin;
        rect.yMax=ymax;
        rect.yMin=ymin;
        return  rect;
    }
    //
    // 摘要:
    //     ///
    //     The position of the center of the rectangle.
    //     ///
    public Vector2 center (){
        _center.x=(xMax+xMin)/2;
        _center.y=(yMax+yMin)/2;
return _center;
    };
}
