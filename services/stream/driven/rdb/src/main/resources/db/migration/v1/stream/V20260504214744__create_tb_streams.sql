CREATE TABLE IF NOT EXISTS "stream"."streams" (
      "id"                BIGINT        ,
      "streamer_id"       BIGINT        NOT NULL,
      "title"             VARCHAR(255)  NOT NULL,
      "category"          VARCHAR(50)   ,
      "max_viewer_count"  INTEGER       DEFAULT 0 NOT NULL,
      "status"            VARCHAR(20)   DEFAULT 'READY' NOT NULL,
      "started_at"        TIMESTAMP    ,
      "ended_at"          TIMESTAMP    ,
      "created_at"        TIMESTAMP    DEFAULT NOW() NOT NULL,
      "updated_at"        TIMESTAMP    DEFAULT NOW() NOT NULL
);

COMMENT ON COLUMN "stream"."streams"."id"               IS '방송 고유 식별자';
COMMENT ON COLUMN "stream"."streams"."streamer_id"      IS '스트리머 ID (users.user_id FK)';
COMMENT ON COLUMN "stream"."streams"."title"            IS '방송 제목';
COMMENT ON COLUMN "stream"."streams"."category"         IS '방송 카테고리';
COMMENT ON COLUMN "stream"."streams"."max_viewer_count" IS '최고 동시 시청자 수 (종료 시 Redis 동기화)';
COMMENT ON COLUMN "stream"."streams"."status"           IS 'READY | LIVE | ENDED';
COMMENT ON COLUMN "stream"."streams"."started_at"       IS '실제 송출 시작 일시';
COMMENT ON COLUMN "stream"."streams"."ended_at"         IS '방송 종료 일시';
COMMENT ON COLUMN "stream"."streams"."created_at"       IS '방송 생성 일시';
COMMENT ON COLUMN "stream"."streams"."updated_at"       IS '수정 일시';

ALTER TABLE "stream"."streams" ADD CONSTRAINT "pk_streams" PRIMARY KEY ("id");