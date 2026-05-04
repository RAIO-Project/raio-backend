CREATE TABLE IF NOT EXISTS "chat"."chat_logs" (
  "id"                BIGINT        NOT NULL,
  "stream_id"         BIGINT        NOT NULL,
  "user_id"           BIGINT        NOT NULL,
  "sender_nickname"   VARCHAR(50)   ,
  "message"           TEXT          NOT NULL,
  "is_blocked"        BOOLEAN       DEFAULT FALSE NOT NULL,
  "block_reason"      VARCHAR(100)  ,
  "created_at"        TIMESTAMP     NOT NULL,
  PRIMARY KEY ("id", "created_at")
) PARTITION BY RANGE ("created_at");

COMMENT ON COLUMN "chat"."chat_logs"."id"               IS '채팅 로그 식별자';
COMMENT ON COLUMN "chat"."chat_logs"."stream_id"        IS '방송 ID';
COMMENT ON COLUMN "chat"."chat_logs"."user_id"          IS '발화 유저 ID';
COMMENT ON COLUMN "chat"."chat_logs"."sender_nickname"  IS '채팅 당시 닉네임 (스냅샷)';
COMMENT ON COLUMN "chat"."chat_logs"."message"          IS '채팅 내용';
COMMENT ON COLUMN "chat"."chat_logs"."is_blocked"       IS '차단 여부';
COMMENT ON COLUMN "chat"."chat_logs"."block_reason"     IS 'PROFANITY | SPAM | AI_VERDICT';
COMMENT ON COLUMN "chat"."chat_logs"."created_at"       IS '발생 일시 (파티션 키)';

CREATE INDEX IF NOT EXISTS "idx_chat_logs_stream_created_at"
    ON "chat"."chat_logs" ("stream_id", "created_at" DESC);

CREATE INDEX IF NOT EXISTS "idx_chat_logs_user_created_at"
    ON "chat"."chat_logs" ("user_id", "created_at" DESC);