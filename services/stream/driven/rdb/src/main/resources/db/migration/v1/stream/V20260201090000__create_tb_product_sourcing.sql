CREATE SCHEMA IF NOT EXISTS "product";

CREATE TABLE IF NOT EXISTS "product"."product_sourcing" (
    "id"                         BIGINT         ,
    -- 소유자
    "user_id"                    BIGINT         NOT NULL,
    -- 엑셀: 키워드 (B열)
    "keyword"                    VARCHAR(255)   ,
    -- 엑셀: 참고 상품 (C열)
    "reference_product"          VARCHAR(255)   ,
    -- 엑셀: 1688 URL (D열)
    "source_url"                 TEXT           ,
    -- 엑셀: 이미지 (E열)
    "image_url"                  TEXT           ,
    -- 엑셀: 상품명 (Q열)
    "name"                       VARCHAR(255)   ,
    -- 엑셀: 원가(위안) (F열)
    "cost_cny"                   BIGINT         ,
    -- 엑셀: 원가(원) (G열)
    "cost_krw"                   BIGINT         ,
    -- 엑셀: 쿠팡 카테고리 (H열)
    "coupang_category"           VARCHAR(255)   ,
    -- 엑셀: 입출고 및 배송 카테고리 (I열)
    "wing_logistics_category"    VARCHAR(255)   ,
    -- 엑셀: 판매가 (J열)
    "sale_price_krw"             BIGINT         ,
    -- 엑셀: 수수료(%) (K열)
    "fee_rate_percent"           NUMERIC(10,4)  ,
    -- 엑셀: 판매 수수료(원) (L열)
    "fee_amount_krw"             BIGINT         ,
    -- 엑셀: 부가세 (M열)
    "vat_krw"                    BIGINT         ,
    -- 엑셀: 그로스 마진 (N열)
    "gross_margin_krw"           BIGINT         ,
    -- 엑셀: 그로스 마진율(%) (O열)
    "gross_margin_rate_percent"  NUMERIC(10,4)  ,
    -- 엑셀: 최소 광고 수익율(%) (P열)
    "min_ad_roi_percent"         NUMERIC(10,4)  ,
    -- 공통 컬럼
    "created_at"                 TIMESTAMP     DEFAULT NOW() NOT NULL,
    "updated_at"                 TIMESTAMP     DEFAULT NOW() NOT NULL
    );

COMMENT ON COLUMN "product"."product_sourcing"."id"                        IS '상품 소싱 PK (Snowflake)';
COMMENT ON COLUMN "product"."product_sourcing"."created_at"                IS '생성일자';
COMMENT ON COLUMN "product"."product_sourcing"."updated_at"                IS '수정일자';
COMMENT ON COLUMN "product"."product_sourcing"."user_id"                   IS '소유 사용자 ID';
COMMENT ON COLUMN "product"."product_sourcing"."keyword"                   IS '엑셀: 키워드(B열)';
COMMENT ON COLUMN "product"."product_sourcing"."reference_product"         IS '엑셀: 참고 상품(C열)';
COMMENT ON COLUMN "product"."product_sourcing"."source_url"                IS '엑셀: 1688 URL(D열)';
COMMENT ON COLUMN "product"."product_sourcing"."image_url"                 IS '엑셀: 이미지(E열)';
COMMENT ON COLUMN "product"."product_sourcing"."name"                      IS '엑셀: 상품명(Q열)';
COMMENT ON COLUMN "product"."product_sourcing"."cost_cny"                  IS '엑셀: 원가(위안)(F열)';
COMMENT ON COLUMN "product"."product_sourcing"."cost_krw"                  IS '엑셀: 원가(원)(G열)';
COMMENT ON COLUMN "product"."product_sourcing"."coupang_category"          IS '엑셀: 쿠팡 카테고리(H열)';
COMMENT ON COLUMN "product"."product_sourcing"."wing_logistics_category"   IS '엑셀: 입출고/배송 카테고리(I열)';
COMMENT ON COLUMN "product"."product_sourcing"."sale_price_krw"            IS '엑셀: 판매가(J열)';
COMMENT ON COLUMN "product"."product_sourcing"."fee_rate_percent"          IS '엑셀: 수수료율(%)(K열)';
COMMENT ON COLUMN "product"."product_sourcing"."fee_amount_krw"            IS '엑셀: 판매 수수료(원)(L열)';
COMMENT ON COLUMN "product"."product_sourcing"."vat_krw"                   IS '엑셀: 부가세(M열)';
COMMENT ON COLUMN "product"."product_sourcing"."gross_margin_krw"          IS '엑셀: 그로스 마진(N열)';
COMMENT ON COLUMN "product"."product_sourcing"."gross_margin_rate_percent" IS '엑셀: 그로스 마진율(%)(O열)';
COMMENT ON COLUMN "product"."product_sourcing"."min_ad_roi_percent"        IS '엑셀: 최소 광고 수익률(%)(P열)';

ALTER TABLE "product"."product_sourcing" ADD CONSTRAINT "pk_product_sourcing" PRIMARY KEY ("id");

CREATE INDEX IF NOT EXISTS "idx_product_sourcing_user_created_at_id"
    ON "product"."product_sourcing" ("user_id", "created_at" DESC, id DESC);

CREATE INDEX IF NOT EXISTS "idx_product_sourcing_user_keyword"
    ON "product"."product_sourcing" ("user_id", "keyword");
