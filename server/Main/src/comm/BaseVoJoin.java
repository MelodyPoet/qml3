package comm;



import java.util.Collection;

/**
 * Created by jackie on 14-8-8.
 */
public abstract class    BaseVoJoin <T> {
     public String joinCollection(Collection<T> items){
         StringBuffer sb=new StringBuffer();
         for (T vo: items) {
             sb.append(toSplitStr(vo));
         }
         return sb.toString();
     }


    public  abstract     int fromSplitStr(T vo,String str[],int start);
    public  abstract     int fromSplitStr(T vo,int str[],int start);


    public    abstract  String toSplitStr(T vo) ;

}
