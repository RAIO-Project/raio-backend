CREATE SCHEMA IF NOT EXISTS "payment";

CREATE TABLE IF NOT EXISTS "payment"."settlement_items" (
   "id"                   VARCHAR(36)     ,
   "settlement_id"        VARCHAR(36)     NOT NULL,
   "donation_id"          BIGINT          NOT NULL,
   "gross_amount"         NUMERIC(19,2)   NOT NULL,
   "applied_fee_rate"     NUMERIC(5,4)    NOT NULL,
   "fee_amount"           NUMERIC(19,2)   NOT NULL,
   "net_amount"           NUMERIC(19,2)   NOT NULL,
   "revenue_occurred_at"  TIMESTAMP       NOT NULL
);

COMMENT ON COLUMN "payment"."settlement_items"."id"                  IS '정산 항목 식별자 (UUID)';
COMMENT ON COLUMN "payment"."settlement_items"."settlement_id"       IS '부모 정산 ID (FK 제약 없음 - INSERT 성능)';
COMMENT ON COLUMN "payment"."settlement_items"."donation_id"         IS '원본 후원 ID (중복 정산 방지 기준)';
COMMENT ON COLUMN "payment"."settlement_items"."gross_amount"        IS '수수료 차감 전 후원 금액';
COMMENT ON COLUMN "payment"."settlement_items"."applied_fee_rate"    IS '해당 후원 건에 실제 적용된 수수료율';
COMMENT ON COLUMN "payment"."settlement_items"."fee_amount"          IS '차감된 플랫폼 수수료';
COMMENT ON COLUMN "payment"."settlement_items"."net_amount"          IS '스트리머 정산 금액 (gross - fee)';
COMMENT ON COLUMN "payment"."settlement_items"."revenue_occurred_at" IS '원본 후원 수익 발생 시각';

ALTER TABLE "payment"."settlement_items" ADD CONSTRAINT "pk_settlement_items" PRIMARY KEY ("id");
ALTER TABLE "payment"."settlement_items" ADD CONSTRAINT "uk_settlement_items_donation_id" UNIQUE ("donation_id");
CREATE INDEX IF NOT EXISTS "idx_settlement_items_settlement_id" ON "payment"."settlement_items" ("settlement_id");
