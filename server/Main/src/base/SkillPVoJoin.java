package base;

import comm.BaseVoJoin;
import protocol.SkillPVo;

public   class SkillPVoJoin extends BaseVoJoin<SkillPVo> {
      public static SkillPVoJoin instance = new SkillPVoJoin();

      @Override
      public int fromSplitStr(SkillPVo vo, String[] str, int start) {
return 1;
      }

      @Override
      public int fromSplitStr(SkillPVo vo, int[] str, int start) {
          vo.baseID = str[start++];
          vo.level = (byte) str[start++];
          return 2;
      }

      @Override
      public String toSplitStr(SkillPVo vo) {
          return vo.baseID+","+vo.level+",";
      }


  }
