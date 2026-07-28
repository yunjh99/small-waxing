# Small Waxing

스몰왁싱 매장의 서비스와 가격을 소개하고, 예약 채널과 고객 커뮤니티를 제공하는 웹사이트입니다.  
관리자는 별도의 관리 화면에서 가격표, 공지사항, 이벤트, FAQ, 팝업 및 사이트 이미지를 관리할 수 있습니다.

## ✨ 주요 기능

### 사용자

- 매장 및 왁싱 서비스 안내
- 성별·카테고리별 가격표 조회
- 갤러리 조회
- 공지사항, 이벤트 및 FAQ 조회
- 네이버 예약, 카카오톡 및 인스타그램 연결
- 이용약관 및 개인정보처리방침 확인

### 관리자

- Spring Security 기반 관리자 로그인
- 가격 등록, 수정, 삭제 및 노출 순서 변경
- 공지사항과 이벤트 등록, 수정, 삭제 및 복구
- FAQ 등록, 수정, 삭제 및 복구
- 사이트 팝업 관리
- 메인 이미지, 갤러리 이미지 및 문구 관리
- 방문 통계 대시보드
---

## 🛠 기술 스택

| 구분 | 기술                                                                                                                                                                                                                                                                                                                                                                                                                          |
| --- |-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Backend | ![Java](https://img.shields.io/badge/Java-21-F5A623?style=flat-square&logo=openjdk&logoColor=black) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.0-9BD36A?style=flat-square&logo=springboot&logoColor=black) ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)                                                                                                                                                                                                  |
| View | ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3-79C982?style=flat-square&logo=thymeleaf&logoColor=black) ![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=flat-square&logo=html5&logoColor=white) ![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=flat-square&logo=css3&logoColor=white) ![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat-square&logo=javascript&logoColor=black) |
| Database | ![MySQL](https://img.shields.io/badge/MySQL-8.4-72C5E8?style=flat-square&logo=mysql&logoColor=black)                                                                                                                                                                                                                                                                                                                          |
| Data Access | ![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat-square&logo=spring&logoColor=white)  ![QueryDSL](https://img.shields.io/badge/QueryDSL-0769AD?style=flat-square)                                                                                                                | |
---
## 📁 프로젝트 구조

```text
src
├── main
│   ├── java/com/example/waxing
│   │   ├── analytics     # 방문 통계
│   │   ├── common        # 공통 페이지
│   │   ├── event         # 이벤트
│   │   ├── faq           # FAQ
│   │   ├── file          # 이미지 업로드
│   │   ├── global        # 보안, 설정 및 예외 처리
│   │   ├── notice        # 공지사항
│   │   ├── popup         # 팝업
│   │   ├── pricing       # 가격표
│   │   ├── siteimage     # 사이트 이미지와 문구
│   │   └── user          # 사용자와 관리자 인증
│   └── resources
│       ├── static        # CSS, JavaScript, 이미지
│       └── templates     # Thymeleaf 템플릿
└── test
```

---

## 🐳 Docker 실행

### 1. 환경변수 준비

최초 실행 시 예시 파일을 복사합니다.

```powershell
Copy-Item .env.example .env
```

생성된 `.env`에서 DB 및 관리자 비밀번호를 실제 값으로 변경합니다. `.env`는 Git에 포함되지 않습니다.

### 2. 컨테이너 실행

```powershell
docker compose up -d --build
```

```powershell
docker compose ps
docker compose logs -f app
```

실행 후 <http://localhost:8080>으로 접속합니다. MySQL은 로컬 PC에서 `localhost:3307`로 접근할 수 있습니다.

### 3. 컨테이너 종료

```powershell
docker compose down
```

MySQL 데이터와 업로드 이미지는 Docker 볼륨에 유지됩니다. `docker compose down -v`는 데이터 볼륨까지 삭제하므로 주의합니다.

---
