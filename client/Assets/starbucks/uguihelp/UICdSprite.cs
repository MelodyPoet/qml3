using System;
using UnityEngine;
using UnityEngine.UI;

namespace starbucks.uguihelp
{
    public class UICdSprite : MonoBehaviour
    {
        //public UISprite grayIcon;
        Image sprite;
        private float seconds, total = 1;
        bool isRun = false;
        public Action OnCdStart;
        public Action OnCdStop;

        public int maxCount = 0;
        int _leftCount = 0;
        public int leftCount
        {
            get
            {
                return _leftCount;
            }
            set
            {
                _leftCount = value;
                if (maxCount > 1)
                {
                    if (_leftCount <= 0)
                    {
                        if (showInCding != null)
                            showInCding.SetActive(true);
                    }
                }
            }
        }
        public GameObject showInCding;
        public GameObject hideInCding;
        public bool fillRevers = false;
        public bool disableOnStop = false;

        void Awake()
        {
            sprite = GetComponent<Image>();


            fillAmount = 0;
        }

        public float fillAmount
        {
            set
            {
                if (sprite != null)
                {
                    if (fillRevers)
                        sprite.fillAmount = 1 - value;
                    else
                        sprite.fillAmount = value;
                }
            }
        }

        public void init(int max)
        {
            maxCount = max;
            leftCount = max;
        }

        public void startCD(float seconds, float total = 0, bool needMinus = true)
        {
            if (gameObject.activeSelf == false)
                gameObject.SetActive(true);
            this.total = total <= 0 ? seconds : total;
            this.seconds = seconds;

            if (maxCount > 1 && needMinus)
            {
                leftCount--;
                if (leftCount <= 0)
                    leftCount = 0;
            }

            if (leftCount > 0)
                fillAmount = 0;
            else
                fillAmount = 1;


            if (OnCdStart != null)
                OnCdStart.Invoke();
            isRun = true;
            if (maxCount <= 1)
            {
                if (showInCding != null)
                    showInCding.SetActive(true);
            }

            if (hideInCding != null)
                hideInCding.SetActive(false);
        }

        public void stopCD()
        {
            //if (isReady)
            //return;
            stop();
        }


        void Update()
        {
            if (isRun == false)
                return;

            fillAmount = seconds / total;
            if (seconds <= 0)
            {
                stop();
            }


            seconds -= Time.deltaTime;

        }



        public bool isReady
        {
            get
            {
                if (maxCount > 1)
                    return leftCount > 0;
                else
                    return seconds <= 0;
            }
        }


        void stop()
        {
            seconds = 0;
            if (showInCding != null)
                showInCding.SetActive(false);
            if (hideInCding != null)
                hideInCding.SetActive(true);

            fillAmount = 0;
            leftCount++;
            if (leftCount >= maxCount)
                leftCount = maxCount;

            isRun = false;
            if (OnCdStop != null)
            {
                OnCdStop.Invoke();
            }
            if (disableOnStop)
            {

                gameObject.SetActive(false);
            }

            if (leftCount >= maxCount)
                gameObject.SetActive(false);
        }
    }
}
