CREATE TABLE IF NOT EXISTS "payment"."point_histories" (
   "id"                BIGINT        ,
   "wallet_id"         BIGINT        NOT NULL,
   "user_id"           BIGINT        NOT NULL,
   "type"              VARCHAR(20)   NOT NULL,
   "amount"            BIGINT        NOT NULL,
   "balance_snapshot"  BIGINT        NOT NULL,
   "created_at"        TIMESTAMPTZ   DEFAULT NOW() NOT NULL
);

COMMENT ON COLUMN "payment"."point_histories"."id"                IS '원장 식별자';
COMMENT ON COLUMN "payment"."point_histories"."wallet_id"         IS '지갑 ID (성능상 FK 미사용)';
COMMENT ON COLUMN "payment"."point_histories"."user_id"           IS '대상 유저 ID';
COMMENT ON COLUMN "payment"."point_histories"."type"              IS 'CHARGE | DONATE | REFUND';
COMMENT ON COLUMN "payment"."point_histories"."amount"            IS '변동 금액';
COMMENT ON COLUMN "payment"."point_histories"."balance_snapshot"  IS '변동 후 잔액 스냅샷';
COMMENT ON COLUMN "payment"."point_histories"."created_at"        IS '발생 일시';

ALTER TABLE "payment"."point_histories" ADD CONSTRAINT "pk_point_histories" PRIMARY KEY ("id");
