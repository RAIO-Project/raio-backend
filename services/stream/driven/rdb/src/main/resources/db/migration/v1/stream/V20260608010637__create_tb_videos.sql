CREATE TABLE IF NOT EXISTS "stream"."videos" (
     "id"                  BIGINT        NOT NULL,
     "uploader_id"         BIGINT        NOT NULL,
     "title"               VARCHAR(200),
     "original_file_name"  VARCHAR(255)  NOT NULL,
     "stored_file_path"    VARCHAR(500)  NOT NULL,
     "file_size"           BIGINT        NOT NULL,
     "content_type"        VARCHAR(100),
     "status"              VARCHAR(20)   NOT NULL DEFAULT 'READY',
     "created_at"          TIMESTAMP     NOT NULL DEFAULT NOW(),
     "updated_at"          TIMESTAMP     NOT NULL DEFAULT NOW()
);

ALTER TABLE "stream"."videos" ADD CONSTRAINT "pk_videos" PRIMARY KEY ("id");
