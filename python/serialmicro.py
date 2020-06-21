#차량이 벽면과 가까워 지는 지 거리를 통해 주차 중인지 완료했는지 등을 판별 하는 코드
#코드의 흐름 serialard.py -> serialmicro.py -> final_newprocess.py -> qrprocess.py
#          불법주차 시 ->buzzer.py->serialmicro2 ->serialard.py
#          불법주차 아닐 시  ->serialard.py
import serial
import sys
from time import sleep
import os
import RPi.GPIO as GPIO
import time

def micro():
    GPIO.setmode(GPIO.BCM)
    GPIO.setup(17, GPIO.OUT)
    while True:
        ser = serial.Serial('/dev/ttyUSB0',9600)   #while문이 계속 돌면서 통신포트 open
        microresult = int(ser.readline())
        
        GPIO.output(17, True)  #주차중인 경우 계속 LED가 깜박이도록한다. 
        time.sleep(0.1)
        GPIO.output(17, False)
        time.sleep(0.1)
        
        if microresult < 10:  #주차완료
            print("초음파센서 인식했습니다 ")
            GPIO.output(17, False)
            
            print("주차완료 및 번호판 인식으로 넘어갑니다")
            import final_newprocess
            final_newprocess.computervision3()   #CNN모델을 통해 실제 주차 여부 구분 및 번호판 추출
    
            sys.exit(1)    #이건 필수로 있어야 함
            
        elif microresult >= 10 and microresult < 90:   #주차중
            print("차가 주차중입니다")    
            continue
        else:                             #차가 다시 뒤로 나가면 조도센서로 처음부터 다시 인식
            print("차가 나갔습니다")
            GPIO.output(17, False)
            import serialard
            serialard.sensor()   #다시 기존 코드로 돌아감 
            sys.exit(1)   #이것도 필수
            
micro()