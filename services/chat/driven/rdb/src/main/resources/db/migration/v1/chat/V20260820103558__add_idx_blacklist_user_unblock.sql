-- 블랙리스트 활성 여부 조회용 인덱스
--
-- BlacklistQueryAdapter.existsActiveByUserId 가 캐시 미스마다 실행하는 쿼리:
--   SELECT MAX(unblock_at) FROM chat.blacklist WHERE user_id = ?
-- 인덱스가 없어 Seq Scan 을 탄다. 채팅 1건마다 호출되는 경로라 행이 쌓이면 그대로 비용이 된다.
--
-- 측정값 (로컬, 만료 이력으로 행수만 늘려 EXPLAIN ANALYZE)
--    30만행   21.1ms
--   100만행   48.5ms
--   300만행  123.1ms   →  인덱스 추가 후 0.13ms (947배), 인덱스 크기 21MB
--
-- 부하테스트에서 확인한 영향: 캐시 미스율 0→100% 로 올릴 때 채팅 처리량이
--   인덱스 없음 -32.5% / 인덱스 있음 -2.8%.
--   즉 지금은 Redis 캐시가 인덱스 부재를 가려주고 있고, Redis 유실 시 처리량이 3분의 1 가까이 빠진다.
--
-- 선두 컬럼을 user_id 로 두어 등호 조건을 인덱스로 처리하고,
-- unblock_at DESC 를 뒤에 붙여 MAX(unblock_at) 를 인덱스 스캔만으로 얻는다.

CREATE INDEX IF NOT EXISTS "idx_blacklist_user_unblock"
    ON "chat"."blacklist" ("user_id", "unblock_at" DESC);
