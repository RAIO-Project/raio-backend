-- 닉네임 유일성 제약 (조회 인덱스 겸용)
--
-- 두 가지를 한 번에 해결한다.
--
-- 1) 조회 성능
--    RegisterService 가 회원가입마다 실행하는 중복 검사:
--      SELECT EXISTS(SELECT 1 FROM "user".users WHERE nickname = ?)
--    인덱스가 없어 Seq Scan 을 탔다. 같은 목적의 email 은 uk_users_email 로
--    Index Only Scan 을 타서 1,100행 기준 0.114ms 인데, nickname 은 0.638ms 였다 (5.6배).
--    유저가 늘면 선형으로 느려지고, 회원가입이 커넥션을 그만큼 오래 잡는다.
--
-- 2) 경쟁 조건
--    RegisterService 는 existsByNickname 으로 확인한 뒤 INSERT 하는 구조다.
--    동시 요청이 같은 닉네임으로 들어오면 둘 다 검사를 통과해 중복이 저장된다.
--    애플리케이션 검사만으로는 막을 수 없고 DB 제약이 필요하다.
--    (email 은 이미 uk_users_email 로 막혀 있다 — nickname 만 빠져 있었다)
--
-- nickname 은 nullable 이지만 PostgreSQL 의 UNIQUE 는 NULL 을 서로 다른 값으로 보아
-- 여러 개 허용하므로 충돌하지 않는다. 닉네임 없는 가입은 그대로 동작한다.
--
-- ⚠ 배포 전 확인: 이미 중복 닉네임이 있으면 이 마이그레이션이 실패하고 앱이 기동하지 않는다.
--    SELECT nickname, count(*) FROM "user".users
--     WHERE nickname IS NOT NULL GROUP BY nickname HAVING count(*) > 1;
--    결과가 비어 있어야 안전하다.

ALTER TABLE "user"."users"
    ADD CONSTRAINT "uk_users_nickname" UNIQUE ("nickname");
