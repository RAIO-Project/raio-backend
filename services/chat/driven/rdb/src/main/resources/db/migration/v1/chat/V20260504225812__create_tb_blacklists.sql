CREATE TABLE IF NOT EXISTS "chat"."blacklist" (
  "id"           BIGINT        ,
  "user_id"      BIGINT        NOT NULL,
  "reason"       VARCHAR(100)  NULL,
  "unblock_at"   TIMESTAMP   NOT NULL,
  "created_at"   TIMESTAMP   DEFAULT NOW() NOT NULL
);

COMMENT ON COLUMN "chat"."blacklist"."id"           IS '블랙리스트 식별자';
COMMENT ON COLUMN "chat"."blacklist"."user_id"      IS '차단 대상 유저 ID';
COMMENT ON COLUMN "chat"."blacklist"."reason"       IS '차단 사유 (AI_PROFANITY_STRIKE_OUT 등)';
COMMENT ON COLUMN "chat"."blacklist"."unblock_at"   IS '차단 해제 일시';
COMMENT ON COLUMN "chat"."blacklist"."created_at"   IS '차단 발생 일시';

ALTER TABLE "chat"."blacklist" ADD CONSTRAINT "pk_blacklist" PRIMARY KEY ("id");