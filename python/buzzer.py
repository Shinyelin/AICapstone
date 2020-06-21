#불법 주차 시 부저를 울리기 위한 코드
#부저가 울린 후 불법 주차 차량이 주차를 완료했는지 확인하는 코드로 넘어감
#코드의 흐름 serialard.py -> serialmicro.py -> final_newprocess.py -> qrprocess.py
#          불법주차 시 ->buzzer.py->serialmicro2 ->serialard.py
#          불법주차 아닐 시  ->serialard.py

import sys

def buz():   #불법주차인지 확인하기 위한 부저
    import RPi.GPIO as GPIO
    import time
    from time import sleep

    GPIO.cleanup() # cleanup all GPIO

    GPIO.setmode(GPIO.BCM) # Broadcom pin-numbering scheme
    GPIO.setup(26, GPIO.OUT) # output rf
    GPIO.output(26, GPIO.HIGH)

    sleep(1)   #1초동안 부저가 울린다.

    GPIO.cleanup() # cleanup all GPIO
    
    import serialmicro2   #불법주차 차량이 주차를 중단했는지 확인하기 위해 초음파센서2로 넘어가기
    serialmicro2.micro2()
    
    sys.exit(1)

buz()