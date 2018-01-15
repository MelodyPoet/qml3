package comm;

import flushnpc.BaseFlushNpc;
import gluffy.udp.core.BaseRspd;
import gluffy.utils.JkTools;
import navigation.AStar;
import navigation.Vector2;
import protocol.MltSceneDeleteRspd;
import sceneRole.Hero;
import sceneRole.Npc;
import table.MapBaseVo;

import java.util.ArrayList;
import java.util.HashMap;

public class Scene {
    public static int CellSize=10000*50;//50米   1m=10000
    public static int CastCells=3;//3x3 只有奇数有效
    public MapBaseVo mapBaseVo;
    public int offsetX=  550000;
    public int offsetY = 700000;
    public int cellRow=  100;
    public int cellColumn = 100;
    public  static   HashMap<Integer,HashMap<Integer,Scene>> allScenes=new HashMap<>();//场景类型 和场景id 最后改成 场景baseID+线ID
    public  int heroCount=0;
    private  int heroCountChange=0;
    public SceneCell[][] allCells;
    public  int sceneID;
    private AStar navigator;
    public BaseFlushNpc flushNpc;
    public HashMap<Long,Npc> allNpc=new HashMap<>();
    private Scene(int mapID, AStar navigator){

        mapBaseVo=Model.MapBaseMap.get(mapID);
        offsetX=-mapBaseVo.senceRect[0]*10000;
        offsetY=-mapBaseVo.senceRect[1]*10000;
        cellColumn=mapBaseVo.senceRect[2]*10000/CellSize;
        cellRow=mapBaseVo.senceRect[3]*10000/CellSize;
       allCells=new SceneCell[cellColumn][cellRow];
        HashMap<Integer,Scene> sceneMap= allScenes.get(mapID);
        flushNpc=new BaseFlushNpc(this);
        if(sceneMap!=null){
            this.sceneID=sceneMap.size();
        }else {
            this.sceneID=0;
        }
       this.navigator=navigator;

       addToAll();

        if(mapID==997) {

//            for (int i = 0; i < 5; i++) {
//                for (int j = 0; j < 5; j++) {
//                    Npc npc = new Npc();
//                    npc.tempID = -i * 10 + j;
//                    npc.baseID = 100;
//                    npc.level = 6;
//                    npc.posx = -176000 + j * 100000 + 50000;
//                    npc.posz = -118000 + i * 100000 + 50000;
//
//                    enter(npc);
//                }
//            }
        }
        int [] ary= null;
        if(mapID==998||mapID==999) {
          // ary=JkTools.readArray("-210|-550|0");
           // ary=JkTools.readArray("-209_-300_0|-198_-521_0|-537_-219_0|-178_-520_0|-154_-535_0|-574_-234_0|-213_-561_0|-514_-235_0|-549_-262_0|-189_-563_0|-173_-542_0|-233_-298_0|-558_-220_0|-533_-242_0|-217_-258_0|-174_-272_0|-193_-280_0|-197_-257_0|-573_-260_0|-234_-271_0|-215_-534_0|-198_-544_0");
            flushNpc.start();
        }
        if(mapID==1) {
           // ary=JkTools.readArray("5_-604_0|192_-560_0|460_-883_0|417_-736_0|-136_-957_0|-202_-931_0|-193_-1054_0|261_-412_0|-25_-470_0|161_-390_0|-316_-1421_0|-258_-1324_0|-324_-1298_0|339_-1135_0|-220_-1283_0|405_-1161_0|218_-888_0|160_-985_0|-212_-1406_0|-153_-1309_0|347_-1258_0|153_-863_0|-212_-1593_0|-153_-1496_0|-220_-1471_0");
        flushNpc.start();
        }



    }
    private  void addToAll(){
        HashMap<Integer,Scene> sceneMap= allScenes.get(mapBaseVo.ID);
        if(sceneMap==null){
            sceneMap=new HashMap<>();
            allScenes.put(mapBaseVo.ID,sceneMap);
        }
        sceneMap.put(sceneID,this);
    }

    public  static  Scene getOrCreateNewScene(int mapID, int maxClientCount){
        HashMap<Integer,Scene> sceneMap= allScenes.get(mapID);
        AStar navigator=null;
        if(sceneMap==null||sceneMap.size()==0){
            navigator=null;
            double[] vectors=null;
            String[] rectStr=null;
            if(mapID==998) {
                navigator = new AStar();
                  vectors = new double[]{-138,3774,245,3207,-66,3047,-277,3043,-434,3294,-478,3539,-347,3742,-1041,3157,-699,3092,-999,2757,-1341,2822,100,2743,209,2748,367,2517,198,2247,361,2023,168,2002,-122,1881,230,1645,-350,1914,-185,1610,63,1434,-97,2284,-313,2344,-220,2721,-606,2379,-811,2635,-588,2912,-353,2891,-2714,2427,-2960,2419,-2938,2764,-2475,2890,-2507,2560,-2167,2634,-2381,2445,-1677,2693,-2201,2233,-1271,2496,-1962,2041,-1495,2188,-2154,1832,-1468,1939,-1660,1834,-1532,1706,-1286,1644,-1734,1361,-1564,1381,-1660,1585,-2115,1539,-2979,2152,-2895,1585,-3295,899,-3516,1313,-3581,1562,-3178,2068,-2890,601,-2623,947,-2690,1380,-3893,1020,-3883,660,-1300,1470,-1449.4,1355.5,-1352,1271,-1341,1100,-1538,1025,-1648,1178,-1194,932,-854,981,-1084,1246,-946,810,-669,667,-818,563,-987,535,-690,829,-1085,779,-1220,663,-1364,777,-1203,526,-1360,527,-1930,2970,-1557,2963,-1954,3161,-1638,3263};
                rectStr = "0_1_2_3_4,4_5_6_0,4_5_7_8,9_10_7_8,11_2_1_12,12_13_14_11,13_15_16_14,16_17_18_15,17_19_20_21_18,17_22_23_19,23_24_22,23_25_26_27_28_24,9_8_27_26,29_30_31_32_33,34_32_33,34_33_35,34_36_37_35,36_10_9_38,39_37_36_38_40,41_39_40_42_43,42_43_44,44_42_45,46_47_45_44_48,41_43_49,37_35_50_51,52_53_54_55,52_56_57,52_55_50_51,57_52_51_58,57_49_41_58,59_53_52_60,47_45_61,61_62_47,61_63_62,63_64_65_66_62,67_68_69_63_64,70_71_72_73,70_68_74_71,70_75_67_68,75_76_77_67,76_78_79_77,80_36_10_81,80_82_83_81".split(",");
            }
            if(mapID==999) {
                navigator = new AStar();
             //   -575,-815,-246,-823,-200.7,-682.3,-364,-495,-612,-419,-75,-670,67,-693,213,-814,52,-609,72,-556,103,-597,137,-677,187,-697,352,-653,523,-713,364,-800,-14.1,-427,-37,-520.5,-17,-572.6,-72.7,-647.6,-96.3,-612.6,-132,-556.2,-179.8,-581.9,-129.8,-636.2,-219.1,-587.6,-258.4,-539,-329.1,-476.2,-156.3,-516.9,-90.60001,-453.3,-111.3,-401.9,-199.1,-493.3,-74.8,-363.3,-3.4,-288.3,54.4,-352.6,34,-435.7,124.4,-436.2,134.4,-515.5,202.2,-540.5,274.3,-548.4,255.7,-494.1,201.4,-340.6,161.4,-403.4,-90.60001,-453.3,-38.5,-439.1,-89.5,-538.2,-69.2,-590.7,47.1,-247.2,172.5,-636.4,284,-570.4,363.7,-559.3,449.4,-602.2,570.3,-482.2,589.2,-634.8,694.7,-507.9,13.9,-472.1,-286,-568,-471.2,-432.7,-459.7,-394.3,-264.2,-422.4,-172.4,-336.1,-116.2,-282.3,-50.5,-200,6.5,-145.4,108.6,-39.4,178.2,-121.7,-446.4,-296.3,-364.9,-303.4,-299,-321.4,-313.1,-403.7,-306.8,-360.6,-208,-308.1,-247.2,-285.4,-311.5,-196.8,-349.9,-213.3,-422,-146.7,-470.6,-151.4,-611,-168.7,-472.2,-287.1,-219.1,-183.3,-178.7,-237.2,-120,-159.2,-123.9,-89.89999,-27.6,-163.1,-163.4,-6.1,-170.1,-131.3,-288,-103,-258.7,17,-303,5,-377.1,-112,-482.1,-64.8,-599.6,-18.6,-588,140.3,-452.2,135.5,-508.1,282.8,-365.6,255.8,-268.3,335.7,-374.2,416.6,-353,89,-412,-3,-393,23,-284,110,-284,110,-284,110,-218,34,-396,140,-325,-68,-16,80,89,86,156,-8,-6,137,76.9,138.5,151.7,119.4,123.7,160.7,79,277.8,181,214.9,160.2,290.9,133,192.5,111.5,334.1,49.7,309.1,-267.6,486.8,-211.4,370.6,-15,421.8,-2.1,531.8,-160.9,323.9,-183.7,265.8,-0.2,353.4,-74,365.4,19.7,393.3,69.1,388.1,-53.4,201.2,-110.8,202.5,-141.5,248.2,-78.3,258.8,-15.59999,268.7,9.3,211.1,223.9,401.1,286.2,538.7,123.6,537.3,104.1,406.9,208.8,319,241.6,341.8,218.6,368.1,284.4,360.6,328.9,398.7,369.4,535.1,739.5,127.1,754.7,283.4,744.7,494.7,687.3,520.5,545,541.5,415.6,361.2,360.6,406.9,540.4,219.6,439.6,276.9,621.8,152.3,320.7,311.4,350,309.7,251,255.2,278.5,266.3,229.3,174.4,296.6,231.2,202.3,183.6,350.7,195.6,405.6,250,492.4,214.9,459,189.7,333.2,29.1,260.9,117,423.9,141.7,232.8,77.8,204.1,127.6,304.1,0.09999999,200.8,-85.3,604.2,117.7,470.1,115.4,601.9,24.8,505.6,21.8,445,66.5,281,-124.9,219.7,-173.6,321,-155.7,320,-237.1,282.3,-196.4,370.6,-87.2,335.9,-64.4,399.3,5.9,751.8,18.8,624.8,-147.9,750.9,-159.8,739,-400,610,-415,748,-255,624.8,-208.4,565.2,-120.1,504.6,-56.6,533.1,-421.7,443.2,-390.5,488.1,-352,540.4,-367.6,438.6,-131.1,457.9,-74.5,528.5,-180.6,474.4,-218.2,412,-241.1,576.2,-295.2,532.2,-315.4,406.5,-297.2,456.9,-276.6
                vectors = new double[]{-575,-815,-246,-823,-200.7,-682.3,-364,-495,-612,-419,-75,-670,67,-693,213,-814,52,-609,72,-556,103,-597,137,-677,187,-697,352,-653,523,-713,364,-800,-14.1,-427,-37,-520.5,-17,-572.6,-72.7,-647.6,-96.3,-612.6,-132,-556.2,-179.8,-581.9,-129.8,-636.2,-219.1,-587.6,-258.4,-539,-329.1,-476.2,-156.3,-516.9,-90.60001,-453.3,-111.3,-401.9,-199.1,-493.3,-74.8,-363.3,-3.4,-288.3,54.4,-352.6,34,-435.7,124.4,-436.2,134.4,-515.5,202.2,-540.5,274.3,-548.4,255.7,-494.1,201.4,-340.6,161.4,-403.4,-90.60001,-453.3,-38.5,-439.1,-89.5,-538.2,-69.2,-590.7,47.1,-247.2,172.5,-636.4,284,-570.4,363.7,-559.3,449.4,-602.2,570.3,-482.2,589.2,-634.8,694.7,-507.9,13.9,-472.1,-286,-568,-471.2,-432.7,-459.7,-394.3,-264.2,-422.4,-172.4,-336.1,-116.2,-282.3,-50.5,-200,6.5,-145.4,108.6,-39.4,178.2,-121.7,-446.4,-296.3,-364.9,-303.4,-299,-321.4,-313.1,-403.7,-306.8,-360.6,-208,-308.1,-247.2,-285.4,-311.5,-196.8,-349.9,-213.3,-422,-146.7,-470.6,-151.4,-611,-168.7,-472.2,-287.1,-219.1,-183.3,-178.7,-237.2,-120,-159.2,-123.9,-89.89999,-27.6,-163.1,-163.4,-6.1,-170.1,-131.3,-288,-103,-258.7,17,-303,5,-377.1,-112,-482.1,-64.8,-599.6,-18.6,-588,140.3,-452.2,135.5,-508.1,282.8,-365.6,255.8,-268.3,335.7,-374.2,416.6,-353,89,-412,-3,-393,23,-284,110,-284,110,-284,110,-218,34,-396,140,-325,-68,-16,80,89,86,156,-8,-6,137,76.9,138.5,151.7,119.4,123.7,160.7,79,277.8,181,214.9,160.2,290.9,133,192.5,111.5,334.1,49.7,309.1,-267.6,486.8,-211.4,370.6,-15,421.8,-2.1,531.8,-160.9,323.9,-183.7,265.8,-0.2,353.4,-74,365.4,19.7,393.3,69.1,388.1,-53.4,201.2,-110.8,202.5,-141.5,248.2,-78.3,258.8,-15.59999,268.7,9.3,211.1,223.9,401.1,286.2,538.7,123.6,537.3,104.1,406.9,208.8,319,241.6,341.8,218.6,368.1,284.4,360.6,328.9,398.7,369.4,535.1,739.5,127.1,754.7,283.4,744.7,494.7,687.3,520.5,545,541.5,415.6,361.2,360.6,406.9,540.4,219.6,439.6,276.9,621.8,152.3,320.7,311.4,350,309.7,251,255.2,278.5,266.3,229.3,174.4,296.6,231.2,202.3,183.6,350.7,195.6,405.6,250,492.4,214.9,459,189.7,333.2,29.1,260.9,117,423.9,141.7,232.8,77.8,204.1,127.6,304.1,0.09999999,200.8,-85.3,604.2,117.7,470.1,115.4,601.9,24.8,505.6,21.8,445,66.5,281,-124.9,219.7,-173.6,321,-155.7,320,-237.1,282.3,-196.4,370.6,-87.2,335.9,-64.4,399.3,5.9,751.8,18.8,624.8,-147.9,750.9,-159.8,739,-400,610,-415,748,-255,624.8,-208.4,565.2,-120.1,504.6,-56.6,533.1,-421.7,443.2,-390.5,488.1,-352,540.4,-367.6,438.6,-131.1,457.9,-74.5,528.5,-180.6,474.4,-218.2,412,-241.1,576.2,-295.2,532.2,-315.4,406.5,-297.2,456.9,-276.6};
                rectStr = "7_6_5_2_1,6_8_9_10_11,7_12_11_6,7_15_14_13_12,2_23_19_5,2_23_22_24,30_27_21_22_24_25,21_44_43_42_27,19_23_20,44_21_20_45,8_19_20_45,9_8_45_18,34_33_16,31_29_42_43,10_37_36_9,37_39_41_35_36,37_38_39,41_33_34_35,32_33_41_40_46,11_12_47_10,12_13_50_49_48_47,13_14_52_50,53_52_50_51,54_17_18_9,34_54_17_16,2_55_3_0_1,0_3_56_4,26_3_55_25,25_30_58_26,30_29_59_58,29_31_60_59,31_32_61_60,46_64_63_62,76_4_56_57_77,57_65_77,57_68_69_66_65,57_26_58_68,69_67_66,67_69_70_71,66_73_74_65,76_77_75,95_94_93_96,91_92_94_93,89_92_91_90,76_75_89_90,75_74_88_89,74_73_72_88,59_60_79_70,70_79_78_71,71_78_72,78_72_88_85,78_79_80_81_84,81_82_61_80,84_81_83,101_97_104,102_103_86_87_97,85_86_87,85_105_87,88_85_105,88_98_99_105,32_46_62_82_61,63_106_107_108,110_109_106_107,110_107_111_112,115_113_116_114,114_111_112_116,117_115_113_118,119_120_121_122,119_96_95_120,95_120_123_124,124_125_126_123,125_127_126,117_118_125_127_128,129_130_131_132,132_133_134_129,110_109_129_134,134_113_118_133,135_136_137_138,122_137_138_128_127_121,115_139_140_141_117,142_140_141_135,142_143_144_136_135,145_146_147_148_149_144,150_145_144_151,152_145_150_153,145_152_154,144_151_143,142_155_156_143,139_157_158_140,159_160_158_157_161,162_163_156_155_158_160,164_165_163_153,153_152_164,159_167_169_170_161,171_172_108_169,171_166_167_169,162_168_166_167,168_165_163_162,168_165_173_174,173_175_176_177_174,108_63_64_172,172_178_179_64,180_181_182_178,179_178_182,180_183_184_178,184_183_185_166_171,177_166_185_176,186_145_154_173_175,175_187_188_186,51_53_189_190,189_191_192_190,191_192_187_188,193_194_176_175,195_196_197_198,199_183_185_200,200_199_201_193_194,192_201_193_187,201_202_203_199,201_204_205_202,207_206_203_202,197_196_206_207".split(",");
            }
            if(navigator!=null){
                ArrayList<Vector2> vector2s=new ArrayList<>();
                for (int i = 0; i <vectors.length/2 ; i++) {
                    vector2s.add(new Vector2((float) vectors[i*2],(float) vectors[i*2+1]));
                }
                int [][] rect=new int[rectStr.length][];
                for (int i = 0; i < rect.length; i++) {
                    rect[i]= JkTools.readArray(rectStr[i]);
                }
                navigator.initMap(vector2s,rect);
            }

        }
        if(sceneMap!=null) {

            for (Scene scene :
                    sceneMap.values()) {
                navigator=scene.navigator;
                if (scene.heroCount < maxClientCount) {
                    return scene;
                }

            }
        }
        return new Scene(mapID,navigator);
    }

    public void exit(Hero hero) {

    SceneCell cell=    hero.currentCell;
    if(cell.allHeros!=null) ;
        cell.allHeros.remove(hero);
        hero.currentCell=null;
        updateHeroCount();

        hero.scene=null;
        BaseRspd.tempCast(cell.getCastTargetsNear(0));
        new MltSceneDeleteRspd(null, hero.tempID);
    }

    private void updateHeroCount() {
        heroCountChange++;
        if (heroCountChange > 10) {
            heroCountChange = 0;
            heroCount = 0;
            for (SceneCell[] cells : allCells) {
                if (cells == null) continue;
                for (SceneCell cell : cells) {
                    if (cell != null && cell.allHeros != null) heroCount += cell.allHeros.size();
                }
            }

        }

    }
    public SceneCell getCellByPos(int posx, int posz,boolean createOnNull) {
        posx+=offsetX;
        posz+=offsetY;
        int cellX=posx/CellSize;
        int cellY=posz/CellSize;
        if(cellX<0||cellX>=cellColumn||cellY<0||cellY>=cellRow){
            return null;
        }
        SceneCell cell= allCells[cellX][cellY];
        if(createOnNull&&cell==null)
            cell= allCells[cellX][cellY]=new SceneCell(cellX,cellY,this);
            return cell;
    }

    public void enter(Hero hero) {
        hero.scene=this;

        SceneCell cell= getCellByPos(hero.posx,hero.posz,true);
        if(cell==null)return;

        cell.enterCell(hero);
        updateHeroCount();

    }
    public void enter(Npc npc) {
        npc.scene=this;
        npc.bornX=npc.posx;
        npc.bornZ=npc.posz;
        npc.bornDir=npc.dir;
      //  allClients.put(client.guid, client);
        SceneCell cell= getCellByPos(npc.posx,npc.posz,true);
        if(cell==null)return;
        cell.enterCell(npc);

    }


    public   ArrayList<Vector2> findPath(Vector2 startPt, Vector2 endPt){
        return navigator.find(startPt,endPt);
    }
}
