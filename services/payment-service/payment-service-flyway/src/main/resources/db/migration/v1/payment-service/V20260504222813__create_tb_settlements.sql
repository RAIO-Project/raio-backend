CREATE SCHEMA IF NOT EXISTS "payment";

CREATE TABLE IF NOT EXISTS "payment"."settlements" (
   "id"                VARCHAR(36)     ,
   "streamer_id"       BIGINT          NOT NULL,
   "cycle"             SMALLINT        NOT NULL,
   "period_start_at"   TIMESTAMP       NOT NULL,
   "period_end_at"     TIMESTAMP       NOT NULL,
   "gross_amount"      NUMERIC(19,2)   NOT NULL,
   "applied_fee_rate"  NUMERIC(5,4)    NOT NULL,
   "fee_amount"        NUMERIC(19,2)   NOT NULL,
   "net_amount"        NUMERIC(19,2)   NOT NULL,
   "status"            SMALLINT        NOT NULL,
   "created_at"        TIMESTAMP       DEFAULT NOW() NOT NULL
);

COMMENT ON COLUMN "payment"."settlements"."id"                IS '정산 식별자 (UUID)';
COMMENT ON COLUMN "payment"."settlements"."streamer_id"       IS '대상 스트리머 ID';
COMMENT ON COLUMN "payment"."settlements"."cycle"             IS '1=DAILY | 2=WEEKLY | 3=MONTHLY';
COMMENT ON COLUMN "payment"."settlements"."period_start_at"   IS '정산 대상 기간 시작 시각 (포함)';
COMMENT ON COLUMN "payment"."settlements"."period_end_at"     IS '정산 대상 기간 종료 시각 (미포함)';
COMMENT ON COLUMN "payment"."settlements"."gross_amount"      IS '총 후원금 (수수료 전)';
COMMENT ON COLUMN "payment"."settlements"."applied_fee_rate"  IS '실제 적용된 수수료율';
COMMENT ON COLUMN "payment"."settlements"."fee_amount"        IS '플랫폼 수수료';
COMMENT ON COLUMN "payment"."settlements"."net_amount"        IS '실수령액 (gross - fee)';
COMMENT ON COLUMN "payment"."settlements"."status"            IS '1=CALCULATING | 2=CALCULATED | 3=CONFIRMED | 4=CANCELLED';
COMMENT ON COLUMN "payment"."settlements"."created_at"        IS '생성 일시';

ALTER TABLE "payment"."settlements" ADD CONSTRAINT "pk_settlements" PRIMARY KEY ("id");
ALTER TABLE "payment"."settlements" ADD CONSTRAINT "uk_settlements_streamer_period" UNIQUE ("streamer_id", "period_start_at", "period_end_at");
