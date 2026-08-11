-- status: VARCHAR(20) → SMALLINT
ALTER TABLE "payment"."payments"
    DROP COLUMN IF EXISTS "status";

ALTER TABLE "payment"."payments"
    ADD COLUMN "status" SMALLINT NOT NULL DEFAULT 1;

-- method: VARCHAR(20) → SMALLINT
ALTER TABLE "payment"."payments"
    DROP COLUMN IF EXISTS "method";

ALTER TABLE "payment"."payments"
    ADD COLUMN "method" SMALLINT;

-- pg_provider 컬럼 추가 (1=TOSS | 2=NAVER | 3=KAKAO | 4=INICIS)
ALTER TABLE "payment"."payments"
    ADD COLUMN IF NOT EXISTS "pg_provider" SMALLINT;

-- order_id 컬럼 추가 (가맹점 주문번호, PG 콜백 역조회 키)
ALTER TABLE "payment"."payments"
    ADD COLUMN IF NOT EXISTS "order_id" VARCHAR(100);

ALTER TABLE "payment"."payments"
    DROP CONSTRAINT IF EXISTS "uk_payments_order_id";

ALTER TABLE "payment"."payments"
    ADD CONSTRAINT "uk_payments_order_id" UNIQUE ("order_id");

-- user_id 조회 인덱스
CREATE INDEX IF NOT EXISTS "idx_payments_user_id"
    ON "payment"."payments" ("user_id");

COMMENT ON COLUMN "payment"."payments"."status"      IS '1=READY | 2=APPROVING | 3=APPROVED | 4=FAILED | 5=CANCELED';
COMMENT ON COLUMN "payment"."payments"."method"      IS '1=CARD | 2=VIRTUAL_ACCOUNT | 3=EASY_PAY | 4=TRANSFER';
COMMENT ON COLUMN "payment"."payments"."pg_provider" IS '1=TOSS | 2=NAVER | 3=KAKAO | 4=INICIS';
COMMENT ON COLUMN "payment"."payments"."order_id"    IS '가맹점 주문번호 (PG 콜백 역조회 키)';