# 프로젝트 진행 방향
- java 21, spring boot 3.4.6, mysql 8.0으로 진행
- 주요 포인트
  - 인덱스 유무에 따른 조회 성능 비교
  - 동기/비동기 insert 성능 비교(플랫폼 스레드와 가상 스레드 사용 예정. java 21 버전 선택의 주된 인자)
  - 단일/배치 insert 비교
  - 격리수준에 따른 성능 차이 비교
  - 락 규모에 따른 성능 차이 비교
  - join 최적화에 따른 성능 비교
### 모든건 쿼리 실행 계획 분석을 토대로 진행

---

## ERD

![image](https://github.com/user-attachments/assets/c1308ed8-3cd7-4a35-96bf-1466213015ee)

---
