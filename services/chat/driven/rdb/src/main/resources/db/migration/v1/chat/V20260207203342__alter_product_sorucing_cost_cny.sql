-- cost_cny: BIGINT -> NUMERIC(10,2)
ALTER TABLE "product"."product_sourcing"
    ALTER COLUMN "cost_cny"
        TYPE NUMERIC(10,2)
        USING "cost_cny"::NUMERIC(10,2);

COMMENT ON COLUMN "product"."product_sourcing"."cost_cny"
    IS '엑셀: 원가(위안)(F열) – 소수점 허용';