-- password: test
INSERT INTO "user"."users" (
    "id",
    "email",
    "password",
    "nickname",
    "phone_number",
    "role",
    "status",
    "last_login_at",
    "created_at",
    "updated_at"
)
VALUES (
           1,
           'test@test.com',
           '$2a$10$u1M5xP7Yw3Pj2lK8Q6Y7OeG7P2gZK7Q8F3Y6L9mWzN0xV1b2c3d4e',
           '테스트유저',
           '010-1234-5678',
           'USER',
           'ACTIVE',
           NOW(),
           NOW(),
           NOW()
       )
ON CONFLICT ("email") DO NOTHING;