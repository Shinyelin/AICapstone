#QR코드 인식을 위한 코드
#QR코드에 'P'or'B'와 코드의 유효기간 데이터를 넣어, 이를 통해 불법 주차 판별
#코드의 흐름 serialard.py -> serialmicro.py -> final_newprocess.py -> qrprocess.py
#          불법주차 시 ->buzzer.py->serialmicro2 ->serialard.py
#          불법주차 아닐 시  ->serialard.py
import sys

def qr():
    import pymysql  #데이터베이스 import
    from datetime import datetime
    import cv2
    import matplotlib.pyplot as plt
    import matplotlib.image as mpimg
    import pyzbar.pyzbar as pyzbar
    import matplotlib.pyplot as plt
    from time import sleep    #sleep()을 사용하기 위한 방법
    #네이버 클라우드 데이터베이스
    conn = pymysql.connect(host = '', user='', password='', db = '', charset='utf8')  #데이터베이스 연동
    
    curs=conn.cursor() #데이터베이스 커서 생성
    
    img = cv2.imread('/home/pi/CarLicensePlate/test_img/pic2.jpg')
    plt.imshow(img)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    plt.imshow(gray, cmap='gray')
    decoded = pyzbar.decode(gray)
    decoded
    
    stnowday = datetime.today().strftime("%Y%m%d")
    nowday = int(stnowday)   #현재 날짜를 가져옴
    if not decoded:         #QR코드가 없을 경우 (불법주차 알림)
        print('qr없음')
        sleep(1)
        import buzzer    #부저 (불법주차 알림)
        buzzer.buz()
        sys.exit(1)
    for d in decoded:
        
        
        data1=d.data.decode('utf-8').split(' ')[0]    #P or D
        data2=d.data.decode('utf-8').split(' ')[1]    #유효기간
        print(data1)
        print(data2)
        
        if(int(data2)>=nowday):   #현재 날짜 이후일 경우 유효 코드 
           print("유효한 코드입니다")
           sleep(1)
           if(data1=='P'):
              print("임산부")
              sql = "UPDATE picam SET piTF =%s where id=1"    #id가 13인 위치에 데이터 수정(임산부, TF 설정)
              curs.execute(sql, ('T'))     #sql실행
              conn.commit()
              conn.close()
              sleep(1)
              import serialard    #다시 조도센서로 출입하는 차량을 인식하기 위한 import
              serialard.sensor()
              sys.exit(1)
          
           elif(data1=='D'):
              print("장애인")
              sql = "UPDATE picam SET piTF =%s where id=5"      #id가 13인 위치에 데이터 수정(장애인, TF 설정)
              curs.execute(sql, ('T'))    #sql실행
              conn.commit()
              conn.close()
              sleep(1)
              import serialard    #다시 조도센서로 출입하는 차량을 인식하기 위한 import
              serialard.sensor()
              sys.exit(1)
          
           else:   #현재 날짜 이후일 경우 유효하지 않은 코드 
              print("유효하지 않은 코드입니다")
              sleep(1)
              import buzzer    #유효하지 않은 코드인 경우 부저(불법주차 알림)
              buzzer.buz()
              sys.exit(1)
        else:
          print("만료된 코드입니다")
          sleep(1)
          import buzzer    #만료된 코드인 경우 부저 (불법주차 알림)
          buzzer.buz()
          sys.exit(1)
      
    plt.imshow(img)
    
qr()