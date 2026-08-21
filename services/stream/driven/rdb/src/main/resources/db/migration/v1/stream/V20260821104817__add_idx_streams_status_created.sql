-- 방송 목록 조회(키셋 페이징) 인덱스
--
-- GET /streams -> StreamQueryApi.getByStatuses -> StreamQueryAdapter.findByStatusesAfter:
--   WHERE created_at > ? AND status IN (...) [AND category = ?]
--   ORDER BY created_at DESC LIMIT ?
--
-- 테이블이 34행 / 8KB(한 페이지)라서 플래너가 Seq Scan 을 고른다.
-- 인덱스를 만들고 EXPLAIN 한 결과도 Seq Scan(cost 1.54, 0.073ms) 이었다.
--
-- 그래도 지금 넣는 이유:
--   1) 종료된 방송이 계속 누적되는 테이블이다. 현재도 34행 중 33행이 종료 상태이므로
--      status 로 진행 중인 방송만 걸러내는 조건은 갈수록 선택적으로 바뀐다.
--      10만행으로 합성해 측정했을 때 44.4ms -> 27.2ms (-39%), 처리량 +37% 였다.
--   2) CREATE INDEX 는 ACCESS EXCLUSIVE 락을 잡아 쓰기를 막는다. 나중에 행이 쌓인
--      뒤에 걸면 방송 생성·상태 변경이 그 시간만큼 멈춘다. 지금은 8KB 라 사실상 0초다.
--      (CONCURRENTLY 는 Flyway 가 마이그레이션을 트랜잭션으로 감싸므로 별도 설정이 필요하다.)
--
-- title LIKE '%...%' 조건은 btree 로 처리할 수 없어 이 인덱스 범위 밖이다.
CREATE INDEX IF NOT EXISTS "idx_streams_status_created"
    ON "stream"."streams" ("status", "created_at" DESC);
