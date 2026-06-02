-- status: VARCHAR -> SMALLINT (1=READY, 2=LIVE, 3=ENDED)
ALTER TABLE "stream"."streams" ALTER COLUMN "status" DROP DEFAULT;
ALTER TABLE "stream"."streams"
ALTER COLUMN "status" TYPE SMALLINT
    USING (CASE "status"
        WHEN 'READY' THEN 1
        WHEN 'LIVE'  THEN 2
        WHEN 'ENDED' THEN 3
    END);
ALTER TABLE "stream"."streams" ALTER COLUMN "status" SET DEFAULT 1;
COMMENT ON COLUMN "stream"."streams"."status" IS '1=READY | 2=LIVE | 3=ENDED';

-- category: VARCHAR -> SMALLINT (1~7)
ALTER TABLE "stream"."streams"
ALTER COLUMN "category" TYPE SMALLINT
    USING (CASE "category"
        WHEN 'GAMING'   THEN 1
        WHEN 'MUKBANG'  THEN 2
        WHEN 'TALK'     THEN 3
        WHEN 'MUSIC'    THEN 4
        WHEN 'STUDY'    THEN 5
        WHEN 'SPORTS'   THEN 6
        WHEN 'CREATIVE' THEN 7
        ELSE NULL
    END);
COMMENT ON COLUMN "stream"."streams"."category" IS '1=GAMING | 2=MUKBANG | 3=TALK | 4=MUSIC | 5=STUDY | 6=SPORTS | 7=CREATIVE';