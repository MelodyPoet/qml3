 
using starbucks.ui.basic;

namespace modules.battleMainPage.views
{
    public class BattlePanel: BasePanel<BattleModule>
    {
        
        public override void Init()
        {
            base.Init();
            createView<BattleView>(gameObject);
            createView<BattleMyHeadView>(transform.Find("myHead").gameObject);
            createView<BattleMonsterHeadView>(transform.Find("monsterHead").gameObject);
             
 
        }
    }
}