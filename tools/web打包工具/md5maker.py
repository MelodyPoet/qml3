#!/usr/bin/python
#use like this: python md5maker.py /m1res/res_develop
import os
import hashlib
import datetime  
import sys
import stat
from os.path import join, getsize
def getMd5OfFile(fname):
    if not os.path.exists(fname):
        return None
 
      
    f = file(fname, 'rb+')
    m = hashlib.md5()
    while True:
        d = f.read()
        if not d:
            break
        m.update(d)
  
    f.close()
    return m.hexdigest()
    
      
    return None



def doMain(root,recSize):
    starttime = datetime.datetime.now()   
    
    fileVs=open(root+"/resvs.txt",'w')
    rootLen=len(root)
    count=0
    for rt, dirs, files in os.walk(root):
        for f in files:
            #fname = os.path.splitext(f)
            if f.endswith(".abd") or f.endswith(".tbl") or f.endswith(".mp3") or f.endswith(".lua") or f.endswith(".wav") or f.endswith(".xml") or f.endswith(".ogg"):
                u=os.path.join(rt,f)
                count=count+1
                filename=u[rootLen+1:len(u)].replace("\\","/")
                fileVs.write(filename+"\n"+ getMd5OfFile(u)+"\n")
                #print u
                if recSize:
                    fileVs.write(str(os.path.getsize(u))+"\n")
    print "all files:"+str(count)
    fileVs.close()
    endtime = datetime.datetime.now()   
    print (endtime - starttime).seconds
    
import time 
print sys.argv[1]
 
root=sys.argv[1]
#doMain(root,False)


lastMtim=0
while True:
    time.sleep(10)
  
    cMtim=0
    try:
        '''
        for rt, dirs, files in os.walk(root):
            for f in files:
                if f.endswith(".abd") or f.endswith(".tbl") or f.endswith(".mp3") or f.endswith(".lua") or f.endswith(".wav") or f.endswith(".xml") or f.endswith(".ogg"):
                    cMtim=max(cMtim,os.stat(os.path.join(root,os.path.join(rt,f))).st_mtime)
    


        if cMtim>lastMtim:
        '''
        doMain(root,True)
        lastMtim=cMtim
    except  Exception,e:

        print e

