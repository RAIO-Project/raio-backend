INSERT INTO "payment"."wallets" (
    "id",
    "user_id",
    "balance",
    "created_at",
    "updated_at"
)
VALUES (
           1,
           1,
           100000,
           NOW(),
           NOW()
       )
ON CONFLICT ("user_id") DO NOTHING;