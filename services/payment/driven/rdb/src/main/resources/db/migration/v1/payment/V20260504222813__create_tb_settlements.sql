CREATE TABLE IF NOT EXISTS "payment"."settlements" (
   "id"            BIGINT        ,
   "streamer_id"   BIGINT        NOT NULL,
   "total_amount"  BIGINT        NOT NULL,
   "fee_amount"    BIGINT        NOT NULL,
   "net_amount"    BIGINT        NOT NULL,
   "status"        VARCHAR(20)   NOT NULL,
   "created_at"    TIMESTAMP     DEFAULT NOW() NOT NULL
);

COMMENT ON COLUMN "payment"."settlements"."id" IS '정산 식별자';
COMMENT ON COLUMN "payment"."settlements"."streamer_id"   IS '대상 스트리머 ID';
COMMENT ON COLUMN "payment"."settlements"."total_amount"  IS '총 후원금 (수수료 전)';
COMMENT ON COLUMN "payment"."settlements"."fee_amount"    IS '플랫폼 수수료';
COMMENT ON COLUMN "payment"."settlements"."net_amount"    IS '실수령액 (total - fee)';
COMMENT ON COLUMN "payment"."settlements"."status"        IS 'REQUESTED | COMPLETED';
COMMENT ON COLUMN "payment"."settlements"."created_at"    IS '생성 일시';

ALTER TABLE "payment"."settlements" ADD CONSTRAINT "pk_settlements" PRIMARY KEY ("id");