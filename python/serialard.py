#조도센서 인식에 따른 파이캠 촬영 여부를 결정하는 코드
#코드의 흐름 serialard.py -> serialmicro.py -> final_newprocess.py -> qrprocess.py
#          불법주차 시 ->buzzer.py->serialmicro2 ->serialard.py
#          불법주차 아닐 시  ->serialard.py
import serial
import sys
from picamera import PiCamera
from time import sleep
import RPi.GPIO as GPIO
import time

def sensor():
    FromArduino = serial.Serial("/dev/ttyACM0" , 9600)   #통신포트 open
    FromArduino.flushInput()
    GPIO.cleanup() # cleanup all GPIO 
    while True:
        light = int(FromArduino.readline())
        
        if light < 200:      #자동차가 출입했을 때
            print("자동차가 출입했습니다")
            
            GPIO.setmode(GPIO.BCM) # Broadcom pin-numbering scheme
            GPIO.setup(26, GPIO.OUT) # output rf
            GPIO.output(26, GPIO.HIGH)  #부저가 사진을 찍는동안 울린다.
            
            camera = PiCamera()
            camera.start_preview()
            camera.rotation = 180      #180도 회전
            sleep(5)    #번호판사진을 찍는데 걸리는 시간
            camera.capture("/home/pi/CarLicensePlate/test_img/pic2.jpg")   #사진 저장 경로
            camera.stop_preview()
            camera.close()
            
            GPIO.cleanup() # 부저 끄기
            
            sleep(1)
            print("초음파센서 인식으로 넘어갑니다")
            import serialmicro    #조도센서인식후 초음파인식
            serialmicro.micro()
            
            sys.exit(1)
        else:                #아무것도 없을 때
            print("현재 아무것도 들어오지 않았습니다")
            sleep(1)
            continue
        
sensor()
