#번호판을 인식하기 위한 함수가 있는 코드
#cnn_model.py를 통해 만든 주차여부판별CNN모델을 load하여 이미지를 통해 주차 여부 판별
#주차라고 판별 될 시 번호판 글자 추출 함수 실행
#구글 vision을 사용하여 텍스트 추출
#기존 사용되고 있는 번호판 형태와, 앞으로 도입될 번호판 형태 둘다 인식 가능하도록 코드 구현
#코드의 흐름 serialard.py -> serialmicro.py -> final_newprocess.py -> qrprocess.py
#          불법주차 시 ->buzzer.py->serialmicro2 ->serialard.py
#          불법주차 아닐 시  ->serialard.py
# -*- coding: utf-8 -*-

def computervision3():
    import sys    #코드 실행을 강제 종료하기 위해 import sys
    import numpy as np
    import os    #코드가 한번 실행한 후 기존의 사진파일을 삭제해야 한다.
    import pymysql  #데이터베이스 import
    from time import sleep    #sleep()을 사용하기 위한 방법
    import io # 파일을 읽고 쓰기위한 모듈
    import re
    import shutil
    
    os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = ""  #API 사용하기 위한 Key의 경로
    
    #네이버 클라우드 데이터베이스
    conn = pymysql.connect(host = '', user='', password='', db = '', charset='utf8')  #데이터베이스 연동
    
    curs=conn.cursor()
    
    def convertToBinaryData(filename):    #데이터베이스에 바이너리 이미지넣기
        with open(filename, 'rb') as file:
            binaryData = file.read()
        return binaryData
    
    def createFolder(directory):   #폴더 생성
      try:
        if not os.path.exists(directory):
          os.makedirs(directory)
      except OSError:
        print("폴더 생성 오류")
        
    def removeAllFile(filePath):   #파일 지우기 
      if os.path.exists(filePath):
        for file in os.scandir(filePath):
          os.remove(file.path)
 
    def isHangul(text):   #한글 판별 함수 
        #Check the Python Version
        pyVer3 =  sys.version_info >= (3, 0)

        if pyVer3 : # for Ver 3 or later
            encText = text
        else: # for Ver 2.x
            if type(text) is not unicode:
                encText = text.decode('utf-8')
            else:
                encText = text

        hanCount = len(re.findall(u'[\u3130-\u318F\uAC00-\uD7A3]+', encText))
        return hanCount > 0

    """# 번호판 추출"""   
    import numpy as np
    # Imports the Google Cloud client library
    from google.cloud import vision
    from google.cloud.vision import types
    
    #번호판 인식 함수
    def recognition_carlicenseplate(img_path,folder_path): # 파이캠에서 촬영한이미지의 경로, 촬영된 이미지 데이터를 부풀린 이미지들이 들어있는 폴더의 경로
      # 랜덤시드 고정시키기
      print('실행')
      np.random.seed(5)
      removeAllFile(folder_path)
      from keras.preprocessing.image import ImageDataGenerator, array_to_img, img_to_array, load_img
      createFolder(folder_path)
      # 데이터셋 불러오기
      data_aug_gen = ImageDataGenerator(rescale=1./255, 
                                        rotation_range=4,
                                        width_shift_range=0.1,    #수평으로 이동
                                        height_shift_range=0.1,   #수직으로 이동
                                       #shear_range=0.5,
                                        zoom_range=[0.8, 1.2],
                                        fill_mode='nearest')
      path = img_path                                   
      img = load_img(path)
      x = img_to_array(img)
      x = x.reshape((1,) + x.shape)
      i = 0

      # 이미지를 약간씩 변경하여 한 폴더에 5개의 이미지를 저장 
      for batch in data_aug_gen.flow(x, batch_size=1, save_to_dir=folder_path, save_prefix='car', save_format='png'): #save_to_dir 부분만 새로운 폴더를 생성하여 변경하면 됨
          i += 1
          if i > 2: 
              break
      client = vision.ImageAnnotatorClient()
      filenames = os.listdir(folder_path) # 위의 save_to_dir와 같은 위치로 변경

      carplate=""
      for filename in filenames:
          path = os.path.join(folder_path, filename) # 위의 save_to_dir와 같은 위치로 변경

          # Loads the image into memory
          with io.open(path, 'rb') as image_file:
              content = image_file.read()

          image = vision.types.Image(content=content)
          response = client.text_detection(image=image)
          texts = response.text_annotations
          textstr = texts[0].description
          finalstr = textstr.split('\n')[0]
          finalstr=finalstr.replace(' ','')
          finalstr=re.sub('[-=+,#/\?:^$.@*\"※~&%ㆍ!』\\‘|\(\)\[\]\<\>`\'…》]', '', finalstr)
          try: 
            if (isHangul(finalstr[2]) and len(finalstr)==7) or (isHangul(finalstr[3])and len(finalstr)==8):   #번호판 추출이 잘 되었는지 확인 
                carplate =finalstr
                print(carplate)
          except IndexError:
            print('인덱스오류')
            pass
      
      import datetime    #현재 시간을 넣기위해
      now = datetime.datetime.now()
      empPicture = convertToBinaryData("/home/pi/CarLicensePlate/test_img/pic2.jpg")  #넣을 이미지의 위치지정
          
      sql = "UPDATE picam SET piTime = %s, piImg = %s, piExtract=%s where id=2"      #id가 13인 위치에 데이터 수정
      curs.execute(sql, (now, empPicture, carplate))  #sql실행
          
      conn.commit()
      conn.close()
       
    """# 모델 생성 후"""
    from PIL import Image
    import os, glob, numpy as np
    from keras.models import load_model

    #주차여부인식
    def parking_classfication(img_path):   #img_path는 촬영된 사진이 있는 경로 
      image_w_ap = 64
      image_h_ap = 64
      pixels_ap = image_h_ap * image_w_ap * 3
      X_ap = []
      img_ap = Image.open(img_path)
      img_ap = img_ap.convert("RGB")
      img_ap= img_ap.resize((image_w_ap, image_h_ap))
      data_ap = np.asarray(img_ap)
      X_ap.append(data_ap)
      X_ap = np.array(X_ap)
      model_ap = load_model('/home/pi/model/parking_img_classification_1.model')   #CNN모델 불러옴 
      prediction_ap = model_ap.predict(X_ap)
      np.set_printoptions(formatter={'float': lambda x: "{0:0.3f}".format(x)})
      cnt = 0 
      
      for i in prediction_ap:
          pre_ans_ap = i.argmax()  # 예측 레이블
          pre_abs_str_ap =''
          
          if pre_ans_ap > 0.5 :   #주차 안됨
             pre_ans_str_ap = "주차 상태 아님"
             print("해당 " + img_path + " 이미지는 " + pre_ans_str_ap + "으로 추정됩니다.")
             print("다시 조도센서 인식으로 넘어갑니다.")    #주차되지 않았다고 판단
             import serialard
             serialard.sensor()
             
          else :       #주차됨
             pre_ans_str_ap = "주차완료"
             print("해당 " + img_path + " 이미지는 " + pre_ans_str_ap + "로 추정됩니다.")
             sleep(1)
             recognition_carlicenseplate('/home/pi/CarLicensePlate/test_img/pic2.jpg','/home/pi/testfolder') #번호판 추출
             print("cnn 주차됨. QRcode 인식으로 넘어갑니다 ")
             import qrprocess 
             qrprocess.qr()          
            
          #cnt += 1

    parking_classfication("/home/pi/CarLicensePlate/test_img/pic2.jpg")
    #sys.exit(1)  #메모리 과부하를 막기 위한 exit()
    
computervision3()