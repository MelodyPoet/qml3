using System.Collections;
using System.Collections.Generic;
using System.Reflection;
using starbucks.basic;
using UnityEngine;

namespace starbucks.socket.http
{
    public class HttpService  
    {



        private Queue eventQueue;
         public static readonly HttpService instance = new HttpService();
        private string url;

        public bool isSending { get { return willSendBytes != null; } }

        public ByteArray lastSendRqst;
        public float lastSendTime;
        private MonoBehaviour mainBehaviour;
        private ByteArray willSendBytes;

        List<ByteArray> nextRqstList = new List<ByteArray>();

        public delegate void TempCallback(object[] events);

        public TempCallback tempCallback;

        public HttpService()
        {

             eventQueue = Queue.Synchronized(new Queue());

        }

        public void getIPInfor(out string ip, out int port)
        {
            int pos1 = "http://".Length;
            int pos2 = url.LastIndexOf(":");
            ip = url.Substring(pos1, pos2 - pos1);
            port = int.Parse(url.Substring(pos2 + 1, url.LastIndexOf("/") - pos2 - 1));
            //Debug.LogError(ip + "---" + url.Substring(pos2 + 1, url.LastIndexOf("/") - pos2 - 1));
        }

        public void init(MonoBehaviour mainBehaviour)
        {

            if (this.mainBehaviour == null)
                mainBehaviour.StartCoroutine(sendRealLoop());
            this.mainBehaviour = mainBehaviour;

        }

        public void changeUrl(string url)
        {
            this.url = url;
        }

        public string getUrl(bool forRoot)
        {
            if (forRoot)
            {
                return url.Substring(0, url.Length - 5);
            }
            else
            {
                return url;
            }

        }

        IEnumerator sendNoRspdRqst(WWW www)
        {
            yield return www;
            read(www);
        }

        public void send(ByteArray bytes, TempCallback tempCallback = null, bool noRspd = false)
        {
            if (noRspd)
            {
                byte[] tempData = bytes.getBuff(true);

                WWW www = new WWW(url, tempData);
                mainBehaviour.StartCoroutine(sendNoRspdRqst(www));
                //MessageRecorder.addRecord (tempData);

                return;
            }
            if (isSending == true)
            {

                //if (nextRqstList.Count < 3) {

                nextRqstList.Add(bytes);
                //}


                return;
            }



            this.tempCallback = tempCallback;
            willSendBytes = bytes;
            //mainBehaviour.StartCoroutine (sendReal (bytes));

        }

        public void resend()
        {
            if (isSending)
                return;
            willSendBytes = lastSendRqst;

            //mainBehaviour.StartCoroutine (sendReal (lastSendRqst));

        }

        static int getResponseCode(WWW request)
        {
            int ret = 0;
            if (request.responseHeaders == null)
            {
                //Debug.Log("no response headers.");
            }
            else
            {
                if (!request.responseHeaders.ContainsKey("STATUS"))
                {
                    //		Debug.Log("response headers has no STATUS.");
                }
                else
                {
                    ret = parseResponseCode(request.responseHeaders["STATUS"]);
                }
            }

            return ret;
        }

        static int parseResponseCode(string statusLine)
        {
            int ret = 0;

            string[] components = statusLine.Split(' ');
            if (components.Length < 3)
            {
                //		Debug.Log("invalid response status: " + statusLine);
            }
            else
            {
                if (!int.TryParse(components[1], out ret))
                {
                    //				Debug.Log("invalid response code: " + components[1]);
                }
            }

            return ret;
        }

        private IEnumerator sendReal(ByteArray bytes)
        {



            //View.uiCamera.eventReceiverMask.value = 0;
            if (CoreLibCallBack.OnSendingState != null)
                CoreLibCallBack.OnSendingState(true);
            lastSendRqst = bytes;
            bytes.stream.Position = 0;
            lastSendTime = Time.time;
            Debug.Log("sendReal " + url);
            byte[] tempData = bytes.getBuff(true);

            WWW www = new WWW(url, tempData);
            //	MessageRecorder.addRecord (tempData);


            while (true)
            {
                if (www.isDone)
                    break;
                yield return null;
      
                if (Time.time - lastSendTime > 30)
                {
                    // www.Dispose ();
                    resetForAlert();
                    if (CoreLibCallBack.OnShowError != null)
                        CoreLibCallBack.OnShowError(-2);
                    //View.main.onHttpError(-2);

                    lastSendTime = float.MaxValue;

                }
            }
            lastSendTime = 0;
            willSendBytes = null;
            // View.tipManager.closeAlert(991);
            CoreLibCallBack.OnCloseAlert(991);

            bool localNetError = false;
            if (www.bytes.Length == 0 && www.responseHeaders.ContainsKey("STATUS") == false)
            {
                localNetError = true;
            }
            if (www.error != null || localNetError)
            {
                resetForAlert();
                int errorCode = 0;
                if (localNetError)
                    errorCode = -1;
                else
                    errorCode = getResponseCode(www);

                if (CoreLibCallBack.OnShowError != null)
                    CoreLibCallBack.OnShowError(errorCode);
                //   View.main.onHttpError(errorCode);
                Debug.Log(www.error);

                yield break;

            }
            else
            {
                //webClosed = false;
                //  View.tipManager.linkTip.SetActive(false);
                if (CoreLibCallBack.OnSendingState != null)
                    CoreLibCallBack.OnSendingState(false);
                Debug.Log("read" + www.bytes.Length);

                read(www);

            }



            if (nextRqstList.Count > 0)
            {
                ByteArray ba = nextRqstList[0];
                nextRqstList.RemoveAt(0);
                willSendBytes = ba;
                //mainBehaviour.StartCoroutine (sendReal (ba));
            }
            else
            {
                resetForAlert();
            }
        }

        void resetForAlert()
        {
            lastSendTime = 0;
            willSendBytes = null;
            if (CoreLibCallBack.OnSendingState != null)
                CoreLibCallBack.OnSendingState(false);
            //if (View.uiCamera.eventReceiverMask.value == 0)
            //  View.uiCamera.eventReceiverMask.value = (1 << LayerMask.NameToLayer("ui"));
        }

        private void read(WWW www)
        {
            ByteArray buffbytes = new ByteArray(www.bytes);

            int leftSize = www.bytes.Length;
            while (leftSize > 0)
            {
                int tim = System.Environment.TickCount;

                int packHead = buffbytes.readInt();
                //if(packHead==0)break;


                Debug.Log("rspd head" + packHead);
                byte[] msgBytes = new byte[packHead];
                leftSize -= packHead + 4;
                buffbytes.readBytes(msgBytes);
                createRspd(msgBytes);
                int dtim = System.Environment.TickCount - tim;
                UnityEngine.Debug.Log(dtim);
            }


            dispachAllEvent();


        }

        private void createRspd(byte[] msgBytes)
        {
            ByteArray bytes = new ByteArray(msgBytes);
            int proID = bytes.readShort();

            ConstructorInfo rspdCtr = null;
            global::starbucks.socket.BaseRspd rspd = SocketService.instance.createRspdInstance(proID, bytes);
 

            eventQueue.Enqueue(new EventData(proID + "", rspd));

        }

        private void dispachAllEvent()
        {


            object[] temp = eventQueue.ToArray();
            eventQueue.Clear();

            foreach (EventData item in temp)
            {
                SocketService.instance.DispatchEvent(item);
            }
            if (tempCallback != null)
            {
                tempCallback.Invoke(temp);
                tempCallback = null;
            }

        }

      


        private IEnumerator sendRealLoop()
        {
            while (true)
            {
                yield return null;
                if (willSendBytes != null)
                {

                    yield return sendReal(willSendBytes);

                }
            }
        }
    }

}