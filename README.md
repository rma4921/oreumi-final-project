## 📝 요구사항 명세서 (Requirements Specification)

## 1. 프로젝트 간략 설명
- **프로젝트 명**: 투브리핑 (ToBriefing)
- **목표**: 앨런 AI를 이용하여 사용자에게 최신 기사를 요약하여 제공해주고, 그와 관련된 주식 정보를 제공합니다

<br>

## 2. 팀원
<br>

| 이름 | GitHub |
|------|----------------|
| 금종민 | [rma4921](https://github.com/rma4921)
| 김준형 |  [banditKing](https://github.com/BanditKing)
| 김진우 | [James Kim](https://github.com/jameskdev)
| 이동엽 | [redoyp](https://github.com/redoyp) |

<br>

## 3. 프로젝트 요구사항 체크
<br>

- [✅] 회원 관리 (소셜로그인 연동)
- [✅] 정보 조회/입력/수정/삭제 (CRUD)
- [✅] 외부 Open API 연동(네이버 뉴스 검색 API)
- [✅] 앨런 AI 연동
- [✅] 테스트코드 작성(JUnit, Mockito)
- [✅] CI/CD 환경 구성(Github Actions, AWS S3, AWS IAM, AWS CodeDeploy)

<br>

## 4. 기술 스택 
<br>

| 영역 | 기술 |
|------|------|
| 백엔드 | Spring Boot, Spring Security, JPA, JWT (쿠키 기반 인증), OAuth2 |
| 프론트엔드 | Thymeleaf, JavaScript, CSS |
| 데이터베이스 | MySQL (AWS RDS) |
| 인프라 / DevOps | AWS EC2, AWS RDS, AWS S3, AWS IAM, AWS CodeDeploy, AWS Route 53, AWS Certificate Manager |
| 인증 / 보안 | OAuth2 (Google, Kakao, Naver), JWT (Access / Refresh Token), HTTPS(SSL 인증서 기반 보안) |
| 외부 API | 네이버 뉴스 검색 API, 앨런 AI API |
| CI/CD | GitHub Actions |
| 테스트 | JUnit5, Mockito |
| 공통 | Lombok, SLF4J, RESTful API 설계, ERD(erdcloud.com) |

<br>

## 5. 주요 서비스 목록 
<br>

**소셜 로그인/로그아웃** (OAuth2 + JWT)  <br>
**마이페이지** <br>
**소통 게시판** <br>
**메인 페이지** <br>
**뉴스 상세 게시글** <br>
**댓글**

<br>

## 6. 서비스 소개 
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

</details>

<details>
<summary>메인페이지</summary><br>

- **최신 뉴스 기사 호출**
  - 네이버 뉴스 API를 통한 최신 뉴스 기사 제공

- **뉴스 제목 클릭 시 뉴스 기사 요약 및 관련 주식 정보 제공**
  - 앨런 AI 기반 프롬프트 엔지니어링 설정
  - 앨런 AI 기반 뉴스 요약 서비스 제공

</details>

<details>
<summary>뉴스 상세 게시글</summary><br>

- **요약된 정보 확인**


- ** 관련 주식 정보 확인**
  - 외부 주식 API를 통한 관련 주식 상세 정보 확인 가능

- **요약된 정보 저장**
  - 로그인 후 요약된 정보 마이페이지에 저장 가능

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

<summary>댓글</summary><br>

- **소통 게시판의 게시글에 댓글 작성, 수정, 삭제 가능**

</details>

<br>

## 7. 프로젝트 구조
<br>

```
📁 ToBriefing/
        ├── main
              ├── category
              ├── comment
              ├── config
              ├── content
              ├── mypage
              ├── Post
              └── user
        └── test
              ├── category
              ├── comment
              ├── content.services
              ├── Post
              └── userTest
```


<br>

## 8. 기능 명세</summary><br> 

<details>
<summary>로그인 페이지</summary><br>  

| **메뉴** | **기능** | **기능 설명** |
| --- | --- | --- |
| 회원가입 | 소셜 로그인을 통한 회원가입 | google, naver, kakao 계정을 통한 회원가입, 처음 로그인 시 자동으로 가입 |
| 로그인 | 소셜 로그인 | 소셜 로그인을 통한 로그인, 시작 페이지로 연결 |
| 로그아웃 | 소셜 로그아웃 | 소셜 로그인을 통한 로그아웃, 시작 페이지로 연결 |

</details>

<details>
<summary>댓글(Comment)</summary><br>

| **메뉴** | **기능** | **기능 설명** |
| --- | --- | --- |
| 댓글 작성 | 로그인 사용자의 댓글 작성 | 게시글 상세 페이지에서 로그인한 사용자만 댓글 작성 가능 |
| 게시글의 댓글 목록 조회 | 해당 게시글에 달린 모든 댓글을 최신순으로 표시 | 
| 자신의 댓글 수정 | 사용자는 본인이 작성한 댓글만 수정 가능, 수정 시간 표시 | 
| 자신의 댓글 삭제 | 사용자는 본인이 작성한 댓글만 삭제 가능|
| 유효성 검사 | 댓글 입력값 검증 | 빈 댓글 또는 1000자 초과 시 등록/수정 불가, 예외 처리 |

</details>

<details>
<summary>마이페이지(MyPage)</summary><br>

| **메뉴** | **기능** | **기능 설명** |
| --- | --- | --- |
| 목록 조회 | 스크랩한 기사 또는 작성한 댓글 조회 | 스크랩한 기사 또는 작성한 댓글 페이지 단위로  조회 |
| 카테고리 수정 | 지정된 카테고리 정보 수정 | 수동으로 카테고리 정보 수정 |
| 게시글 삭제 | 스크랩한 기사 삭제 | 삭제 버튼으로 스크랩한 기사 삭제 |
| 게시글 이동 | 공유한 게시글 위치로 이동 | 소통 게시판 상세보기로 이동 |

</details>

<details>
<summary>소통 게시판(ScrapPost)</summary><br>

| **메뉴** | **기능** | **기능 설명** |
| --- | --- | --- |
| 목록 조회 | 사용자가 공유한 모든 기사 확인 | 사용자가 공유한 모든 기사 목록 페이지 단위로 조회 |

</details>

## 9. API 명세

### User

| NAME | METHOD | URL | DESCRIPTION |
| --- | --- | --- | --- |
| token refresh | POST | /api/token/refresh | refresh tokne 유효 여부 따라 access token 발급 |
| get user info | GET | /api/users | access token 을 통해 인증 된 유저의 정보 조회 |
| get users status | GET | /api/user/status | access token 을 통해 인증 된 유저의 이름 조회 |
| login | GET | /login | 존재하는 유저면 로그인, 처음 로그인이면 회원가입 후 로그인 

### Comment

| NAME | METHOD | URL | DESCRIPTION |
| --- | --- | --- | --- |
| saveComment | POST | /api/comments | 댓글 작성 ( 로그인 사용자만 가능) |
| getCommentsByPostId | GET | /api/comments/post/{postId} | 특정 게시글의 댓글 목록 조회 |
| updateComment | PUT | /api/comments/{commentId} | 댓글 수정(작성자 본인만 가능) |
| deleteComment | DELETE | /api/comments/{commentId} | 댓글 삭제(작성자 본인만 가능) |
| getCommentsByUserId | GET | /api/comments/user/{userId} | 특정 유저가 작성한 목록 조회 |

### Category
| NAME | METHOD | URL | DESCRIPTION |
| --- | --- | --- | --- |
| updateTags | POST | api/mypage/{scrapId}/categories| 카테고리 업데이트 |

### MyPage
| NAME | METHOD | URL | DESCRIPTION |
| --- | --- | --- | --- |
| getScrappedArticles | GET | /mypage | 마이페이지 목록 조회 |
| getScrappedArticleDetail | GET | /mypage/scrap/{scrapId} | 마이페이지 상세 게시글 조회|
| deleteScrappedArticle | DELETE| /api/mypage/{scrapId} | 마이페이지 상세 게시글 삭제 |

### Community Post
| NAME | METHOD | URL | DESCRIPTION |
| --- | --- | --- | --- |
| saveScrapPost | POST | /post | 공유한 스크랩 기사 저장하기. |
| getScrapPosts | GET | /post | 공유한 스크랩 기사 목록 조회. |

### Content
| NAME | METHOD | URL | DESCRIPTION |
| --- | --- | --- | --- |
| latestNewsItem | GET | /api/v1/briefing/latest | 최신 기사 가져오기 |
| getBriefingInfo | GET | /api/v1/briefing/detail?news-url={} | 네이버 뉴스 기사에 대한 정보를 가져옵니다. AI와 상호작용을 하지 않아, 대기 없이 결과를 바로 가져옵니다. |
| getDetailedInfo | GET | /api/v1/briefing/ai_detail?news-url={} | 네이버 뉴스 기사에 대한 AI 요약과 관련 주식 종목들을 조회합니다. |
| getSummaryOnly | GET | /api/v1/briefing/ai_summary?news-url={} | 네이버 뉴스 기사에 대한 AI 요약만을 제공합니다. |
| getInvestmentRecommendation | GET | /api/v1/briefing/recommendation?company-name={} | 특정 회사에 대해 AI의 추천 정보를 제공합니다. 추천 시에는 해당 회사와 관련된 최근 기사들과, 지난 30일간의 주가를 토대로 조언을 제공합니다. company-name에 대입되는 회사명은 반드시 증시에 상장된 공식 명칭이어야 합니다. |
| getStockItemsByName | GET | /api/v1/stock-price/by-name?name={회사명}&count={N}| 특정 회사에 대한 지난 N일간의 주가 정보를 가져옵니다. name에 대입되는 회사명은 반드시 증시에 상장된 공식 명칭이어야 합니다. |
| getStockItemsByIsin | GET | /api/v1/stock-price/by-isin?isin={회사명}&count={N}| 특정 회사에 대한 지난 N일간의 주가 정보를 가져옵니다. 해당 회사의 ISIN 코드를 통해 조회합니다 |
| saveScrapPost | POST | /api/scrap | 기사 스크랩. |


<br>

## 10. 화면 설계
https://www.figma.com/design/5hswlgWQRIBpyRXJFc7Glm/Untitled?node-id=0-1&p=f&t=XZXhk9xmK1rAnLhI-0

<br>

## 11. ERD
### ERD Cloud 데이터베이스 설계
![ERD Cloud DB](https://github.com/user-attachments/assets/976be347-d9e1-48cf-b4c7-e5a07e3f02c1)

