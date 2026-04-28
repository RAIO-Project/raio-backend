-- ======================================================================
-- Schema: product
-- Table : china_import_calc
-- Desc  : 상품 소싱(product_sourcing) 단위로 중국 사입 계산기(입력/계산 스냅샷)를 저장한다.
--         - 1개 소싱 상품(product_sourcing_id) 당 1개 계산기 레코드(유니크)로 운영
--         - created_at/updated_at은 애플리케이션에서 세팅하거나(권장) 트리거로 갱신 가능
-- ======================================================================

CREATE SCHEMA IF NOT EXISTS "product";

CREATE TABLE IF NOT EXISTS "product"."china_import_calc" (
    -- PK (Snowflake)
    "id"                           BIGINT,

    -- 공통 컬럼(시간대 포함)
    "created_at"                   TIMESTAMP WITH TIME ZONE,
    "updated_at"                   TIMESTAMP WITH TIME ZONE,

    -- 소유자 / 연결 키
    "user_id"                      BIGINT,
    "product_sourcing_id"          BIGINT,

    -- =========================
    -- inputs (사용자 입력값)
    -- =========================
    -- 환율(원/위안)
    "fx_rate_krw_per_cny"          NUMERIC(12,4),
    -- 포워딩 수수료율(0~1)
    "forwarding_fee_rate"          NUMERIC(8,6),
    -- 상품 단가(위안)
    "unit_cost_cny"                NUMERIC(14,6),
    -- 수량
    "qty"                          INTEGER,

    -- 박스 규격(단위: cm)
    "size_w_cm"                    NUMERIC(10,3),
    "size_d_cm"                    NUMERIC(10,3),
    "size_h_cm"                    NUMERIC(10,3),

    -- 부대비용(단위: 원)
    -- 검수/포장(개당)
    "inspection_pack_per_unit_krw" BIGINT,
    -- 바코드(개당)
    "barcode_per_unit_krw"         BIGINT,
    -- 박스/파렛트(총액)
    "box_pallet_total_krw"         BIGINT,
    -- CBM 단가(원/CBM)
    "cbm_rate_krw"                 BIGINT,

    -- 통관/서류/작업비(단위: 원)
    "customs_broker_krw"           BIGINT,
    "container_work_krw"           BIGINT,
    "bl_issue_krw"                 BIGINT,
    "fta_issue_krw"                BIGINT,

    -- 국내 운송(단위: 원)
    "domestic_shipping_krw"        BIGINT,
    "milk_run_krw"                 BIGINT,

    -- =========================
    -- computed snapshot (계산 스냅샷)
    -- 저장 시점 결과를 보존하여 재현/감사/회귀에 사용
    -- =========================
    -- 중국 내륙 운송비(위안)
    "inland_shipping_cny"          NUMERIC(14,6),
    -- 검수/포장 총액(원)
    "inspection_pack_total_krw"    BIGINT,
    -- 바코드 총액(원)
    "barcode_total_krw"            BIGINT,
    -- 상품 매입금(원)
    "product_purchase_krw"         BIGINT,
    -- CBM 비용(원)
    "cbm_fee_krw"                  BIGINT,
    -- 통관 완료 총액(원)
    "customs_done_total_krw"       BIGINT,
    -- 부가세(원)
    "vat_krw"                      BIGINT,
    -- 총 비용(원)
    "total_krw"                    BIGINT,
    -- 개당 원가(원)
    "unit_cost_krw"                BIGINT,
    -- 배수(예: 원가 대비 배수 등, 시트 정의에 따름)
    "multiple"                     NUMERIC(14,6),
    -- 예상 박스 수(시트 정의에 따름)
    "estimated_box_count"          INTEGER
    );

-- =========================
-- Column Comments
-- =========================
COMMENT ON COLUMN "product"."china_import_calc"."id"                           IS '중국 사입 계산기 PK (Snowflake)';
COMMENT ON COLUMN "product"."china_import_calc"."created_at"                   IS '생성일자(타임존 포함)';
COMMENT ON COLUMN "product"."china_import_calc"."updated_at"                   IS '수정일자(타임존 포함)';
COMMENT ON COLUMN "product"."china_import_calc"."user_id"                      IS '소유 사용자 ID';
COMMENT ON COLUMN "product"."china_import_calc"."product_sourcing_id"          IS '연결된 상품 소싱 ID(product.product_sourcing.id)';

COMMENT ON COLUMN "product"."china_import_calc"."fx_rate_krw_per_cny"          IS '입력: 환율(원/위안)';
COMMENT ON COLUMN "product"."china_import_calc"."forwarding_fee_rate"          IS '입력: 포워딩 수수료율(0~1)';
COMMENT ON COLUMN "product"."china_import_calc"."unit_cost_cny"                IS '입력: 단가(위안)';
COMMENT ON COLUMN "product"."china_import_calc"."qty"                          IS '입력: 수량';

COMMENT ON COLUMN "product"."china_import_calc"."size_w_cm"                    IS '입력: 박스 가로(cm)';
COMMENT ON COLUMN "product"."china_import_calc"."size_d_cm"                    IS '입력: 박스 세로/깊이(cm)';
COMMENT ON COLUMN "product"."china_import_calc"."size_h_cm"                    IS '입력: 박스 높이(cm)';

COMMENT ON COLUMN "product"."china_import_calc"."inspection_pack_per_unit_krw" IS '입력: 검수/포장(개당, 원)';
COMMENT ON COLUMN "product"."china_import_calc"."barcode_per_unit_krw"         IS '입력: 바코드(개당, 원)';
COMMENT ON COLUMN "product"."china_import_calc"."box_pallet_total_krw"         IS '입력: 박스/파렛트(총액, 원)';
COMMENT ON COLUMN "product"."china_import_calc"."cbm_rate_krw"                 IS '입력: CBM 단가(원/CBM)';

COMMENT ON COLUMN "product"."china_import_calc"."customs_broker_krw"           IS '입력: 관세사 비용(원)';
COMMENT ON COLUMN "product"."china_import_calc"."container_work_krw"           IS '입력: 컨테이너 작업비(원)';
COMMENT ON COLUMN "product"."china_import_calc"."bl_issue_krw"                 IS '입력: BL 발급비(원)';
COMMENT ON COLUMN "product"."china_import_calc"."fta_issue_krw"                IS '입력: FTA 발급비(원)';

COMMENT ON COLUMN "product"."china_import_calc"."domestic_shipping_krw"        IS '입력: 국내 운송비(원)';
COMMENT ON COLUMN "product"."china_import_calc"."milk_run_krw"                 IS '입력: 밀크런 비용(원)';

COMMENT ON COLUMN "product"."china_import_calc"."inland_shipping_cny"          IS '계산: 중국 내륙 운송비(위안)';
COMMENT ON COLUMN "product"."china_import_calc"."inspection_pack_total_krw"    IS '계산: 검수/포장 총액(원)';
COMMENT ON COLUMN "product"."china_import_calc"."barcode_total_krw"            IS '계산: 바코드 총액(원)';
COMMENT ON COLUMN "product"."china_import_calc"."product_purchase_krw"         IS '계산: 상품 매입금(원)';
COMMENT ON COLUMN "product"."china_import_calc"."cbm_fee_krw"                  IS '계산: CBM 비용(원)';
COMMENT ON COLUMN "product"."china_import_calc"."customs_done_total_krw"       IS '계산: 통관 완료 총액(원)';
COMMENT ON COLUMN "product"."china_import_calc"."vat_krw"                      IS '계산: 부가세(원)';
COMMENT ON COLUMN "product"."china_import_calc"."total_krw"                    IS '계산: 총 비용(원)';
COMMENT ON COLUMN "product"."china_import_calc"."unit_cost_krw"                IS '계산: 개당 원가(원)';
COMMENT ON COLUMN "product"."china_import_calc"."multiple"                     IS '계산: 배수(시트 정의 기준)';
COMMENT ON COLUMN "product"."china_import_calc"."estimated_box_count"          IS '계산: 예상 박스 수(시트 정의 기준)';

-- =========================
-- Constraints / Indexes
-- =========================
ALTER TABLE "product"."china_import_calc"
    ADD CONSTRAINT "pk_china_import_calc" PRIMARY KEY ("id");

-- 소싱 상품 1개당 계산기 레코드 1개 보장 (운영 정책)
CREATE UNIQUE INDEX IF NOT EXISTS "ux_china_import_calc_sourcing_id"
    ON "product"."china_import_calc" ("product_sourcing_id");

-- 유저별 조회 최적화
CREATE INDEX IF NOT EXISTS "ix_china_import_calc_user_id"
    ON "product"."china_import_calc" ("user_id");
