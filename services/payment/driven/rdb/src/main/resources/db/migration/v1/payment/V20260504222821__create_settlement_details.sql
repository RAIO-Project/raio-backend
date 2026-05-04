CREATE TABLE IF NOT EXISTS "payment"."settlement_details" (
    "id"     BIGINT        ,
    "settlement_id" BIGINT        NOT NULL,
    "history_id"    BIGINT        NOT NULL,
    "amount"        BIGINT        NOT NULL,
    "created_at"    TIMESTAMP     DEFAULT NOW() NOT NULL
);

COMMENT ON COLUMN "payment"."settlement_details"."id"            IS '상세 식별자';
COMMENT ON COLUMN "payment"."settlement_details"."settlement_id" IS '부모 정산 ID';
COMMENT ON COLUMN "payment"."settlement_details"."history_id"    IS '원장 ID (중복 집계 방지)';
COMMENT ON COLUMN "payment"."settlement_details"."amount"        IS '해당 건 금액';
COMMENT ON COLUMN "payment"."settlement_details"."created_at"    IS '생성 일시';

ALTER TABLE "payment"."settlement_details" ADD CONSTRAINT "pk_settlement_details" PRIMARY KEY ("id");