<img src="https://capsule-render.vercel.app/api?type=waving&color=7DDA58&height=100&section=header&text=&fontSize=0" width="100%"/>

## 기술스택

---

![Spring-Boot](https://img.shields.io/badge/spring%20boot-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Spring-Security](https://img.shields.io/badge/spring%20security-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)

![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)

![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)

![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)
# API 명세서
* [ID&PW 로그인](#idpw-로그인)
* [OAuth2 로그인](#oauth2-로그인)
* [JWT 토큰관련 기능](#jwt-토큰관련)

## ID&PW 로그인
### 회원가입 
* URL: `/api/pass/signup`
* Method: `POST`
* RequestBody:
```
{
    "email":{id},
    "name":{name},
    "password":{password},
    "role_key":{role_key}
}
```
* ResponseBody:
```
{
    "email":{id},
    "name":{password},
}
```
### 로그인
* URL: `/login`
* Method: `POST`
* RequestBody:
```
{
  "username": {email},
  "password": {password}
}
```

## OAuth2 로그인
### 로그인 및 회원가입
* URL(Github): `/oauth2/authorization/github`
* URL(Kakao): `/oauth2/authorization/kakao`
* URL(Google): `/oauth2/authorization/google`
* URL(Naver): `/oauth2/authorization/naver`
* 사용방법:
  * 로그인 서버의 주소를 포함한 API 경로로 유저를 리다이렉트 시킨다.
  * 유저는 각각의 oauth2 provider 에 맞는 인증을 완료후,
  * 쿼리 파라미터에 jwt 토큰을 포함하여 프론트 엔드의 주소로 리다이렉트 된다.
  * `/redirect?access={access}&refresh={refresh}`
  * 프론트 엔드의 주소는 환경 변수를 통하여 설정할 수 있다.

## JWT 토큰관련
### JWT 갱신
* URL: `/api/refresh`
* Method: `GET`
* RequestHeader: `"Authorization":"Bearer {refreshToken}"`
* ResponseBody:
```
{
  "userId": {사용자 ID},
  "access": {accessToken},
  "refresh": {refreshToken} 
}
```
### JWT 만료
* URL: `/api/logout`
* Method: `POST`
* RequestHeader: `"Authorization":"Bearer {accessToken}"`
* Response: (HTTP Status 200)

---
<img src="https://capsule-render.vercel.app/api?type=rect&color=7DDA58&height=40&section=footer&text=&fontSize=0" width="100%"/>