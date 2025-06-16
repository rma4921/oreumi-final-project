# 오르미 최종 프로젝트 TEAM 1. ToBriefing
<br>

## 1. 프로젝트 개요 (프로젝트 소개 써야하는데 여기는 조금 있다 이야기하고 채우겠습니다)
- **프로젝트 명**: 투브리핑 (ToBriefing)
- **목표**: 사용자에게 주요 뉴스를 요약해 제공하고, 게시판을 통해 자유롭게 소통할 수 있는 웹 플랫폼을 개발합니다.
- **주요 기능 요약**:
    - 소셜 로그인 인증

<br>

## 2. 팀원 (깃헙 정보 써주시면 되겠습니다)
<br>

| 이름 | GitHub |
|------|----------------|
| 금종민 | [rma4921](https://github.com/rma4921)
| 김준형 |  [banditKing](https://github.com/BanditKing)
| 김진우 |  |
| 이동엽 | [redoyp](https://github.com/redoyp) |

<br>

## 3. 프로젝트 요구사항 체크
<br>

- [✅] 회원 관리 (소셜로그인 연동)
- [✅] 정보 조회/입력/수정/삭제 (CRUD)
- [✅] 외부 Open API 연동(네이버 뉴스 검색 API)
- [✅] 앨런 AI 연동(어떤 부분에서 AI 쓰셨는지 작성해주시면 됩니다)
- [✅] 테스트코드 작성(JUnit, Mockito)
- [✅] CI/CD 환경 구성(Github Actions, AWS S3, AWS IAM, AWS CodeDeploy)

<br>

## 4. 기술 스택 (사용하신 기술 써주시면 되겠습니다)
<br>

| 영역 | 기술 |
|------|------|
| 백엔드 | Spring Boot, Spring Security, JPA, JWT (쿠키 기반 인증), OAuth2 |
| 프론트엔드 | Thymeleaf, JavaScript, CSS |
| 데이터베이스 | MySQL (AWS RDS) |
| 인프라 / DevOps | AWS EC2, AWS RDS, AWS S3, AWS IAM, AWS CodeDeploy, AWS Route 53, AWS Certificate Manager |
| 인증 / 보안 | OAuth2 (Google, Kakao, Naver), JWT (Access / Refresh Token), HTTPS(SSL 인증서 기반 보안) |
| 외부 API | | (사용하신 외부 API 써주시면 되겠습니다)
| CI/CD | GitHub Actions |
| 테스트 | JUnit5, Mockito |
| 공통 | Lombok, SLF4J, RESTful API 설계, ERD(erdcloud.com) |

<br>

## 5. 주요 서비스 목록 (작성하신 서비스를 간략하게 주제만 써주시면 되겠습니다)
<br>

**소셜 로그인/로그아웃** (OAuth2 + JWT)  <br>
**마이페이지** <br>
**소통 게시판** <br>

<br>

## 6. 서비스 소개 (작성하신 서비스에 대해 자세히 써주시면 되겠습니다)
<br>

<details>
<summary>🔑 소셜 로그인 / 로그아웃 (OAuth2 + JWT 인증)</summary><br>

- **OAuth2** 기반의 소셜 로그인 구현
    - 로그인 시 Google, Kakao, Naver OAuth2 인증 제공
    - 최초 로그인 시 사용자 정보를 DB에 저장

- **JWT 기반 쿠키 인증 방식**
    - 로그인 성공 시 **Access Token**과 **Refresh Token**을 **HttpOnly 쿠키**로 발급
    - Access Token은 짧은 만료 시간(1시간), Refresh Token은 긴 만료 시간(일주일)
    - Access Token 만료 시 Refresh Token으로 자동 재발급

- **보안 및 인증 흐름**
    - 모든 인증 요청은 JWT 쿠키를 통해 처리
    - 로그아웃 시 쿠키 삭제 및 DB의 Refresh Token 무효화

<summary>마이페이지</summary><br>

- **스크랩한 기사 및 작성한 댓글 확인 가능**
    - 두 정보는 **tab으로 변경**하여 확인 가능
    - 각각의 tab에 대한 검색 기능 제공

- **스크랩한 기사 상세보기 가능**
    - 카테고리 수정 가능
    - 소통 게시판에 스크랩한 기사 공유 가능
    - 소통 게시판에 공유한 기사의 위치로 이동 가능
    - 스크랩한 기사 삭제 가능

<summary>소통 게시판</summary><br>

- **사용자가 공유한 모든 스크랩한 기사 확인 가능**


</details>

<br>

## 7. 프로젝트 구조 (배포 끝나고 작성하겠습니다)
<br>


<br>

## 8. 기능 명세</summary><br> (각자 기능 명세 작성해주세요)

<details>
<summary>로그인 페이지</summary><br>  

| **메뉴** | **기능** | **기능 설명** |
| --- | --- | --- |
| 회원가입 | 소셜 로그인을 통한 회원가입 | google, naver, kakao 계정을 통한 회원가입, 처음 로그인 시 자동으로 가입 |
| 로그인 | 소셜 로그인 | 소셜 로그인을 통한 로그인, 시작 페이지로 연결 |
| 로그아웃 | 소셜 로그아웃 | 소셜 로그인을 통한 로그아웃, 시작 페이지로 연결 |

<summary>댓글(Comment)</summary><br>
| 댓글 작성 | 로그인 사용자의 댓글 작성 | 게시글 상세 페이지에서 로그인한 사용자만 댓글 작성 가능 |
| 게시글의 댓글 목록 조회 | 해당 게시글에 달린 모든 댓글을 최신순으로 표시 | 
| 자신의 댓글 수정 | 사용자는 본인이 작성한 댓글만 수정 가능, 수정 시간 표시 | 
| 자신의 댓글 삭제 | 사용자는 본인이 작성한 댓글만 삭제 가능|
| 유효성 검사 | 댓글 입력값 검증 | 빈 댓글 또는 1000자 초과 시 등록/수정 불가, 예외 처리 |




</details>

## 9. API 명세 (각자 API 명세 작성해주세요)

### User

| NAME | METHOD | URL | DESCRIPTION |
| --- | --- | --- | --- |
| token refresh | POST | /api/token/refresh | refresh tokne 유효 여부 따라 access token 발급 |
| get user info | GET | /api/users | access token 을 통해 인증 된 유저의 정보 조회 |
| get users status | GET | /api/user/status | access token 을 통해 인증 된 유저의 이름 조회 |
| login | GET | /login | 존재하는 유저면 로그인, 처음 로그인이면 회원가입 후 로그인 |


<br>

## 10. 화면 설계 (일단 피그마 url 썼는데 배포 후 페이지 이미지 하나하나 추가하던가 하겠습니다)
[피그마 화면 설계] (https://www.figma.com/design/5hswlgWQRIBpyRXJFc7Glm/Untitled?node-id=0-1&p=f&t=XZXhk9xmK1rAnLhI-0)

<br>

## 11. ERD
### ERD Cloud 데이터베이스 설계
![ERD Cloud DB] (https://github.com/user-attachments/assets/976be347-d9e1-48cf-b4c7-e5a07e3f02c1)
