## [한국대항생IT경영학회 26기 x 파블로항공] 기업 프로젝트 개발 E조

### 🙋‍♀️ 소개
- 큐시즘 26기, 파블로항공과 연계한 기업 프로젝트 <개발 과제 E조> 결과물입니다.
- 주문 및 QR 관리 어플리케이션 / 도어락 잠금장치 QR 스캔 어플리케이션 / 스프링부트 서버 / 아두이노 로 구성되어 있습니다.

### 🖇Repository
[QR 생성 Android repo](https://github.com/Kusitms-pabloAir/Pablo-air-assignment-android)

[QR 스캔 Android repo](https://github.com/Kusitms-pabloAir/Pablo-air-assignment-qr-scan)

[Server repo](https://github.com/Kusitms-pabloAir/Pablo-air-assignment-backend)

[Arduino repo](https://github.com/Kusitms-pabloAir/Pablo-air-assignment-arduino)

### 📂 구현 기능 

- 소셜 로그인 및 회원가입
- 상품 주문
- 주문별 고유 QR 생성
- 마이페이지

- QR 스캔
- 시리얼 넘버 검증
- 도어락 잠금 해제

### ✍️ 서비스 아키텍처
<img width="500" alt="image" src="https://user-images.githubusercontent.com/78305431/192710299-d00bc558-039d-466a-b1c9-c70b9b7a2c03.png">

### 🔌 아두이노 회로도
<img width="500" alt="image" src="https://user-images.githubusercontent.com/78305431/192709363-b93a0db8-4cf0-4d06-b1a0-c7235d19f18b.png">
- 아두이노 우노 보드 R3, 브레드 보드, 1채널 릴레이 모듈, 배럴 잭, 도어락 솔레노이드, 16x2 LCD

### 🔎 유의사항
- 안드로이드 앱과 서버의 경우, 민감정보에 대한 properties 파일을 추가하여야 빌드가 가능합니다. 필요 시 연락처(luck732002@gmail.com)로 요청 부탁드립니다.
- QR 생성 앱의 카카오 로그인은 KAKAO Developer API에 플랫폼 해시 키를 등록해놓아야 실행이 가능합니다. 이후 아래와 같이 res/string/kakao_key.xml에 추가 바랍니다.
```XML
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="kakao_app_api">{카카오 네이티브 앱 key}</string>
    <string name="kakao_native_api">kakao{카카오 네이티브 앱 key}</string>
</resources>
```
- QR 스캔 앱은 블루투스 연결 가능 기기인지 확인이 필요합니다.

### 👤 팀원
<table>
<tr>
    <td align="center"><a href="https://github.com/mingeun0507"><img src="https://user-images.githubusercontent.com/78305431/192702338-c4af9806-a392-4d9f-bcd2-2f64775e5529.png" width="100px;" alt=""/><br /><sub><b>김민근(PM & Hardware)</b></sub></a><br /></td>
    <td align="center"><a href="https://github.com/yujeongkimm"><img src="https://user-images.githubusercontent.com/78305431/192702485-89bef3c7-a970-4b54-8442-f9bd0dff110c.png" width="100px;" alt=""/><br /><sub><b>김유정(BE)</b></sub></a><br /></td>
    <td align="center"><a href="https://github.com/Jeongminyooa"><img src="https://user-images.githubusercontent.com/78305431/192702617-d1ff035f-4b32-45ed-bb11-6b30c685be62.png" width="100px;" alt=""/><br /><sub><b>유정민(Android)</b></sub></a><br /></td>
<tr>
</table>

