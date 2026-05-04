CREATE TABLE IF NOT EXISTS "payment"."payments" (
    "id"           BIGINT        ,
    "user_id"      BIGINT        NOT NULL,
    "amount"       BIGINT        NOT NULL,
    "status"       VARCHAR(20)   NOT NULL,
    "method"       VARCHAR(20)   ,
    "external_tid" VARCHAR(255)  ,
    "fail_reason"  TEXT          ,
    "created_at"   TIMESTAMP   DEFAULT NOW() NOT NULL,
    "updated_at"   TIMESTAMP   DEFAULT NOW() NOT NULL
);

COMMENT ON COLUMN "payment"."payments"."id"           IS '결제 식별자 (PK)';
COMMENT ON COLUMN "payment"."payments"."user_id"      IS '결제 유저 ID';
COMMENT ON COLUMN "payment"."payments"."amount"       IS '결제 금액 (KRW)';
COMMENT ON COLUMN "payment"."payments"."status"       IS 'PENDING | SUCCESS | FAIL';
COMMENT ON COLUMN "payment"."payments"."method"       IS 'CARD | PAY 등 결제 수단';
COMMENT ON COLUMN "payment"."payments"."external_tid" IS 'PG사 거래 번호 (정산/대사 필수)';
COMMENT ON COLUMN "payment"."payments"."fail_reason"  IS '실패 사유';
COMMENT ON COLUMN "payment"."payments"."created_at"   IS '결제 요청 일시';
COMMENT ON COLUMN "payment"."payments"."updated_at"   IS '수정 일시';

ALTER TABLE "payment"."payments" ADD CONSTRAINT "pk_payments" PRIMARY KEY ("id");

