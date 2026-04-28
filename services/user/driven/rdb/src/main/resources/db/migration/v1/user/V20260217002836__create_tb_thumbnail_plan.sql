CREATE TABLE IF NOT EXISTS "product"."thumbnail_plan" (
    "id"                 BIGINT,
    "user_id"            BIGINT        NOT NULL,
    "product_sourcing_id"    BIGINT        NULL,
    "name"               VARCHAR(255)  ,

    "status"             VARCHAR(30)   NOT NULL,
    "progress"           INT           NOT NULL DEFAULT 0,
    "current_step"       VARCHAR(60)   NULL,

    "payload_json"       JSONB         ,

    "error_code"         VARCHAR(60)   NULL,
    "error_message"      TEXT          NULL,

    "started_at"         TIMESTAMP     NULL,
    "finished_at"        TIMESTAMP     NULL,

    "created_at"         TIMESTAMP     DEFAULT NOW() NOT NULL,
    "updated_at"         TIMESTAMP     DEFAULT NOW() NOT NULL
    );

ALTER TABLE "product"."thumbnail_plan"
    ADD CONSTRAINT "pk_thumbnail_plan" PRIMARY KEY ("id");

CREATE INDEX IF NOT EXISTS "idx_thumbnail_plan_user_created_at_id"
    ON "product"."thumbnail_plan" ("user_id", "created_at" DESC, "id" DESC);

CREATE INDEX IF NOT EXISTS "idx_thumbnail_plan_user_sourcing"
    ON "product"."thumbnail_plan" ("user_id", "product_sourcing_id");

CREATE INDEX IF NOT EXISTS "idx_thumbnail_plan_user_status"
    ON "product"."thumbnail_plan" ("user_id", "status");-- Flyway Migration
-- Write your SQL below

