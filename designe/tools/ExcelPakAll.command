#!/usr/bin/env python
#coding:utf-8
import os
import sys
path = os.path.dirname(sys.argv[0])  
os.chdir(path)

TableInputPath="/Users/jackie/M1/M1/Design/gameData/table/"
ClientDataPath="/Users/jackie/anHei/whole_client/client/Assets/all/res/data/data.tbl"
ServerDataPath="/Users/jackie/anHei/Temp/data/"
ServerDataBindingCodePath="/Users/jackie/anHei/Temp/java_code/table/"
ClientDataBindCodePath="/Users/jackie/anHei/client/client/Assets/all/table/"

os.system ("java -Dfile.encoding=UTF-8 -jar ExcelPak.jar "+TableInputPath+" "+ClientDataPath +" "+ServerDataPath+" "+ServerDataBindingCodePath+" "+ClientDataBindCodePath)
