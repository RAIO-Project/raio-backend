CREATE SCHEMA IF NOT EXISTS "payment";

CREATE TABLE IF NOT EXISTS "payment"."settlement_settings" (
   "streamer_id"                  BIGINT      ,
   "current_cycle"                SMALLINT    NOT NULL,
   "pending_cycle"                SMALLINT    ,
   "pending_cycle_effective_at"   TIMESTAMP   ,
   "next_settlement_at"           TIMESTAMP   NOT NULL,
   "last_settled_at"              TIMESTAMP   ,
   "active"                       BOOLEAN     DEFAULT TRUE NOT NULL,
   "created_at"                   TIMESTAMP   DEFAULT NOW() NOT NULL,
   "updated_at"                   TIMESTAMP   DEFAULT NOW() NOT NULL
);

COMMENT ON COLUMN "payment"."settlement_settings"."streamer_id"                IS '정산 설정 대상 스트리머 ID (PK, 1인 1설정)';
COMMENT ON COLUMN "payment"."settlement_settings"."current_cycle"              IS '1=DAILY | 2=WEEKLY | 3=MONTHLY';
COMMENT ON COLUMN "payment"."settlement_settings"."pending_cycle"              IS '다음 정산 기간부터 적용할 예약 주기';
COMMENT ON COLUMN "payment"."settlement_settings"."pending_cycle_effective_at" IS '예약된 주기 변경이 적용되는 정산 기간 시작 시각';
COMMENT ON COLUMN "payment"."settlement_settings"."next_settlement_at"         IS '다음 정산 실행 예정 시각 (배치 대상 선정 기준)';
COMMENT ON COLUMN "payment"."settlement_settings"."last_settled_at"           IS '마지막으로 정산이 완료된 기간의 종료 시각';
COMMENT ON COLUMN "payment"."settlement_settings"."active"                    IS '정산 설정 활성 여부';
COMMENT ON COLUMN "payment"."settlement_settings"."created_at"                IS '생성 일시';
COMMENT ON COLUMN "payment"."settlement_settings"."updated_at"                IS '마지막 변경 일시';

ALTER TABLE "payment"."settlement_settings" ADD CONSTRAINT "pk_settlement_settings" PRIMARY KEY ("streamer_id");
CREATE INDEX IF NOT EXISTS "idx_settlement_settings_active_next_settlement_at" ON "payment"."settlement_settings" ("active", "next_settlement_at");
