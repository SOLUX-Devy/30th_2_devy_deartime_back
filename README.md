# 📍 Deartime(디어타임)
**🗓 프로젝트 기간: 2025.9 ~ 2026.1**

별빛처럼 사라지지 않는 기억을 기록하는 곳
DearTime은 흩어져 있는 개인의 기록과 감정을 정제된 디지털 유산으로 보존하는 기억 관리 플랫폼입니다.
직관적인 UX를 통해 추억을 안전하게 관리·공유하며, 과거의 기억을 미래의 가치로 이어줍니다.

## 주요 기능
- **갤러리 기록 기능**: 사진과 함께 추억을 기록하고 시간의 흐름에 따라 정리할 수 있습니다. 개인의 기억을 시각적으로 보존하며 의미 있는 순간을 한곳에 모을 수 있습니다.
- **우체통(편지) 기능**: 친구와 편지를 주고받으며 감정과 이야기를 기록할 수 있습니다. 말로 전하지 못한 마음을 글로 남기고, 시간이 지나 다시 꺼내볼 수 있습니다.
- **타임캡슐 기능**: 특정 시점에 열리는 타임캡슐을 만들어 미래의 나 또는 친구에게 메시지를 보낼 수 있습니다. 현재의 기억을 봉인해 미래의 가치로 이어줍니다.
- **친구목록 및 대리인 설정 기능**: 친구를 추가하고, 나의 기록을 관리해 줄 대리인을 설정할 수 있습니다. 개인 기록의 공유 범위를 조절하고, 안전하게 추억을 보존할 수 있습니다.

## 역할 분담

| 이름  | 역할분담 |
|-----| ------ |
| <a href="https://github.com/yoonjae-kwon">권윤재</a> |타임캡슐·알림(Web Socket)·aws 서버|
| <a href="https://github.com/nhyeonii">김나현</a> |회원가입/로그인·마이페이지·친구|
| <a href="https://github.com/dmseong">김성희</a> |편지·갤러리|

## CI/CD Pipeline
<img width="1900" height="800" alt="제목을-입력해주세요_-002" src="https://github.com/user-attachments/assets/02c51d3b-6df1-4064-b4cc-d334c6f66cd9" />

## System Architecture
### API Flow
| 구성 요소 | 설명 |
|---------|------|
| **API Request** | 프론트엔드는 `https://api.deartime.kr` 도메인을 통해 백엔드 서버로 API 요청을 전송합니다. |
| **AWS EC2 (Server)** | 백엔드 서버는 AWS EC2 인스턴스 위에서 동작하며, Nginx와 Spring Boot 애플리케이션을 포함합니다. |
| **Nginx (Reverse Proxy)** | HTTPS(SSL) 적용 및 보안 통신 처리, 80 포트 요청을 443 포트로 리다이렉트하며 외부 요청을 내부 Spring Boot 애플리케이션(8080 포트)으로 전달합니다. |
| **Docker Container (Spring Boot)** | Spring Boot 기반 백엔드 애플리케이션이 Docker 컨테이너 환경에서 실행되며, 회원 관리·편지·타임캡슐·갤러리 등 핵심 비즈니스 로직을 처리합니다. |
| **AWS RDS (PostgreSQL)** | 회원 정보, 편지 데이터, 타임캡슐 메타데이터 등 주요 서비스 데이터를 관계형 데이터베이스로 저장합니다. |
| **AWS S3** | 이미지 파일 등 대용량 정적 파일을 저장하는 객체 스토리지로 사용됩니다. |

---

### Security & Authentication

| 구성 요소 | 설명 |
|---------|------|
| **Certbot (Let’s Encrypt)** | Nginx에 SSL 인증서를 적용하여 HTTPS 통신을 지원하고 사용자 데이터 전송 보안을 강화합니다. |
| **Google OAuth 2.0** | 소셜 로그인을 통해 사용자 인증을 처리하여 간편하고 안전한 로그인 경험을 제공합니다. |

## Project Architecture
```
├─java
│  └─com
│      └─project
│          └─deartime
│              ├─app
│              │  ├─auth
│              │  ├─capsule
│              │  ├─config
│              │  ├─domain
│              │  ├─friend
│              │  ├─gallery
│              │  ├─letter
│              │  ├─notification
│              │  └─service
│              └─global
│                 ├─config
│                 ├─dto
│                 └─exception        
└─resources
    └─application.yml
``` 
