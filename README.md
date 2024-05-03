# 기능
## OAUTH2
### 로그인 요청
```
{host:port}oauth2/authorization/github

{host:port}oauth2/authorization/kakao

{host:port}oauth2/authorization/google

{host:port}oauth2/authorization/naver
```


### 로그인 성공 시
* loginSuccessHandler를 호출하여 JWT를 발급받고, 클라이언트의 redirect주소로 JWT를 포함하여 이동시킨다
* 이때 redirect 주소는 환경변수로 제외시켜 수정이 편하게 구성하였다.
```
  redirectUrl + "/redirect?access="+access+"&refresh="+refresh
```
  
## JWT
* JWT accessToken의 subject에는 데이터베이스에 저장된 유저의 PK값을 주었고, claim으로는 권한에 대한 정보만 주었다.
* 클라이언트에서 JWT를 파싱하여 정보를 얻을 수 있을 경우를 염려하여 이렇게 구성하였다.
* accessToken이 만료되고, refreshToken으로 서버에 접근을 막기위해
* JWT refreshToken의 subject에는 PK값을 주었지만, claim에 권한은 주지 않았다.

# 엔드포인트 정리
```
/redirect?access="+access+"&refresh="+refresh
```
* 로그인완료 후 리다이렉트되는 지점
* 로그인이 성공하였으면 jwt값을 받고, 실패하였으면 null값을 받음

```
/login
```
* oauth2 로그인 시작 엔드포인트

```
/api/reissue
```
* jwt토큰의 재발급 엔드포인트
* Authorization 헤더에 Bearer {refreshToken}을 담아 전송
* jwt 혹은 null값 받음

```
/logout
```
* jwt토큰의 만료 엔드포인트(구현예정)
