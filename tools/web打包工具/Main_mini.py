#!/usr/bin/python
# -*- coding: utf-8 -*-
import commands
import datetime
import time
import os
import paramiko
import collections
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
from urlparse import urlparse

PORT_NUMBER = 8081
TOOL_URL="http://192.168.0.85:8081/"
#TOOL_URL="http://192.168.0.80:8081/"

project_set={
    'exe':{
        'charactor':{
            'order':1,
            'update':"svn",
            'label':'角色、皮肤、武器、特效 <font color="#FF8000"> {负责人：陈磊}</font>',
            'proj':"/Users/m1mac/Desktop/M1_Assets/exe/Character_Public",
            'lastTime':0,
            'fileStarts':["effect/","npc/","shader/","skin/","weapon/"]
        },



        'sound': {
'order':2,
            'update': "svn",
            'label': '音效 <font color="#FF8000"> {负责人：XX}</font>',
            'proj':"/Users/m1mac/Desktop/M1_Assets/exe/audio_Public",
            #'proj': "/Users/jackie/M1/M1/Art/audio_Public",
            'lastTime': 0,
            'fileStarts':["sounds.abd","bgsound/"]
        },
        'ai': {
'order':3,
            'update':"svn",
            'label': '角色AI、游戏引导          <font color="#FF8000">{负责人：刘赣}</font>',
            'proj': "/Users/m1mac/Desktop/M1_Assets/exe/AiProject",
            'lastTime': 0,
            'fileStarts': ["ai.abd","guide.abd"]
        },
        'ui': {
'order':4,
            'update': "git",
            'label': 'UI          <font color="#FF8000">{负责人：瑞敏}</font>',
            'proj': "/Users/m1mac/Desktop/M1_Assets/exe/M1_UI_Unity",
            'lastTime': 0,
            'fileStarts': ["uires.abd", "uimodules.abd"]
        },
        'scene': {
'order':5,
            'update': "svn",
            'label': '场景          <font color="#FF8000">{负责人：志勇}',
            #'proj': "/Users/m1mac/Desktop/M1_Assets/exe/scene",
            'proj': "/Users/jackie/M1/M1/Art/Scene",
            'lastTime': 0,
            'fileStarts': ["map/"]
        },
        'data': {
'order':6,
            'update': "svn",
            'label': '表格数据          <font color="#FF8000">{负责人：瑞敏}',
            'proj': "/Users/m1mac/Desktop/M1_Assets/exe/gameData",
            #'proj': "/Users/jackie/M1/M1/Design/gameData",
            'lastTime': 0,
            'fileStarts': ["data.tbl"]
        },
        'game': {
'order':7,
            'update': "git",
            'label': 'exe游戏包         <font color="#FF8000">{负责人：瑞敏}',
            'proj': "/Users/jackie/anHei/client/client",
            'lastTime': 0,

        },
        'set':{
'order':99,
        'remotepath':"/opt/m1/res/res_dev_exe",
        'resOutPath':"/Users/m1mac/Desktop/M1_Assets/exe/output",
        #'resOutPath':"/Users/jackie/Desktop/m1_res_exe",
        'buildTarget':"win32"
        }
            },
    'apk':{
        'charactor':{
            'order': 1,
            'update': "svn",
            'label':'角色、皮肤、武器、特效            <font color="#FF8000"> {负责人：陈磊}</font>',
            'proj':"/Users/m1mac/Desktop/M1_Assets/apk/Character_Public",
            'lastTime':0,
            'fileStarts':["effect/","npc/","shader/","skin/","weapon/"]
        },
        'sound':{
            'order': 2,
            'update': "svn",
            'label': '音效 <font color="#FF8000"> {负责人：XX}</font>',
            'proj':"/Users/m1mac/Desktop/M1_Assets/apk/audio_Public",
            'lastTime':0,
            'fileStarts':["sounds.abd","bgsound/"]
        },
        'ai': {
            'order': 3,
            'update': "svn",
            'label': '角色AI、游戏引导         <font color="#FF8000"> {负责人：刘赣}</font>',
            'proj': "/Users/m1mac/Desktop/M1_Assets/apk/AiProject",
            'lastTime': 0,
            'fileStarts': ["ai.abd","guide.abd"]
        },
        'ui': {
            'order': 4,
            'update': "git",
            'label': 'UI         <font color="#FF8000"> {负责人：瑞敏}</font>',
            'proj': "/Users/m1mac/Desktop/M1_Assets/apk/M1_UI_Unity",
            'lastTime': 0,
            'fileStarts': ["uires.abd", "uimodules.abd"]
        },
        'scene': {
            'order': 5,
            'update': "svn",
            'label': '场景          <font color="#FF8000">{负责人：志勇}',
            'proj': "/Users/m1mac/Desktop/M1_Assets/apk/scene",
            'lastTime': 0,
            'fileStarts': ["map/"]
        },
        'data': {
            'order': 6,
            'update': "svn",
            'label': '表格数据          <font color="#FF8000">{负责人：瑞敏}',
            'proj': "/Users/m1mac/Desktop/M1_Assets/apk/gameData",
            # 'proj': "/Users/jackie/M1/M1/Design/gameData",
            'lastTime': 0,
            'fileStarts': ["data.tbl"]
        },
        'set':{
'order':99,
        'remotepath': "/opt/m1/res/res_dev_apk",
        'resOutPath':"/Users/m1mac/Desktop/M1_Assets/apk/output",
        'buildTarget':"android"
        }
        },
    'ios': {
        'charactor': {
            'order': 1,
            'update': "svn",
            'label':'角色、皮肤、武器、特效        <font color="#FF8000">  {负责人：陈磊}</font>',
            'proj': "/Users/m1mac/Desktop/M1_Assets/ios/Character_Public",
            'lastTime': 0,
            'fileStarts': ["effect/", "npc/", "shader/", "skin/", "weapon/"]
        },
        'sound': {
            'order': 2,
            'update': "svn",
            'label': '音效 <font color="#FF8000"> {负责人：XX}</font>',
            'proj': "/Users/m1mac/Desktop/M1_Assets/ios/audio_Public",
            'lastTime': 0,
            'fileStarts':["sounds.abd","bgsound/"]
        },
        'ai': {
            'order': 3,
            'update': "svn",
            'label': '角色AI、游戏引导         <font color="#FF8000"> {负责人：刘赣}</font>',
            'proj': "/Users/m1mac/Desktop/M1_Assets/ios/AiProject",
            'lastTime': 0,
            'fileStarts': ["ai.abd","guide.abd"]
        },
        'ui': {
            'order': 4,
            'update': "git",
            'label': 'UI         <font color="#FF8000"> {负责人：瑞敏}</font>',
            'proj': "/Users/m1mac/Desktop/M1_Assets/ios/M1_UI_Unity",
            'lastTime': 0,
            'fileStarts': ["uires.abd", "uimodules.abd"]
        },
        'scene': {
            'order': 5,
            'update': "svn",
            'label': '场景          <font color="#FF8000">{负责人：志勇}',
            'proj': "/Users/m1mac/Desktop/M1_Assets/ios/scene",
            'lastTime': 0,
            'fileStarts': ["map/"]
        },
        'data': {
            'order': 6,
            'update': "svn",
            'label': '表格数据          <font color="#FF8000">{负责人：瑞敏}',
            'proj': "/Users/m1mac/Desktop/M1_Assets/ios/gameData",
            # 'proj': "/Users/jackie/M1/M1/Design/gameData",
            'lastTime': 0,
            'fileStarts': ["data.tbl"]
        },
        'set':{
'order':99,
        'remotepath': "/opt/m1/res/res_dev_ios",
        'resOutPath': "/Users/m1mac/Desktop/M1_Assets/ios/output",
        'buildTarget': "ios"
        }
    },


}

class myHandler(BaseHTTPRequestHandler):

    def getdtTime(self, lastT):
        dm = int(time.time() - lastT) / 60
        if dm > 60 * 24:
            return (str(int(dm / 60 / 24)) + " 天前")
        elif dm > 60:
            return (str(int(dm / 60)) + " 小时前")
        else:
            return (str(dm) + " 分钟前")

    def do_GET(self):
        if not self.path.startswith('/?'):
            return
        self.send_response(200)
        self.send_header('Content-type', 'text/html;charset=utf-8')

        self.end_headers()

        # Send the html message
        platform=""
        params = urlparse(self.path)
        if len(params.query) >5:
            urlParams=params.query.split("|")
            platform =urlParams[1]
            proj_name=urlParams[0]
            sceneName=urlParams[2]

            update_path=proj_path=project_set[platform][proj_name]["proj"]
            if proj_name=="scene":
                if    len(sceneName)<3 :
                    self.wfile.write("错误：场景名称太短")
                    return
                proj_path=proj_path+"/"+sceneName
            print proj_path
            #return
            update=project_set[platform][proj_name]["update"]
            resOutPath = project_set[platform]["set"]["resOutPath"]
            #自动更新资源
            print "update"
            if update=="svn" :
                (status, output)=commands.getstatusoutput("svn update --accept tf "+update_path)
                print status, output
                # self.wfile.write(output)

            elif update=="git":

                (status, output) = commands.getstatusoutput("cd " + update_path+ ";git reset --hard origin/master;git pull origin master")
                print status,output
                # self.wfile.write(output)
            pakTime = project_set[platform][proj_name]['lastTime'] = time.time()
            if proj_name=="data":
                commands.getstatusoutput(proj_path+"/tools/ExcelPakAll.command")
                self.wfile.write("data"+output)
            elif proj_name == "game":
                print "game_exe_make"
            else:
                #设置unity 打包平台
                (status, output)=commands.getstatusoutput('/Applications/Unity/Unity.app/Contents/MacOS/Unity -quit -batchmode  -buildTarget '+project_set[platform]["set"]["buildTarget"] +' -projectPath '+proj_path)
                self.wfile.write(output)
                #打包

                os.environ['asset_temp_out'] = resOutPath

                (status, output) = commands.getstatusoutput(
                    '/Applications/Unity/Unity.app/Contents/MacOS/Unity -quit -batchmode -executeMethod BuildByCmd.MakeAssets   -projectPath  '+proj_path)

                print output
                self.wfile.write(output)
            newlist = []
            fileStarts=project_set[platform][proj_name]["fileStarts"]
            for dirpath, dirnames, filenames in os.walk(resOutPath):
                for filename in filenames:
                    if (filename.endswith(".abd") or  filename.endswith(".tbl")) and os.stat(dirpath + "/" + filename).st_mtime > pakTime:
                    # if filename.endswith(".abd"):
                        relative_path=dirpath[len(resOutPath):len(dirpath)]  + filename
                        if relative_path.startswith("/"):
                            relative_path=dirpath[len(resOutPath)+1:len(dirpath)] + "/" + filename

                        needProjDeal=False
                        print relative_path
                        for fileStart in fileStarts:
                            if  relative_path.startswith(fileStart):
                                needProjDeal = True
                                break
                        if needProjDeal:
                            newlist.append(relative_path)


            self.uploadFtp(newlist, project_set[platform]["set"]["remotepath"],resOutPath)
            self.wfile.write(platform+"平台资源 打包完成<br/>")
            self.wfile.write("<a href='"+TOOL_URL+"?index' >确定</a><br/>")
            self.wfile.write("<br/>".join(newlist))
        else:
#             self.wfile.write('''打包位置：<select name=''>
# <option value='0'>内网公用</option>
# <option value='1'>不帅</option>
# <option value='1'>刘赣</option>
# <option value='1'>瑞敏</option>
# <option value='1'>雅琼</option>
# </select><br/>''')

            self.wfile.write("--------------------------exe 资源打包--------------------------<button type='button' onClick='window.open(\"http://192.168.0.244/res_dev_exe/\")'>查看ftp资源</button><br/>")

            items=project_set["exe"].items()
            # items.sort(lambda a,b :)
            items=sorted(items, key=lambda d: d[1]['order'])
            for psK, psV in items:

                if psK!="set":
                    input=""

                    if psK=="scene":
                        input="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;场景工程目录名 <input type='text' id='sceneName' size='12' maxlength='40'>"
                    print  psK
                    self.wfile.write(
                 "<font color='#0072E3'>"+   psV['label']+"</font><font color='#00A600'>(" + self.getdtTime(
                    psV['lastTime']) + ")</font>"+input+"<button type='button' onClick='dealPak(\""+psK+ "|exe\")'>打包</button><br/>")

            self.wfile.write("重启服务器<button type='button' onClick='window.open(\"http://192.168.0.244:9101/game?xxx=restart\")'>重启</button><br/>")
            self.wfile.write("--------------------------apk 资源打包-------------------------<button type='button' onClick='window.open(\"http://192.168.0.244/res_dev_apk/\")'>查看ftp资源</button><br/>")
            items=project_set["apk"].items()
            items=sorted(items, key=lambda d: d[1]['order'])
            for psK, psV in items:

                if psK!="set":

                    input=""

                    if psK=="scene":
                        input="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;场景工程目录名 <input type='text' id='sceneName' size='12' maxlength='40'>"
                    self.wfile.write(
                 "<font color='#0072E3'>"+   psV['label']+"</font><font color='#00A600'>(" + self.getdtTime(
                    psV['lastTime']) + ")</font>"+input+"<button type='button' onClick='dealPak(\""+psK+ "|apk\")'>打包</button><br/>")
            self.wfile.write("重启服务器<button type='button' onClick='window.open(\"http://192.168.0.244:9091/game?xxx=restart\")'>重启</button><br/>")
            self.wfile.write("--------------------------ios 资源打包-------------------------<button type='button' onClick='window.open(\"http://192.168.0.244/res_dev_ios/\")'>查看ftp资源</button><br/>")

            items=project_set["ios"].items()
            items=sorted(items, key=lambda d: d[1]['order'])
            for psK, psV in items:

                if psK!="set":
                    input=""

                    if psK=="scene":
                        input="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;场景工程目录名 <input type='text' id='sceneName' size='12' maxlength='40'>"
                    self.wfile.write(
                 "<font color='#0072E3'>"+   psV['label']+"</font><font color='#00A600'>(" + self.getdtTime(
                    psV['lastTime']) + ")</font>"+input+"<button type='button' onClick='dealPak(\""+psK+ "|ios\")'>打包</button><br/>")



            self.wfile.write(
                #'<script language="javascript">function dealPak(parm){alert(parm);self.location="'+TOOL_URL + '?"+parm;}</script>')//window.location.href="http://www.baidu.com";
                '<script language="javascript">function dealPak(parm){self.location="'+TOOL_URL + '?"+parm+"|"+document.getElementById("sceneName").value}</script>')

        return

    def uploadFtp(self,filelist,remotepath,localpath):
        t = paramiko.Transport(("192.168.0.244", 22))
        t.connect(username="root", password="123456")
        sftp = paramiko.SFTPClient.from_transport(t)
        for file in filelist:
            dir=os.path.dirname(remotepath+"/"+file)
            print  localpath,remotepath
            try:
                sftp.stat(dir)
            except IOError:
                sftp.mkdir(dir)  # Create remote_path
                print "change" ,dir
            sftp.put(localpath+"/"+file, remotepath+"/"+file)

        t.close


try:
    # Create a web server and define the handler to manage the
    # incoming request
    server = HTTPServer(('', PORT_NUMBER), myHandler)
    print 'Started httpserver on port ', PORT_NUMBER



    # Wait forever for incoming htto requests
    server.serve_forever()
    # cmd= "/Applications/Unity/Unity.app/Contents/MacOS/Unity  -executeMethod BuildByCmd.MakeAssets   -projectPath /Users/jackie/M1/M1/Art/audio_Public"
    # os.environ['asset_temp_out'] = "/Users/jackie/Desktop/m1_res_apk"
    #
    # (status, output) = commands.getstatusoutput(cmd)
except KeyboardInterrupt:
    print '^C received, shutting down the web server'

#server.socket.close()
server.server_close()
