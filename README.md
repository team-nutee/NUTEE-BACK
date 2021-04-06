# NUTEE-SNS

## NUTEE 구조도
![NUTEE 구조도](https://user-images.githubusercontent.com/47442178/108618442-96779080-7461-11eb-819e-c8dd855a8070.jpg)

## SNS Server 구조도
![SNS 구조도](https://user-images.githubusercontent.com/47442178/112945366-269aab00-916f-11eb-8e91-3d515af644bc.png)


### 서비스 주요 기능
- NUTEE 통합 서비스의 SNS 커뮤니티 관련 API 제공
- 타임라인, 해시태그, 검색, 파일 업로드, 댓글, 답글, 신고 등의 기능 제공 

### 주요 사용 기술
- Spring Boot
- Spring Data JPA
- MySQL
- Kafka
- AWS RDS
- AWS S3
- AWS CloudFront

### 서비스 설계시 고려사항
- 서비스의 핵심 기능인 만큼, 코드의 변경과 확장에 유연하게 대응할 수 있도록 설계.
- 클라이언트가 사용하기 편한 API 제공.(Spring RestDocs를 활용한 문서 자동화)
- SNS 서비스에 연동된 NUTEE-ALARM 서비스가 비동기적으로 처리되도록 Kafka 사용.
- 다양한 동시성 문제를 해결하기 위해 트랜잭션을 관리하기 쉬운 RDBMS 사용.
- 이미지는 aws cloud front cdn을 사용하여 이미지 서버 부하를 분산.
