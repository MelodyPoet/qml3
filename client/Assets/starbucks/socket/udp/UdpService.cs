using System;
using System.Collections;
using System.Net;
using System.Net.Sockets;
using starbucks.basic;
using UnityEngine;

namespace starbucks.socket.udp{
	/*
* 1=type，tempID,devID,info 重要数据需要收到确认 否则一直重发
2=type,devID,info  不重要数据 不需要确认 
3=type,revdTempID,devID 确实协议 不再需要确认和重发
* */
public class UdpService 
{
	private static int maxResendCount=20;
	private UdpClient udpClient;
	private IPEndPoint iPEndPoint;
	private sbyte lastRspdTempID=0;

	private long lastSendTime;
	private ByteArray lastSendData;
        private bool sending;


        private Queue eventQueue;

		private Queue SendingQueue;
		public static readonly UdpService instance	=new UdpService();
	private   sbyte rqstTempID;
	private int resendCount;
	private byte[] checkData;
   		public UdpService ()
	{


		
 		eventQueue=	Queue.Synchronized(new Queue());
			SendingQueue = new Queue ();

			rqstTempID = 0;// (sbyte)UnityEngine.Random.Range (0, 256);





	}
 
	public void connect(long guid,string url)
	{
		 BaseRqst.guid = guid;
		string[] items =	url.Split (':');
		connect (guid,items [0], int.Parse (items [1]));
		//connect ("192.168.0.99", 9091);//int.Parse(items[1]));
	}
	public void connect(long guid,string ip,int port)
	{
		//Tools.Trace("create socket"+ip);
		BaseRqst.guid = guid;
		iPEndPoint = new IPEndPoint (IPAddress .Parse (ip),port);
            checkData = new byte[8 + 2];
            ByteArray bytes = new ByteArray(checkData);
            bytes.writeLong(BaseRqst.guid);
             bytes.writeByte(3);// for
            bytes.writeByte(0);//for rqst tempid
 
            Debug.Log("::::::"+checkData.Length);
            if (udpClient == null) {
			udpClient =new UdpClient(0) ;
			
			udpClient.BeginReceive(OnEndReceive,this);
			Debug.Log ("connect");
		}

	}


		int tempCount;

        public bool Sending
        {
            get
            {
                return sending;
            }

            set
            {
                if(CoreLibCallBack.OnSendingState!=null)
                CoreLibCallBack.OnSendingState(value);
                sending = value;

            }
        }

        public void send(BaseRqst rqst,bool fromQueue=false)
	{
          ByteArray  bytes = rqst.bytes;
 
        if (rqst.isImportant == false) {

			udpClient.Send(bytes.getBuff(),(int)bytes.stream.Position,iPEndPoint);

			return;
		}
		if (Sending) {
				if (fromQueue == false) {
					SendingQueue.Enqueue (rqst);
				}
				Debug.Log ("sending break"+(tempCount++));
			//dispathEvent (new GameEventArgs ("RqstCancel"));
			return;
		}
			if (fromQueue) {
				SendingQueue.Dequeue ();
			}
		lastSendData = bytes;
			rqstTempID++;
			if (rqstTempID == 0) {
				rqstTempID++;
			}
		lastSendData.getBuff () [rqst.tempRqstIDPos] =(byte)rqstTempID;

 		realSend ();


	}

	private void tryResend(){
		if (Sending == false)
			return;
		if (DateTime.Now.Ticks/10000 - lastSendTime <500) {
			return;
		}
		if (resendCount++ > maxResendCount) {
			Sending = false;
			return;
		}
            //	Debug.Log ("resend"+":resend:"+(DateTime.Now.Ticks/10000));
            realSend();
	}

	public void close ()
	{
		udpClient.Close ();
	}
	 
	void realSend ()
	{
            if (BaseRqst.guid == 0) return;
	
			lastSendTime=DateTime.Now.Ticks/10000;
			Sending = true;
		byte [] bytes=new byte[lastSendData.stream.Position];


		lastSendData.stream.Position = 0;
		lastSendData.readBytes (bytes, 0, bytes.Length);
            //	Debug.Log ("send"+lastSendTime);
            // 模拟丢包
            //
            //			if (rnd.Next(100)<30) {
            //				return;
            //		}
            //	Debug.Log("realsendUdp:"+bytes.Length);
            udpClient.Send(bytes,bytes.Length,iPEndPoint);
		
	}
	public void resendError(){
		
		//				if (sending)
		//						return;
		//				 sendReal (errorRqst));

	}
 
	private  void OnEndReceive(IAsyncResult ar){
            //Debug.Log (ar);
            //Debug.Log (ar.AsyncState+"_"+this);
            if (ar == null || ar.AsyncState != this) {
                Debug.Log("ar error:"+ ar);
                udpClient.BeginReceive (OnEndReceive, this); 
			return;
		}
			// 模拟丢包
			//
//						if (rnd.Next(100)<30) {
//				udpClient.BeginReceive (OnEndReceive, this); 
//							return;
//					}
		//IPEndPoint iPEndPointRev = null;
		// 模拟丢包

		//				if (UnityEngine.Random.Range(0,100)<1) {
		//						Debug.Log ("skip");
		//						udpClient.BeginReceive (OnEndReceive, this); 
		//						return;
		//				}
		Debug.Log ("read");
		Byte[] bytes = udpClient.EndReceive(ar, ref iPEndPoint);
		Debug.Log ("<<<<<<<<<<<<<<<<<<<<<"+bytes.Length+"::::"+bytes [0]);

            //Debug.Log("udptime:"+(DateTime.Now.Ticks/10000-lastSendTime));
            if (bytes.Length == 0) {


			udpClient.BeginReceive(OnEndReceive, this); 
			return;
		}
			//for check 
			if (bytes [0] == 3) {
			if (bytes [1] == rqstTempID) {
				Sending = false;
				resendCount = 0;

				//	Debug.Log ("revCheck:"+(DateTime.Now.Ticks/10000));
			}
			udpClient.BeginReceive(OnEndReceive, this); 
				if (SendingQueue.Count > 0) {
					send (SendingQueue.Peek() as BaseRqst, true);
				}
			return;

		}
		//				for (int i = 0; i < bytes.Length; i++) {
		//						Debug.Log (bytes [i]);
		//				}
		ByteArray bb=new ByteArray(bytes);
		sbyte msgType=bb.readByte();
		if (msgType == 1) {
			sbyte rspdTempID = bb.readByte ();

				sendRspdCheck(rspdTempID);
			if(rspdTempID==lastRspdTempID){
				udpClient.BeginReceive(OnEndReceive, this); 
				return;
			}
			lastRspdTempID=rspdTempID;

		}
		//if important
		//		if(rspdTempID!=0){
		//			
		//			
		//		}

	//	long pos = bb.stream.Position;
	//	while (true) {
	//		int len=	bb.readShort();
			createRspd (bb);
           
	//		bb.stream.Position = pos + len;
	//		pos = bb.stream.Position;
	//		if (bb.stream.Length - bb.stream.Position < 4)
	//			break;
	//	}




		udpClient.BeginReceive(OnEndReceive, this); 
	}

		void sendRspdCheck (sbyte rspdTempID)
	{
			//Debug.Log ("sendRspdCheck:"+ (byte)rspdTempID);
			checkData [checkData.Length-1] = (byte)rspdTempID;

			udpClient.Send(checkData,checkData.Length,iPEndPoint);
	}	 

	private void createRspd(ByteArray bytes){

		int proID=bytes.readShort();

	
		BaseRspd rspd=SocketService.instance.createRspdInstance(proID,bytes);

	    Debug.Log("createRspd:" + proID);


            eventQueue.Enqueue (new EventData (proID + "", rspd));

	}
	public void dispachAllEvent ()
	{
		tryResend ();
		if (eventQueue.Count == 0)
			return;
		object[] temp=	eventQueue.ToArray();
		eventQueue.Clear();
		foreach (EventData item in temp) {

			EventDispatcher.globalDispatcher.DispatchEvent(item);
               
		}
		 
	}

 
 
 
	 
}

}