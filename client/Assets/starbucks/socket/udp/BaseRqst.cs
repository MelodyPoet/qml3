using System.IO;
using starbucks.basic;
using UnityEngine;

namespace starbucks.socket.udp{
public class BaseRqst
{
        public static long guid;
        //public static string unID;
        public int proID;
        //public static short serverID;
   
        public ByteArray bytes;
        public bool isImportant;
        public int tempRqstIDPos;
		public   BaseRqst(int proID,bool isImportant){
		this.proID=proID; 
		 
			this.isImportant = isImportant;

		bytes= new ByteArray(new MemoryStream());
			bytes.stream.Position = 0;
            bytes.writeLong(guid);
           
            bytes.writeByte(isImportant?1:2);//	for rqst type
            tempRqstIDPos = (int)bytes.stream.Position;
            if (isImportant) {
                bytes.stream.Position++;
            }
          


        bytes.writeShort(proID);

	}

		public void send()
	{
		Debug.Log("send:"+proID+",len:"+bytes.stream.Position);
			UdpService.instance.send(this);
		 
		 
	}
 


}


}