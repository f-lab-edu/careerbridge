# CareerBridge

### CareerBridge(커리어브릿지)
> 멘토와 멘티를 연결하는 플랫폼 프로젝트
> 새로운 방향을 위한 나침반이 되겠습니다. 

---

## 1. 프로젝트 설명

CareerBridge는 진로 탐색에 어려움을 겪는 사용자(Mentee)와 실제 현직자(Mentor)를 연결하여  
직무 정보, 실무 경험, 커리어 상담을 제공하는 커리어 멘토링 플랫폼입니다.

사용자는 관심직무에 따른 멘토를 추천과 직업군, 이름 등을 통해 직접 멘토의 정보를 검색후 확인할 수 있으며,  
멘토의 커리어 정보를 열람하고 채팅 및 진로 상담과같은 다양한 멘토링 컨텐츠를 받을 수 있습니다.

---

## 2. 사용 기술

### Backend
- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- QueryDSL
- Redis
- WebSocket
- JWT Authentication

### Database
- MySQL

### Infrastructure
- Docker
- Docker Compose
- AWS EC2
- Nginx

### DevOps 
- GitHub
- GitHub Actions
- Jenkins

## Architecture Overview
- 작성 예정

---
## 3. 주요 기능

### 사용자 기능
- 회원가입 및 로그인
- JWT 기반 인증/인가
- 멘토/멘티 프로필 관리

### 멘토 기능
- 멘토 프로필 게시판 CRUD
- 커리어 컨텐츠 및 멘토링 상품 등록

### 멘티 기능
- 멘토 검색
- 관심 직무 설정 
- 멘티 고민 게시판 CRUD

### 채팅 기능
- WebSocket 기반 실시간 1:1 채팅
- 채팅방 생성 및 메시지 관리

### 결제 기능
- 멘토링 상품 결제
- 결제 상태 관리

### 알림 기능
- 예약 상태 알림
- 채팅 알림
- 리뷰 및 멘토링 관련 알림

---

## 프로젝트 목표

- 인증/인가 및 멘토-멘티 실시간 채팅 구현
- 커리어 컨텐츠 및 멘토링 상품 구매 프로세스 구현
- 관심 직무 기반 멘토 추천
