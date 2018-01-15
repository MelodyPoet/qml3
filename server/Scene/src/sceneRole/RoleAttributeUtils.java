package sceneRole;

import table.AttributeEnum;

public class RoleAttributeUtils {
    public static int[] getAttrs(int[] configAttrs)
    {
        int[] attrs = new int[AttributeEnum.MAX];
        if (configAttrs == null)
            return attrs;

        for (int i = 0; i < configAttrs.length; i += 2)
        {
            if(configAttrs[i]>=AttributeEnum.MAX)continue;
            attrs[configAttrs[i]] = configAttrs[i + 1];
        }
        return attrs;
    }
}
