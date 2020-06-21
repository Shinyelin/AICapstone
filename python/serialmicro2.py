#buzzer.py 를 통해 불법 주차 시 알람을 울린 후 실행되는 코드
#알람을 울린 후에도 주차중일 경우 벌금을 부과하기 위한 코드
#코드의 흐름 serialard.py -> serialmicro.py -> final_newprocess.py -> qrprocess.py
#          불법주차 시 ->buzzer.py->serialmicro2 ->serialard.py
#          불법주차 아닐 시  ->serialard.py

import serial
import sys
from time import sleep
import os
import RPi.GPIO as GPIO
import time
from picamera import PiCamera
import pymysql

def micro2():     #QRcode 인식 후 불법주차로 판명되었을때의 초음파센서 과정
    GPIO.setmode(GPIO.BCM)
    GPIO.setup(17, GPIO.OUT)
      
    while True:
        ser = serial.Serial('/dev/ttyUSB0',9600)   #while문이 계속 돌면서 통신포트 open
        microresult = int(ser.readline())
        
        GPIO.output(17, True)  #주차중인 경우 계속 LED가 깜박이도록한다. 
        time.sleep(0.1)
        GPIO.output(17, False)
        time.sleep(0.1)
        
        if microresult < 10:  #불법주차
            print("불법주차 차량이 주차되어 있습니다.")
            GPIO.output(17, False)
            
            #네이버 클라우드 데이터베이스
            conn = pymysql.connect(host = '', user='', password='', db = '', charset='utf8')  #데이터베이스 연동
            curs=conn.cursor()
            
            import datetime    #현재 시간을 넣기위해
            now = datetime.datetime.now()
            
            sql = "UPDATE picam SET piTime = %s, piTF =%s where id=2"  #id가 13인 위치에 데이터 수정(시간과 F 삽입)
            
            curs.execute(sql, (now, 'F'))  #sql실행(현재 시간과 F 삽입)
            
            conn.commit()
            conn.close()
            
            sleep(1)
            import serialard        #데이터 삽입이 끝난후 다시 조도센서부터 시작     
            serialard.sensor()
    
            sys.exit(1)    #이건 필수로 있어야 함
        elif microresult >= 10 and microresult < 90:   #차가 나가는 중
            print("차가 나가는 중입니다")    
            continue
        else:                             #차가 다시 뒤로 나가면 조도센서로 처음부터 다시 인식
            print("차가 나갔습니다")
            GPIO.output(17, False)
            import serialard
            serialard.sensor()
            sys.exit(1)   #이것도 필수
            
micro2()