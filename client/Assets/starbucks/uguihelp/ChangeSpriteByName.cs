using starbucks.ui;
using UnityEngine;
using UnityEngine.UI;

namespace starbucks.uguihelp
{
    [RequireComponent(typeof(Image))]
    public class ChangeSpriteByName : MonoBehaviour {
     
        public string[]  assetsNames;
        [HideInInspector]
        public string spriteName;
        private Image img;
        public Sprite setSprite(string spriteName) {
            if (img == null)
            {
                img = GetComponent<Image>();
            }
            if (spriteName == null)
            {
                img.enabled = false;
                return null;
            }
            img.enabled = true;
            foreach (var item in assetsNames)
            {
          
                Sprite sp=     UIAssetBundleManager.getSprite (item, spriteName);
                if (sp != null) {
                    img.sprite = sp;
                    return sp;
                }
            }
            return null;
        }
    }
}
