# AICapstone
## 인공지능을 통한 지정주차구역 불법주차 관리 시스템
## 1. 프로젝트 목적
### - 프로젝트 주제
본 프로젝트는 지정주차 대상자인 임산부, 장애인의 경우 차량 번호판의 한글 글자 뒤에 QR코드를 부착한다고 가정한다. 인공지능기술인 CNN을 구현하여 차량의 주차 여부를 판별하고, 컴퓨터 비전을 통해 차량번호를 추출 및 QR코드를 통한 주차 대상자 판별로 불법주차일 경우 관리자 APP에서 벌금을 부과할 수 있는 플랫폼을 구축하고자 한다. 

###-  대상
장애인, 임산부 등 사회적 배려가 필요한 사람들 

## 2. 프로젝트 개발 내용
### - 주요기능
![image](https://user-images.githubusercontent.com/50151242/85223016-9454c200-b3fa-11ea-98ce-e0cb90b79803.png)

### - 부가 기능
![image](https://user-images.githubusercontent.com/50151242/85223017-96b71c00-b3fa-11ea-9080-054449138f56.png)

### - 단계별 프로세스
① 주차공간에 차량이 들어오는 것을 조도센서(혹은 초음파)를 통해 인식 
② 주차장 벽면에 설치되어있는 카메라를 통해 차량 번호판을 촬영 
③ 촬영한 사진을 CNN을 통해 주차여부를 판별하고 맞을 시 번호판 인식 
④ 차량 번호판에 QR코드의 유무 확인
⑤ QR코드의 데이터가 적절하지 않거나 없을 경우 불법주차로 인식
⑥ 동시에 주차공간에서도 LED가 깜빡이고 사이렌이 울림 
⑦ 불법주차를 한 채로 일정 시간이 지나면 APP에서 벌금을 부과함

### - 사용된 부품
![image](https://user-images.githubusercontent.com/50151242/85223019-99197600-b3fa-11ea-80ec-492934e17bba.png)

## 3. 프로젝트 개발 결과
### - 주차여부 판별 CNN모델
![image](https://user-images.githubusercontent.com/50151242/85223026-b1899080-b3fa-11ea-88f0-c05c1640419f.png)
![image](https://user-images.githubusercontent.com/50151242/85223027-b2babd80-b3fa-11ea-8b68-17fa1de9addb.png)
![image](https://user-images.githubusercontent.com/50151242/85223028-b4848100-b3fa-11ea-8f60-acf031acbdf2.png)
![image](https://user-images.githubusercontent.com/50151242/85223030-b5b5ae00-b3fa-11ea-91d3-4cee2e2bc170.png)

   - 주차여부 판별을 위한 CNN 모델을 구현했다. Binary Classfication으로 촬영된 이미지를 ‘주차 중’과 ‘주차차량없음’ 두가지로 분류한다. 주차장에서 일어날 수 있는 다양한 경우를 고려하여 trainset과 test을 제작하였는데, 주차가 될 경우는 [그림 6]과 같이 차량마다 번호판이 달린 높이가 다르다는 것을 고려 하여 제작하였으며, 주차가 되지 않을 경우는 [그림 7]과 같이 고양이, 택배상자 등 주차공간에서 볼 수있는 여러 물체를 추가하여 제작하였다. 
   - CNN 모델 생성은 라즈베리파이의 파이썬에서 실행할 시 사양이 낮아 학습이 되지않아 구글의 colab을 사용해서 만든후 라즈베리파이로 옮겨 실행하였다. 
   - 예측레이블이 0.5보다 클 때는 주차 안 됨, 작거나 같은 경우에는 주차 중으로 판별한다.

### - 번호판 글자 추출
![image](https://user-images.githubusercontent.com/50151242/85223032-b9493500-b3fa-11ea-8a46-bd924d03db2f.png)
![image](https://user-images.githubusercontent.com/50151242/85223035-c0704300-b3fa-11ea-89be-4d3c7c0586c7.png)
![image](https://user-images.githubusercontent.com/50151242/85223034-be0de900-b3fa-11ea-9dc0-82fac8d60ae2.png)

    - 파이캠에서 촬영한 이미지로 번호판 글자 추출을 진행한다. 2005년부터 시행되고 있는 7글자의 번호판과 작년 9월부터 시행된 8글자의 번호판 모두 인식이 할 수 있도록 구현하였다.
    - 이미지에서의 텍스트추출은 Google Vision API를 사용해서 진행한다. 하지만 Api만 으로는 정확도가 떨어져 이미지 가공하고 정규식을 사용하여 정확도를 높였다.
    - 정확도를 높이기 위해 파이 카메라에서 촬영된 사진의 각도와 위치 등을 랜덤으로 조정하여 특정 폴더에 저장하고, 해당 이미지들을 하나씩 가져와 Google Vision API를 사용해서 글자를 추출한다.
    - 또한, 정규식을 사용하여 추출된 텍스트에서 각종 특수문자를 제거한다. 7글자와 8글자의 번호판 인식이 모두 가능하도록 가공된 글자 수가 7글자고 3번째에 한글이 있거나, 8글자고 한글이 4번째 위치에 있을 경우에만 추출하도록 하여 정확도를 높였다.

### - QR코드 임산부 장애인 판별
![image](https://user-images.githubusercontent.com/50151242/85223038-c403ca00-b3fa-11ea-84f9-4e5b5224b602.png)
![image](https://user-images.githubusercontent.com/50151242/85223036-c1a17000-b3fa-11ea-9659-f447f7b73941.png)


    - QR코드를 사용하여 장애인과 임산부를 판별한다. 
    - opencv모듈과 pyzbar라이브러리를 사용하여 QR코드를 Decode 한다. QR코드의 데이터는 띄어쓰기로 임산부 및 장애인을 나타내는 문자 데이터와 qr코드의 유효기간을 나타내는 숫자 데이터로 나뉜다.
    - 문자데이터에서 P면 pregant로 임산부, D면 Disabled로 장애인을 뜻하며, 숫자 데이터는 유효기간을 나타낸다.
    - 촬영된 이미지에 QR코드가 없으면 불법주차로 판단하며, QR코드가 있을 경우는 그림의 알고리즘과 같다.
    - QR코드 인식 결과이다.
   
### - 데이터베이스
![image](https://user-images.githubusercontent.com/50151242/85223041-c6662400-b3fa-11ea-8b0b-0d98fef546d6.png)
![image](https://user-images.githubusercontent.com/50151242/85223042-c82fe780-b3fa-11ea-85d0-1e229c7465b1.png)
![image](https://user-images.githubusercontent.com/50151242/85223045-ca924180-b3fa-11ea-8b57-8a8cdaa35d81.png)

    - 추출된 번호판의 글자와 qr코드에 대한 정보는 관리자 APP에서의 확인을 위해 데이터베이스에 저장된다.
    - 라즈베리파이에서는  pymysql을 사용하여 네이버클라우드 서버와 연동해 데이터 insert 및 update를 실행한다.
    - 앱과 데이터베이스를 연동하기위해서는  php파일을 사용하며 이를 통해 앱에서 불법주차 차량 조회, 벌금 조회, 벌금 부과 등 다양한 기능을 수행할 수 있다.

### - 프로토타입
![image](https://user-images.githubusercontent.com/50151242/85223046-cbc36e80-b3fa-11ea-966e-84f8625426ac.png)
![image](https://user-images.githubusercontent.com/50151242/85223047-cd8d3200-b3fa-11ea-8702-deb27180380b.png)
![image](https://user-images.githubusercontent.com/50151242/85223050-cf56f580-b3fa-11ea-9be4-aecfc4a77895.png)

### - 관리자 APP
![image](https://user-images.githubusercontent.com/50151242/85223051-d0882280-b3fa-11ea-8bd4-cf6c01cdb04e.png)
![image](https://user-images.githubusercontent.com/50151242/85223053-d251e600-b3fa-11ea-95c7-fa1f42032657.png)
![image](https://user-images.githubusercontent.com/50151242/85223054-d3831300-b3fa-11ea-94f3-cde951617eeb.png)

     - 관리자가 주차장에 있지 않아도 불법주차 내역을 확인할 수 있도록 APP을 제작하였다. APP에서는 불법주차 차량 확인, 벌금 부과, 벌금 조회, 추출된 차량 번호판 글자 수정 등의 기능을 수행한다. 
     - 관리자는 회원가입을 하여 APP을 사용할 수 있으며, 회원 가입 시 근무장소를 선택할 수 있다. 이때, 근무장소가 없을 시엔 주차장의 정보를 입력하여, 주차장을 추가할 수 있다.
     - 앱의 메인화면에는 로그인 시 사용자가 근무중인 주차장의 이름과 주소가 표시된다. 불법주차 시 해당 구역에 알림이 깜빡인다. 주차장소를 클릭 하면 해당 주차구역에서 촬영된 이미지, 촬영된 시간, 인식된 차량번호를 확인할 수 있으며, ‘일치’를 누를 경우 벌금 부과, ‘불일치’를 클릭할 시 추출번호 변경 페이지로 이동한다. 추출번호페이지에서 글자를 입력하면 추출된 차량의 번호가 변경된다.
     - 메인 페이지에서는 우측상단 버튼을 통해 벌금 조회와 사용자를 등록할 수 있다. 사용자 등록 페이지에서는 차량의 번호와 소유자의 이름을 등록할 수 있으며, 벌금 조회 페이지에서는 차량의 번호를 통해 벌금을 조회할 수 있다.
