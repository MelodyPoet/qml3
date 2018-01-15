using UnityEngine;

namespace starbucks.utils
{
    public class TransformUtils  {

        public static T AddComponentOnce<T>(GameObject gameObject) where T : MonoBehaviour
        {
            T t = gameObject.GetComponent<T>();
            return t != null ? t : gameObject.AddComponent<T>();
        }
        public static void resetMatrix(Transform tm)
        {
            tm.localPosition = Vector3.zero;
            tm.localScale = Vector3.one;
            tm.localRotation = Quaternion.identity;
        }
    }
}
