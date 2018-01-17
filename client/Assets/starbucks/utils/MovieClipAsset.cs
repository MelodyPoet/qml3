using UnityEngine;

namespace starbucks.utils
{
    [CreateAssetMenu(fileName = "Asset", menuName = "create MovieClipAsset")]
    public class MovieClipAsset : ScriptableObject
    {
        public Sprite[] sprites;
        public Vector3 offsetPos;
    }
}