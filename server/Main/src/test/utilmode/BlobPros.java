package test.utilmode; /**
 * Title: BlobPros.java
 * Project: test
 * Description: 把图片存入mysql中的blob字段，并取出
 * Call Module: mtools数据库中的tmp表
 * File: C:downloadsluozsh.jpg
 * Copyright: Copyright (c) 2003-2003
 * Company: uniware
 * Create Date: 2002.12.5
 * @Author: ChenQH
 * @version 1.0 版本*
 *
 * Revision history
 * Name Date Description
 * ---- ---- -----------
 * Chenqh 2003.12.5 对图片进行存取
 *
 * note: 要把数据库中的Blob字段设为longblob
 *
 */

//package com.uniware;

import gluffy.utils.JkTools;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.sql.*;

public class BlobPros
{
    private static final String URL = "jdbc:mysql://localhost:3306/m1_zsms_oywl?user=root&password=123456&useUnicode=true";
    private Connection conn = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;

    public BlobPros()
    {
    }

    /**
     * 向数据库中插入一个新的BLOB对象(图片)
     * @throws java.lang.Exception
     */
    public void blobInsert() throws Exception
    {
        try
        {
            Class.forName("org.gjt.mm.mysql.Driver").newInstance();
            conn = DriverManager.getConnection(URL);

            pstmt = conn.prepareStatement("update tmp set arr = ? where id = 1");
            byte[] arr = new byte[1000];
            Random random = new Random();
            int r = random.nextInt(100);
            System.out.println("r ==" + r);
            for(int i=0;i<1000;i++){
                arr[i] = (byte) (i+r);
            }
            long time = System.currentTimeMillis();
            pstmt.setBytes(1,arr);//第二个参数为文件的内容
//            pstmt.setString(1,new BASE64Encoder().encode(arr));
            pstmt.executeUpdate();
            System.out.println("time ======"+(System.currentTimeMillis()-time));
        }
        catch(Exception ex)
        {
            System.out.println("[blobInsert error : ]" + ex.toString());
        }
        finally
        {
//关闭所打开的对像//
            pstmt.close();
            conn.close();
        }
    }


    /**
     * 从数据库中读出BLOB对象
     * @throws java.lang.Exception
     */

    public void blobRead() throws Exception
    {
        byte[] bytes = null;

        try
        {
            Class.forName("org.gjt.mm.mysql.Driver").newInstance();
            conn = DriverManager.getConnection(URL);
            pstmt = conn.prepareStatement("select arr from tmp where id=1");
            long time = System.currentTimeMillis();
            rs = pstmt.executeQuery();
            rs.next();
            bytes = rs.getBytes("arr");
//            bytes = new BASE64Decoder().decodeBuffer(rs.getString("arr"));
            System.out.println("time2 ======"+(System.currentTimeMillis()-time));
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            int count = buffer.get();
            System.out.println(count);
            for(int i=0;i<count;i++){
//                System.out.println(buffer.get());
            }
        }
        catch(Exception e)
        {
            System.out.println("[OutPutFile error : ]" + e.getMessage());
        }
        finally
        {
//关闭用到的资源 
            rs.close();
            pstmt.close();
            conn.close();
        }
    }

    public static void main(String[] args)
    {
        try
        {
            BlobPros blob = new BlobPros();
            blob.blobInsert();
            blob.blobRead();
        }
        catch(Exception e)
        {
            System.out.println("[Main func error: ]" + e.getMessage());
        }
    }
} 