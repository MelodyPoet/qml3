using System.IO;
using starbucks.basic;
using UnityEngine;

namespace starbucks.socket.http
{
    public class BaseRqst
    {

        public int proID;
        public static string unID;
        public static short serverID;
        public static sbyte clientTempID;
        protected ByteArray bytes;

        public BaseRqst(int proID)
        {
            this.proID = proID;
            bytes = new ByteArray(new MemoryStream());
            bytes.stream.Position = 0;

            bytes.writeString(unID);
            bytes.writeShort(serverID);
            bytes.writeShort(proID);
            bytes.writeByte(clientTempID);

        }
        public void resetStreamPos()
        {
            bytes.stream.Position = bytes.stream.Length;
        }
        public void send(HttpService.TempCallback tempCallback = null, bool noRspd = false)
        {
            Debug.Log("send:" + proID + ",len:" + bytes.stream.Position);
            HttpService.instance.send(bytes, tempCallback, noRspd);
            SocketService.instance.DispatchEvent("RQST" + proID, this);
        }

        public void sendHttp(HttpService http, string tempUnID)
        {
            Debug.Log("send:" + proID + ",len:" + bytes.stream.Position);
            int pos = (int)bytes.stream.Position;
            bytes.stream.Position = 0;
            bytes.writeString(tempUnID);
            bytes.stream.Position = pos;
            http.send(bytes);
        }
    }


}