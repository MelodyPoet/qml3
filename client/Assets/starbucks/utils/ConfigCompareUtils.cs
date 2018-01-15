using System;
using System.Collections.Generic;

namespace starbucks.utils
{
    public class ConfigCompareUtils
    {
        public enum COMPARE_ENUM
        {
            COMPARE_TRUE = -1, COMPARE_EQUAL = 0,
            COMPARE_EQUAL_OR_BIG = 1,COMPARE_BIG = 2, COMPARE_EQUAL_OR_LESS = 3,COMPARE_LESS = 4
        }
        public static Dictionary<String,COMPARE_ENUM> COMPARE_MAP=new Dictionary<String,COMPARE_ENUM>()
        {
            {"=",COMPARE_ENUM.COMPARE_EQUAL},
            {">=",COMPARE_ENUM.COMPARE_EQUAL_OR_BIG},
            {">",COMPARE_ENUM.COMPARE_BIG},
            {"<=",COMPARE_ENUM.COMPARE_EQUAL_OR_LESS},
            {"<",COMPARE_ENUM.COMPARE_LESS}

        };

        public static bool compare(int a, int b, int mode) {
            return  compare(  a,   b, (COMPARE_ENUM) mode);
        }
        public static bool compare(int a, int b, COMPARE_ENUM mode) {
            switch (mode) {
                case COMPARE_ENUM.COMPARE_TRUE:
                    return true;
                case COMPARE_ENUM.COMPARE_EQUAL:
                    return a == b;
                case COMPARE_ENUM.COMPARE_EQUAL_OR_BIG:
                    return a >= b;
                case COMPARE_ENUM.COMPARE_EQUAL_OR_LESS:
                    return a <= b;
                case COMPARE_ENUM.COMPARE_BIG:
                    return a > b;
                case COMPARE_ENUM.COMPARE_LESS:
                    return a < b;
            }
            return false;
        }
    }
}