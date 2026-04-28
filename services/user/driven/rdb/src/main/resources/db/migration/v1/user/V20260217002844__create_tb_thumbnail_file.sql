CREATE TABLE IF NOT EXISTS "product"."thumbnail_file" (
    "id"          BIGINT,
    "user_id"     BIGINT       NOT NULL,
    "product_plan_id"     BIGINT    ,

    "type"        VARCHAR(40)  NOT NULL,
    "file_url"    TEXT         ,
    "sort_order"  INT          DEFAULT 0,
    "meta_json"   JSONB        NULL,

    "created_at"  TIMESTAMP    DEFAULT NOW() NOT NULL,
    "updated_at"  TIMESTAMP    DEFAULT NOW() NOT NULL
    );

ALTER TABLE "product"."thumbnail_file"
    ADD CONSTRAINT "pk_thumbnail_file" PRIMARY KEY ("id");

CREATE INDEX IF NOT EXISTS "idx_thumbnail_file_plan_sort"
    ON "product"."thumbnail_file" ("product_plan_id", "sort_order");

CREATE INDEX IF NOT EXISTS "idx_thumbnail_file_user_plan"
    ON "product"."thumbnail_file" ("user_id", "product_plan_id");