CREATE SCHEMA IF NOT EXISTS "donation";

CREATE TABLE IF NOT EXISTS "donation"."donations" (
     "id"           BIGINT        ,
     "stream_id"    BIGINT        NOT NULL,
     "sender_id"    BIGINT        NOT NULL,
     "receiver_id"  BIGINT        NOT NULL,
     "amount"       BIGINT        NOT NULL,
     "message"      TEXT          ,
     "is_blocked"   BOOLEAN       DEFAULT FALSE NOT NULL,
     "is_refunded"  BOOLEAN       DEFAULT FALSE NOT NULL,
     "status"       VARCHAR(20)   NOT NULL,
     "created_at"   TIMESTAMP     DEFAULT NOW() NOT NULL,
     "updated_at"   TIMESTAMP     DEFAULT NOW() NOT NULL
);

COMMENT ON COLUMN "donation"."donations"."id"          IS '후원 이벤트 식별자';
COMMENT ON COLUMN "donation"."donations"."stream_id"   IS '방송 ID';
COMMENT ON COLUMN "donation"."donations"."sender_id"   IS '후원자(시청자) ID';
COMMENT ON COLUMN "donation"."donations"."receiver_id" IS '수령자(스트리머) ID';
COMMENT ON COLUMN "donation"."donations"."amount"      IS '후원 금액 (원 단위)';
COMMENT ON COLUMN "donation"."donations"."message"     IS '후원 메시지';
COMMENT ON COLUMN "donation"."donations"."is_blocked"  IS 'AI 욕설 차단 여부';
COMMENT ON COLUMN "donation"."donations"."is_refunded" IS '환불 처리 여부';
COMMENT ON COLUMN "donation"."donations"."status"      IS 'COMPLETED | FAILED';
COMMENT ON COLUMN "donation"."donations"."created_at"  IS '후원 발생 일시';
COMMENT ON COLUMN "donation"."donations"."created_at"  IS '후원 발생 일시';

ALTER TABLE "donation"."donations" ADD CONSTRAINT "pk_donations" PRIMARY KEY ("id");