# Umbrella

### 스토리

동아리 활동을 하면서, 협업 및 회의를 하기위한 도구들을 선택하고 여러개를 나눠야 한다는 점에서 불편함을 느꼈고… 생활 의 불편함을 → 개발자로써 해결 하고자 하는 마음가짐

### 목표

여러 사람들과 프로젝트를 진행할 때 가지고 있었던 불편함을 개선한 협업용 웹 페이지를 제작하려고 합니다. 

협업을 진행하면서 화상회의, 문서공유, 일정 관리 등 다양한 프로그램을 활용해서 소통을 해야하는게 프로젝트 생산성이 저하되고 이러한 측면으로 회의를 하고 체력을 쓰는게 아쉬웠습니다.

이러한 불편한 점을 개선하고자, 다양한 기능을 각자의 워크스페이스에 제공하여 효율적인 협업진행을 도울 수 있는 프로젝트를 제작하려고 합니다.


## 기술스택

**프론트엔드**

- framework : `NextJS`
- library : `Typescript`, React, `Redux`
- react-query… `three/lottie.js`

************백엔드************

- 공통
    - Spring Boot 2.7.7
        - WebSocket
        - Spring Security
        - JPA
        - MySQL
    - Java 11
    - Gradle
    - **application.yaml**
- 로그인
    - MySQL
- 회원가입
    - MySQL
- 실시간 채팅
    - MongoDB
    
    
## Commit message
<h3>example : <FEAT> Add a CommentRepository
FEAT     : 새로운 기능에 대한 커밋
   
FIX      : 버그 수정에 대한 커밋
    
BUILD    : 빌드 관련  파일 수정에 대한 커밋
    
REFACTOR : 코드 리팩토링에 대한 커밋
    
DOCS     : 문서 수정에 대한 커밋
    
TEST     : 테스트 코드 수정에 대한 커밋
    
CHORE    : 그 외 자잘한 수정에 대한 커밋
    
CI       : CI 관련 설정 수정에 대한 커밋
    
STYLE    : 코드 스타일 혹은 포맷 등에 관한 커밋
