CREATE TABLE IF NOT EXISTS "product"."detail_page_plan" (
    "id"                 BIGINT,
    "user_id"            BIGINT        NOT NULL,
    "product_sourcing_id"        BIGINT        NULL,
    "name"               VARCHAR(255)  NOT NULL,

    "status"             VARCHAR(30)   NOT NULL,
    "progress"           INT           NOT NULL DEFAULT 0,
    "current_step"       VARCHAR(60)   NULL,

    "payload_json"       JSONB         NULL,
    "result_summary_json" JSONB        NULL,

    "error_code"         VARCHAR(60)   NULL,
    "error_message"      TEXT          NULL,

    "started_at"         TIMESTAMP     NULL,
    "finished_at"        TIMESTAMP     NULL,

    "created_at"         TIMESTAMP     DEFAULT NOW() NOT NULL,
    "updated_at"         TIMESTAMP     DEFAULT NOW() NOT NULL
    );

ALTER TABLE "product"."detail_page_plan"
    ADD CONSTRAINT "pk_detail_page_plan" PRIMARY KEY ("id");

CREATE INDEX IF NOT EXISTS "idx_detail_page_plan_user_created_at_id"
    ON "product"."detail_page_plan" ("user_id", "created_at" DESC, "id" DESC);

CREATE INDEX IF NOT EXISTS "idx_detail_page_plan_user_sourcing"
    ON "product"."detail_page_plan" ("user_id", "product_sourcing_id");

CREATE INDEX IF NOT EXISTS "idx_detail_page_plan_user_status"
    ON "product"."detail_page_plan" ("user_id", "status");

