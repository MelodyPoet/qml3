using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using starbucks.utils;
using UnityEngine;

namespace starbucks.basic
{
    public class ByteArray
    {
        public Stream stream;

        public ByteArray(Stream stream)
        {
            this.stream = stream;
        }

        public ByteArray(byte[] buffer)
        {
            stream = new MemoryStream(buffer);
	 
        }

        public sbyte readByte()
        {
            return (sbyte)(stream.ReadByte());
        }

        public short readShort()
        {
            return (short)((stream.ReadByte() << 8) + stream.ReadByte());
        }

        public int readInt()
        {
            return  (stream.ReadByte() << 24) + (stream.ReadByte() << 16) + (stream.ReadByte() << 8) + stream.ReadByte();
        }

        public long	readLong()
        {
            byte[] bt = new byte[8];
            stream.Read(bt, 0, 8);
            Array.Reverse(bt); 
            return BitConverter.ToInt64(bt, 0);
 
        }

        public bool readBool()
        {
            return readByte() != 0; 
        }

        public string readString()
        { 
            string str = "";
            int len = readShort();
	 
            if (len > 0)
            {	
			
                byte[] bytes = new byte[len];
                stream.Read(bytes, 0, len);
			
                str = System.Text.UTF8Encoding.UTF8.GetString(bytes);
            }
            return str;
        }

	
	
        private   static char[] defaltSplit = "_|\\|".ToCharArray();

        public   int[] readArray(char[] tag = null)
        {
            if (tag == null)
                tag = defaltSplit;
            string str = readString();
            if (str == string.Empty)
                return null;
		
            string[] vals = str.Split(tag);
            int[] intVals = new int[vals.Length];
            for (int i = 0, len = intVals.Length; i < len; i++)
            {
                if (vals [i] == "") {
                    intVals [i] = 0;
                    continue;
                }
                if (ConfigCompareUtils.COMPARE_MAP.ContainsKey(vals[i]))
                {
                    intVals[i] = (int)ConfigCompareUtils.COMPARE_MAP[vals[i]];
                }
                else
                {	
                    intVals[i] = Int32.Parse(vals[i]);
                }
            }
            return intVals;
        }

        public   string[] readArrayStr(char[] tag = null)
        {
            if (tag == null)
                tag = "\\|".ToCharArray();
            string str = readString();
            if (str == string.Empty)
                return null;
            return str.Split(tag);
        }

        public void readBytes(byte[] buff, int offset = 0, int count = 0)
        {
            if (count == 0)
                count = buff.Length - offset;
            stream.Read(buff, offset, count);
		
        }

        public void writeByte(int value)
        {
            stream.WriteByte((byte)value);
        }

        public void writeShort(int value)
        {
            stream.WriteByte((byte)(value >> 8));
            stream.WriteByte((byte)(value));
        }

        public void writeInt(int value)
        {
            stream.WriteByte((byte)(value >> 24));
            stream.WriteByte((byte)(value >> 16)); 
            stream.WriteByte((byte)(value >> 8));
            stream.WriteByte((byte)(value));
        }
 
        public void writeLong(long value)
        {
            writeInt((int)(value >> 32));
            writeInt((int)value);
        }

        public void writeBool(bool value)
        {
            stream.WriteByte(value ? (byte)1 : (byte)0); 
        }

        public void writeString(string value)
        { 
            if (value == null || value.Length == 0)
            {
                writeShort(0);
                return;
            }
            byte[] bytes = System.Text.UTF8Encoding.UTF8.GetBytes(value);
            short len = (short)bytes.Length;
            writeShort(len);
            stream.Write(bytes, 0, len);
		 
        }

        public byte[] getBuff(bool clipLength=false)
        {
            byte[] temp= ((MemoryStream)stream).GetBuffer();
            if (clipLength == false || temp.Length == stream.Length)
                return temp;
            Array.Resize<byte> (ref temp,  (int)stream.Length);
            return  temp;
        }

        public   Dictionary<Tkey,Tvalue>    readMap<Tkey,Tvalue>(string 	keyNames = "ID")
        {
            Dictionary<Tkey,Tvalue> dataSrc = new Dictionary<Tkey, Tvalue>();
            int count = readInt();
            Type type = typeof(Tvalue);
            ConstructorInfo ctr = type.GetConstructor(new Type[0]);
            for (int i = 0; i < count; i++)
            {
                IByteVo obj = (IByteVo)ctr.Invoke(null);
                obj.fromData(this);
                object realKey;
			
                if (typeof(Tkey) == typeof(string))
                {
                    realKey = "";
				
                    string[] keyAry = keyNames.Split(new char[]{ '_' });
				 
                    foreach (string key in keyAry)
                    {
                        realKey += type.GetField(key).GetValue(obj) + "_";
                    }
                    ((string)realKey).TrimEnd(new char[]{ '_' });
                }
                else
                {
                    realKey = type.GetField(keyNames).GetValue(obj);
				
                }
			 
                dataSrc.Add((Tkey)realKey, (Tvalue)obj); 
            }
            return dataSrc;
        }

        public T readValue<T>() where T:new()
        {
            Type type = typeof(T);
            if (type == typeof(int))
            {
                return (T)(object)readInt();
            }
            else if (type == typeof(byte) || type == typeof(sbyte))
            {
                return (T)(object)readByte();

            }
            else if (type == typeof(short))
            {
                return (T)(object)readShort();
            }
            else if (type == typeof(long))
            {
                return (T)(object)readLong();
            }
            T data = new T();
            (data as IByteVo).fromData(this);
            return data;
        }

        public List<T> readArray<T>() where T:new()
        {
            int len = readInt();
            if (len > 100)
            {
                UnityEngine.Debug.Log("too long"+len);
            }
            List<T> ary = new List<T>(len);
            for (int i = 0; i < len; i++)
            {
                ary.Add(readValue<T>());
            }
            return ary;
			 
        }

        public void writeValue<T>(T value)
        {
            Type type = typeof(T);
            if (type == typeof(int))
            {
                writeInt((int)(object)value);
            }
            else if (type == typeof(sbyte))
            {
                writeByte((sbyte)(object)value);
            }
            else if (type == typeof(byte))
            {
                writeByte((byte)(object)value);
            }
            else if (type == typeof(short))
            {
                writeShort((short)(object)value);
            }
            else if (type == typeof(long))
            {
                writeLong((long)(object)value);
            }
            else if (type == typeof(string))
            {
                writeString((string)(object)value);
            }
            else
            {
                Debug.Log("cant write vo now!");
            }
		
	 
        }

        public void writeArray<T>(List<T> value)
        {
            if (value == null)
            {
                writeInt(0);
                return;
            }
            int len = value.Count;
            writeInt(len);
            foreach (T item in value)
            {
                writeValue<T>(item);
            }
		
        }


        public  Dictionary<int, int> readIntIntMap ()
        {
            Dictionary<int, int> map=new Dictionary<int, int>();

            int [] array=readArray();
            if (array != null) {
                for (int i = 0; i < array.Length; i += 2) {
                    map [array [i]] = array [i + 1];
                }
            }
            return map;
        }
    }
}
