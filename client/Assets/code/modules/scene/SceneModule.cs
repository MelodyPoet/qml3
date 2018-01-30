 
using modules.scene.services;
using starbucks.ui.basic;

namespace modules.scene
{
    public class SceneModule : BaseModule
    {
        public SceneModel model;
        public override void init()
        {
            model=new SceneModel();
            RegService<SceneService>();
            base.init();
            
        }
    }
}