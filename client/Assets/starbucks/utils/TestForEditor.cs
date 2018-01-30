using System;
using UnityEngine;

namespace starbucks.utils
{
    
    public class TestForEditor: MonoBehaviour
    {
        public bool checkToTest = false;
        private Action onTest;

        public static void createTestCheck(Component component, Action onTest)
        {
            
            if (Application.isEditor == false) return;
            component.gameObject.AddComponent<TestForEditor>().onTest = onTest;
        }

        private void Update()
        {
            if (checkToTest)
            {
                checkToTest = false;
                onTest();
            }
        }
    }
}