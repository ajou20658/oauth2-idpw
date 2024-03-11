# OAUTH2 로그인 구현
## 각 OAuth2Server attribute 정리
KAKAO
```json
{
  "attributes":{
    "nickname":"A",
    "profile_image":"B",
    "thumbnail_image":"C",
    "id":"D"
  }
}
```
NAVER
```json
{
  "attributes":{
    "id":"A",
    "profile_image":"B",
    "email":"C",
    "name": "D"
  }
}
```
GITHUB
```json
{
  "attributes":{
    "login":"A",
    "id":"B",
    "node_id":"C",
    "avatar_url":"D",
    "url":"A",
    "html_url":"B",
    "followers_url":"C",
    "following_url":"D",
    "...": "...",
    "name":"A",
    "email":"B",
    "..": ".."
  }
}
```
GOOGLE
```json
{
  "attributes":{
    "sub":"A",
    "name":"B",
    "given_name":"C",
    "picture":"D",
    "email":"A",
    "email_verified":"B",
    "locale":"C",
    "hd": "D"
  }
}
```

## principal의 attributes에 저장할 정보
```json
{
  "attributes": {
    "UserID": "A",
    "name": "B",
    "email": "C",
    "profile": "D"
  }
}
```

# JWT발급
```json
{
  "httpStatus": "OK",
  "code": 200,
  "data": {
    "grantType": "Bearer",
    "accessToken": "...",
    "refreshToken": "...",
    "accessTokenExpiresIn": 123123
  }
}
```

# 엔드포인트 정리

```
/
로그인완료 후 리다이렉트되는 지점
로그인이 성공하였으면 jwt값을 받고, 실패하였으면 null값을 받음

/login
oauth2로그인 시작 엔드포인트

/api/reissue
jwt토큰의 재발급 엔드포인트
Authorization 헤더에 Bearer {refreshToken}을 담아 전송
jwt 혹은 null값 받음

/logout
jwt토큰의 만료 엔드포인트(구현예정)
```