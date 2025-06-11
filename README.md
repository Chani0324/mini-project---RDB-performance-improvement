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

## 더미 데이터 설정
- 카테고리 500개
- 유저 10,000명
- 상품 10,000개
- 주문 30,000건
- 주문상품 약 90,000건

---


## 성능 테스트 간단 비교(local 에서만 진행!, 기본적으로 spring boot test로 진행하고 mysql과 네트워크 I/O는 존재하는걸 고려!)
- 상품에서 카테고리를 인덱스로 설정한것과 안한 것 조회.<br>
![image](https://github.com/user-attachments/assets/041f8570-5291-461d-80ad-c7af83a8f58d) <br>
대략 평균 4배정도 차이 발생. 단, 다른 table과 join같은건 하지 않고 product내에서만 진행한 결과.

