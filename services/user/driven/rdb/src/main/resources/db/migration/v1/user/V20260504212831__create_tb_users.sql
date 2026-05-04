CREATE SCHEMA IF NOT EXISTS "user";

CREATE TABLE IF NOT EXISTS "user"."users" (
  "id"            BIGINT        ,
  "email"         VARCHAR(100)  NOT NULL,
  "password"      VARCHAR(255)  NOT NULL,
  "nickname"      VARCHAR(50)   ,
  "phone_number"  VARCHAR(20)   ,
  "role"          VARCHAR(20)   DEFAULT 'USER',
  "status"        VARCHAR(20)   DEFAULT 'ACTIVE',
  "last_login_at" TIMESTAMP     ,
  "created_at"    TIMESTAMP     DEFAULT NOW() NOT NULL,
  "updated_at"    TIMESTAMP     DEFAULT NOW() NOT NULL
);

COMMENT ON COLUMN "user"."users"."id"            IS '사용자 고유 식별자';
COMMENT ON COLUMN "user"."users"."email"         IS '로그인 ID용 이메일';
COMMENT ON COLUMN "user"."users"."password"      IS 'BCrypt 단방향 해시';
COMMENT ON COLUMN "user"."users"."nickname"      IS '서비스 내 표시 닉네임';
COMMENT ON COLUMN "user"."users"."phone_number"  IS '전화번호(선택)';
COMMENT ON COLUMN "user"."users"."role"          IS 'USER | ADMIN';
COMMENT ON COLUMN "user"."users"."status"        IS 'ACTIVE | SUSPENDED | DELETED';
COMMENT ON COLUMN "user"."users"."last_login_at" IS '마지막 로그인 일시(감사용)';
COMMENT ON COLUMN "user"."users"."created_at"    IS '가입 일시';
COMMENT ON COLUMN "user"."users"."updated_at"    IS '수정 일시';

ALTER TABLE "user"."users" ADD CONSTRAINT "pk_users" PRIMARY KEY ("id");
ALTER TABLE "user"."users" ADD CONSTRAINT "uk_users_email" UNIQUE ("email");
