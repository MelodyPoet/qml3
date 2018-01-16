using System;
using System.Collections;
using System.Net.Sockets;
using System.Reflection;
using starbucks.basic;
using UnityEngine;

namespace starbucks.socket.tcp
{
	public class TcpService 
	{
	
	
		private TcpClient  socket;
		private NetworkStream stream;
		private int packHead;
		private byte[] readBuff;
		private ByteArray buffbytes;
		private int leftSize;
		private Queue eventQueue;
		public Queue delayEventQueue;
		public static readonly TcpService instance	=new TcpService();
		private TcpService()
		{
			readBuff=new byte[10240];
			eventQueue=	Queue.Synchronized(new Queue());
			delayEventQueue = new Queue ();
		}
		public void connect(string ip,int port)
		{
			Debug.Log("create socket");
//	bool rst=	Security.PrefetchSocketPolicy( ip, port,7000 );
//		Tools.TraceError ("socket"+rst);
			socket=new TcpClient();
			packHead=0;
	
			socket.Connect(ip, port);
			stream=socket.GetStream();
			stream.BeginRead(readBuff,0,readBuff.Length,new AsyncCallback(onReadBack),stream);
			
		}
 
		public void clean()
		{
			//	unReGlEvents();
			socket.Close();
			socket=null;
			Console.WriteLine("清除一个socket");
		}
		public void sendRawStr(string msg )
		{
			byte[] bytes= System.Text.UTF8Encoding.UTF8.GetBytes( msg);
			stream.Write(bytes,0,bytes.Length);
			stream.Flush();
		
		}
		public void send(ByteArray bytes)
		{
			if (socket == null)
				return;
			int   rawLen=(int)bytes.stream.Position-4;
			bytes.stream.Position=0;
			bytes.writeInt(rawLen);
		
			bytes.stream.Position=0; 		  
			stream.Write(bytes.getBuff(),0,rawLen+4);
			stream.Flush();
		}
		private void onReadBack (IAsyncResult ar)
		{
	 
			leftSize= stream.EndRead (ar);
			//	Tools.Trace ("allsize:", leftSize);
			buffbytes = new ByteArray (readBuff);
			read ();
			stream.BeginRead(readBuff,0,readBuff.Length,new AsyncCallback(onReadBack),stream);
		}
		private void read ()
		{
		
			//a new message (normal)
		 
			//Tools.Trace ("size:", leftSize);
			if (packHead == 0) {
			
				if (leftSize < 4) {
			
					return;// head not ready
				}
			

			
				packHead = buffbytes.readInt ();
			
				//		Tools.Trace ("headLean", packHead);
				if (packHead > leftSize - 4) {
					return;//message not ready
				}
			
		
				byte[] msgBytes = new byte[packHead];
				leftSize-=	packHead+4;
				buffbytes.readBytes (msgBytes);
			
			
			
				createRspd (msgBytes);
			
			
			
				packHead = 0;
				read ();
			}

		}
		private void createRspd(byte[] msgBytes){
			ByteArray bytes=new ByteArray(msgBytes);
			int proID=bytes.readShort();
 
			ConstructorInfo rspdCtr=null;
			BaseRspd rspd = SocketService.instance.createRspdInstance(proID, bytes);
	 

			eventQueue.Enqueue (new EventData (proID + "", rspd));

		}
		public void dispachAllEvent ()
		{
			object[] temp=	eventQueue.ToArray();
			eventQueue.Clear();
			foreach (EventData item in temp) {
	 
				EventDispatcher.globalDispatcher.DispatchEvent(item);
		 
			}
	
		}
	 
 
 
	}
}
 