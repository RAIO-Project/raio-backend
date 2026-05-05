CREATE SCHEMA IF NOT EXISTS "payment";

CREATE TABLE IF NOT EXISTS "payment"."wallets" (
   "id"          BIGINT        ,
   "user_id"     BIGINT        NOT NULL,
   "balance"     BIGINT        DEFAULT 0 NOT NULL,
   "created_at"  TIMESTAMPTZ   DEFAULT NOW() NOT NULL,
   "updated_at"  TIMESTAMPTZ   DEFAULT NOW() NOT NULL
);

COMMENT ON COLUMN "payment"."wallets"."id"         IS '지갑 식별자';
COMMENT ON COLUMN "payment"."wallets"."user_id"    IS '소유자 ID (1인 1지갑)';
COMMENT ON COLUMN "payment"."wallets"."balance"    IS '현재 잔액 (원 단위)';
COMMENT ON COLUMN "payment"."wallets"."created_at" IS '생성 일시';
COMMENT ON COLUMN "payment"."wallets"."updated_at" IS '마지막 변동 일시';

ALTER TABLE "payment"."wallets" ADD CONSTRAINT "pk_wallets" PRIMARY KEY ("id");
ALTER TABLE "payment"."wallets" ADD CONSTRAINT "uk_wallets_user" UNIQUE ("user_id");