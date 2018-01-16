package comm;

import gluffy.comm.IBytes;
import gluffy.utils.JkTools;
import org.jdom2.Document;
import table.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.*;

public class Model extends qmshared.Model {

    public  static HashSet<Integer> mergeSet=new HashSet<>();
      public static void init() throws Exception {
        qmshared.Model.init();
    }
}
