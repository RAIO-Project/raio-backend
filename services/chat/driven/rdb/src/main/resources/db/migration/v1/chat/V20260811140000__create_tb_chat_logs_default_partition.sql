CREATE TABLE IF NOT EXISTS "chat"."chat_logs_default"
    PARTITION OF "chat"."chat_logs" DEFAULT;
