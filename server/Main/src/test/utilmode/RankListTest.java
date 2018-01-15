package test.utilmode;

 import gluffy.utils.RankSortedList;

import java.util.Random;

/**
 * Created by admin on 2017/8/9.
 */
public class RankListTest {
    public static RankSortedList<RankVo> list = new RankSortedList<>();
    public static void main(String[] args){
        int count = 0;
        for(int i=0;i<1000000;i++){
            RankVo rankVo = null;
            Random random = new Random();
            if(Math.random()>0.5){
                rankVo = new RankVo();
                rankVo.orderIndex = -1;
            }else if(list.size() > 0){
                int index = random.nextInt(list.size());
                rankVo = list.get(index);
            }
            if(rankVo == null){
                i--;
                continue;
            }
            count++;
            int value = random.nextInt(500)+1;
//            System.out.println("==i=="+i+"===="+value);
//            for(RankListItem vo : list.list){
//                System.out.println("list==="+vo.orderScore());
//            }
            list.SortWithLimit(rankVo,100,(byte) 0,value);
            rankVo.value = value;
        }
        System.out.println("===count======="+count);
        for(RankVo vo : list.list){
            System.out.println("=========="+vo.orderIndex+"=========="+vo.orderScore());
        }
    }
}
