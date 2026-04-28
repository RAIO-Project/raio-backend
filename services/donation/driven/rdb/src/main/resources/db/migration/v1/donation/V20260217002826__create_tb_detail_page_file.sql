CREATE TABLE IF NOT EXISTS "product"."detail_page_file" (
    "id"          BIGINT,
    "user_id"     BIGINT       NOT NULL,
    "product_plan_id"     BIGINT    ,

    "type"        VARCHAR(40)  NOT NULL,
    "file_url"    TEXT         NOT NULL,
    "sort_order"  INT          NOT NULL DEFAULT 0,
    "meta_json"   JSONB        NULL,

    "created_at"  TIMESTAMP    DEFAULT NOW() NOT NULL,
    "updated_at"  TIMESTAMP    DEFAULT NOW() NOT NULL
);

ALTER TABLE "product"."detail_page_file"
    ADD CONSTRAINT "pk_detail_page_file" PRIMARY KEY ("id");

CREATE INDEX IF NOT EXISTS "idx_detail_page_file_plan_sort"
    ON "product"."detail_page_file" ("product_plan_id", "sort_order");

CREATE INDEX IF NOT EXISTS "idx_detail_page_file_user_plan"
    ON "product"."detail_page_file" ("user_id", "product_plan_id");