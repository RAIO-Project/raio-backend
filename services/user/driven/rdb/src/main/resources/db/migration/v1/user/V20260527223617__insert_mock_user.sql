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
           '$2a$10$Dh5BmaTO6cFYzKnus4kugOC6PAb3yuNm1Ebot.isOpRNC/CMVO/m.',
           '테스트유저',
           '010-1234-5678',
           'USER',
           'ACTIVE',
           NOW(),
           NOW(),
           NOW()
       )
ON CONFLICT ("email") DO NOTHING;