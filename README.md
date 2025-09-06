# 2025_SEASONTHON_TEAM_60_BE
2025_시즌톤_베리벳지_백엔드


### 🍓 베리뱃지 (VeriBadge) 

온라인 발언에 실명보다 강한 신뢰를 더하다.


**사전개발/해커톤 기간:** 2025. 08. 28 ~ 09. 07

## ✨Main 기능
- 회원가입/로그인 (카카오)
- 자격 인증을 통한 뱃지 발급 (소득 증명서, 의사 증명서 등)
- 유튜브 채널 연결 후 고유 태그 발급 (구글)
- 내 계정 관리
- 관리자 페이지 
- 확장 프로그램 통해 외부 사이트에 적용

---

## 👥 팀 구성

| 역할 | 이름 |
| --- | --- |
| **PM** | 배동혁 |
| **FE** | 정민희 |
| **BE** | 노영서, 박승준, 탁은혜 |


## 👩‍💻 역할 분담
|       이름         |                                      역할분담                         |
| -------------------------------------- | --------------------------------------------------------------------- |
| 노영서 | api 명세서, ERD, 대시보드 페이지, 나의 뱃지 관리 페이지(유튜브 채널 연결), 관리자 페이지 |
| 박승준 | api 명세서, 회원가입/로그인(카카오), 소득 증명서 업로드 및 인증 |
| 탁은혜 | 확장 프로그램 개발, 의사 증명서 업로드 및 인증, 계정 관리 페이지 |

---

## 🛠 기술 스택

### Backend

- Spring Boot
- JPA
- MySQL
- Docker
- Spring Security

### 인증 & 배포

- OAuth2 (Google 로그인)
- Rendor, Vercel

## 🌳 프로젝트 구조
```
├─java
│  └─com
│      └─veribadge
│          └─veribadge
│              │  VeribadgeApplication.java
│              ├─common
│              │  └─status
│              ├─config
│              │  └─mock
│              ├─controller
│              ├─domain
│              │  │  Badge.java
│              │  │  Member.java
│              │  │  Verification.java
│              │  └─enums
│              ├─dto
│              ├─exception
│              ├─handler
│              ├─repository
│              ├─security
│              │  └─jwt
│              ├─service
│              ├─util
│              └─veribadge_extension
└─resources
        application-dev.yml
        application-prod.yml
        application.yml
```
