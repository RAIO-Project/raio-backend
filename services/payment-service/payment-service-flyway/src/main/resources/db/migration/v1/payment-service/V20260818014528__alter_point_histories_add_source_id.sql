-- source_id 컬럼 추가 (충전/환불 멱등성 검증용 - 원인 이벤트 ID, 예: Payment ID)
ALTER TABLE "payment"."point_histories"
    ADD COLUMN IF NOT EXISTS "source_id" BIGINT;

COMMENT ON COLUMN "payment"."point_histories"."source_id" IS '변동을 유발한 원인 이벤트 ID (충전/환불은 Payment ID) - 멱등성 검증용';

-- 동일 지갑/유형/원인 이벤트로 중복 반영되지 않도록 유니크 제약 추가
-- (source_id가 NULL인 기존 DONATION 등의 이력에는 영향 없음 - PostgreSQL은 NULL 간 유일성을 강제하지 않음)
ALTER TABLE "payment"."point_histories"
    ADD CONSTRAINT "uk_point_histories_wallet_type_source" UNIQUE ("wallet_id", "type", "source_id");
