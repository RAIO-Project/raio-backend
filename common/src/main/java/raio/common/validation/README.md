<br />

---
<a id="ko"></a>

- ▶⠀⠀Korean
- ⠀⠀⠀[English](#en)
- ⠀⠀⠀[Japanese](#ja)

> Last updated at: 2025-08-14

# 유효성 체크

- `Preconditions`: 사전조건을 검토합니다. 네이밍과 달리 사후조건 체크로 활용할 수도 있습니다.

## 지원 기능

**지원 대상 타입**

- `Object`: `validateNotNull`
- `String`: `validateNotNull`, `validateNotEmpty`, `validateNotBlank`, `validateLength`, `validateMin`, `validateMax`,  
  `validateRegex`
- `Collection`: `validateNotNull`, `validateNotEmpty`, `validateLength`, `validateMin`, `validateMax`
- Number types (`≤ 8 Bytes`): `validateMinMax`, `validateMin`, `validateMax`

### 간단한 예시

- 본 프로젝트는 유효성 관련 정적 메서드의 static import를 허용하므로 예시를 보실 때 참고 부탁드립니다.

```java
// 콤팩트 생성자에서 유효성을 체크하는 예시
public Example {
    // [1] 공백 검증: `title`은 `null`, `""`, `" "` 등 공백만 있거나 비어 있으면 안 됩니다.
    validateNotBlank(title, TITLE_REQUIRED);
    
    // [2] 전처리: `title`의 앞뒤 공백을 절삭한 다음 유효성 검증을 이어갈 수도 있습니다.
    title = title.strip();

    // [3] 길이 검증: `title`의 길이는 3 이상 100 이하여야 합니다.
    validateMin(title, 3, TITLE_TOO_SHORT);
    validateMax(title, 100, TITLE_TOO_LONG);
}
```

### 제안 구조

```java
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_ID_REQUIRED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_INPUT_TYPE_MISMATCHED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_NAME_INVALID_LENGTH;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_NAME_REQUIRED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_NICKNAME_REQUIRED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_OWNER_ID_REQUIRED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_PROFILE_ID_REQUIRED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_URL_INVALID_FORMAT;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_URL_INVALID_LENGTH;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_URL_REQUIRED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_USERNAME_REQUIRED;
import static validation.club.common.Preconditions.validateLength;
import static validation.club.common.Preconditions.validateNotBlank;
import static validation.club.common.Preconditions.validateNotNull;
import static validation.club.common.Preconditions.validateRegex;

public final class BlogValidator {
    
    private BlogValidator() {
    }
    
    public static void validate(BlogValidationTarget field, Object value) {
        switch (field) {
            case BLOG_ID -> validateNotNull(value, BLOG_ID_REQUIRED);
            case BLOG_USER_ID -> {
                String str = castToString(value);
                validateNotBlank(str, BLOG_OWNER_ID_REQUIRED);
            }
            case BLOG_PROFILE_ID -> {
                String str = castToString(value);
                validateNotBlank(str, BLOG_PROFILE_ID_REQUIRED);
            }
            case BLOG_USERNAME -> {
                String str = castToString(value);
                validateNotBlank(str, BLOG_USERNAME_REQUIRED);
            }
            case BLOG_NICKNAME -> {
                String str = castToString(value);
                validateNotBlank(str, BLOG_NICKNAME_REQUIRED);
            }
            case BLOG_NAME -> {
                String str = castToString(value);
                validateNotBlank(str, BLOG_NAME_REQUIRED);
                
                str = str.strip();
                validateLength(str, 3, 30, BLOG_NAME_INVALID_LENGTH);
            }
            case BLOG_URL_IDENTIFIER -> {
                String str = castToString(value);
                validateNotBlank(str, BLOG_URL_REQUIRED);
                
                str = str.strip();
                validateRegex(str, "^[A-Za-z0-9_-]+$", BLOG_URL_INVALID_FORMAT);
                validateLength(str, 3, 15, BLOG_URL_INVALID_LENGTH);
            }
        }
    }
    
    private static String castToString(Object value) {
        if (value == null) return null;
        
        if (!(value instanceof String str)) {
            throw BLOG_INPUT_TYPE_MISMATCHED.exception();
        }
        
        return str;
    }
    
    public enum BlogValidationTarget {
        BLOG_ID,
        BLOG_USER_ID,
        BLOG_PROFILE_ID,
        BLOG_NAME,
        BLOG_URL_IDENTIFIER,
        BLOG_USERNAME,
        BLOG_NICKNAME
    }
}
```

```java
@Builder
public record BlogUpdateCommand(
        @Schema(description = "블로그 이름", example = "Sun☀️ Moon🌙")
        String name,
        @Schema(description = "블로그 주소 식별자", example = "wch-os")
        String urlIdentifier
) {
    public BlogUpdateCommand {
        validate(NAME, name);
        validate(URL_IDENTIFIER, urlIdentifier);
    }
}
```

<br />

---
<a id="en"></a>

- ⠀⠀⠀[Korean](#ko)
- ▶⠀⠀English
- ⠀⠀⠀[Japanese](#ja)

> Last updated: 2025-08-14 07:00 AM

# Validation

- `Preconditions`: Validate preconditions. Despite the name, it can also be used for postcondition checks.

## Supported Features

**Supported Target Types**

- `Object`: `validateNotNull`
- `String`: `validateNotNull`, `validateNotEmpty`, `validateNotBlank`, `validateLength`, `validateMin`, `validateMax`,  
  `validateRegex`
- `Collection`: `validateNotNull`, `validateNotEmpty`, `validateLength`, `validateMin`, `validateMax`
- Number types (`≤ 8 Bytes`): `validateMinMax`, `validateMin`, `validateMax`

### Quick Example

- This project allows static imports of validation-related methods; please keep that in mind when reading the examples.

```java
// Example: validating inside a compact constructor
public Example {
    // [1] Whitespace check: `title` must not be `null`, `""`, or consist only of whitespace such as `" "`.
    validateNotBlank(title, TITLE_REQUIRED);
    
    // [2] Preprocessing: You may trim leading and trailing whitespace from `title` and then continue validation.
    title = title.strip();

    // [3] Length check: the length of `title` must be between 3 and 100 (inclusive).
    validateMin(title, 3, TITLE_TOO_SHORT);
    validateMax(title, 100, TITLE_TOO_LONG);
}
```

### Proposed Structure

```java
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_ID_REQUIRED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_INPUT_TYPE_MISMATCHED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_NAME_INVALID_LENGTH;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_NAME_REQUIRED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_NICKNAME_REQUIRED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_OWNER_ID_REQUIRED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_PROFILE_ID_REQUIRED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_URL_INVALID_FORMAT;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_URL_INVALID_LENGTH;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_URL_REQUIRED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_USERNAME_REQUIRED;
import static validation.club.common.Preconditions.validateLength;
import static validation.club.common.Preconditions.validateNotBlank;
import static validation.club.common.Preconditions.validateNotNull;
import static validation.club.common.Preconditions.validateRegex;

public final class BlogValidator {
    
    private BlogValidator() {
    }
    
    public static void validate(BlogValidationTarget field, Object value) {
        switch (field) {
            case BLOG_ID -> validateNotNull(value, BLOG_ID_REQUIRED);
            case BLOG_USER_ID -> {
                String str = castToString(value);
                validateNotBlank(str, BLOG_OWNER_ID_REQUIRED);
            }
            case BLOG_PROFILE_ID -> {
                String str = castToString(value);
                validateNotBlank(str, BLOG_PROFILE_ID_REQUIRED);
            }
            case BLOG_USERNAME -> {
                String str = castToString(value);
                validateNotBlank(str, BLOG_USERNAME_REQUIRED);
            }
            case BLOG_NICKNAME -> {
                String str = castToString(value);
                validateNotBlank(str, BLOG_NICKNAME_REQUIRED);
            }
            case BLOG_NAME -> {
                String str = castToString(value);
                validateNotBlank(str, BLOG_NAME_REQUIRED);
                
                str = str.strip();
                validateLength(str, 3, 30, BLOG_NAME_INVALID_LENGTH);
            }
            case BLOG_URL_IDENTIFIER -> {
                String str = castToString(value);
                validateNotBlank(str, BLOG_URL_REQUIRED);
                
                str = str.strip();
                validateRegex(str, "^[A-Za-z0-9_-]+$", BLOG_URL_INVALID_FORMAT);
                validateLength(str, 3, 15, BLOG_URL_INVALID_LENGTH);
            }
        }
    }
    
    private static String castToString(Object value) {
        if (value == null) return null;
        
        if (!(value instanceof String str)) {
            throw BLOG_INPUT_TYPE_MISMATCHED.exception();
        }
        
        return str;
    }
    
    public enum BlogValidationTarget {
        BLOG_ID,
        BLOG_USER_ID,
        BLOG_PROFILE_ID,
        BLOG_NAME,
        BLOG_URL_IDENTIFIER,
        BLOG_USERNAME,
        BLOG_NICKNAME
    }
}
```

```java
@Builder
public record BlogUpdateCommand(
        @Schema(description = "Blog name", example = "Sun☀️ Moon🌙")
        String name,
        @Schema(description = "Blog URL identifier", example = "wch-os")
        String urlIdentifier
) {
    public BlogUpdateCommand {
        validate(NAME, name);
        validate(URL_IDENTIFIER, urlIdentifier);
    }
}
```

<br />

---
<a id="ja"></a>

- ⠀⠀⠀[Korean](#ko)
- ⠀⠀⠀[English](#en)
- ▶⠀⠀Japanese

> 最終更新: 2025-08-14 午前7:00

# バリデーション

- `Preconditions`: 前提条件を検証します。命名に反して、事後条件のチェックとしても利用できます。

## 対応機能

**サポート対象の型**

- `Object`: `validateNotNull`
- `String`: `validateNotNull`, `validateNotEmpty`, `validateNotBlank`, `validateLength`, `validateMin`, `validateMax`,  
  `validateRegex`
- `Collection`: `validateNotNull`, `validateNotEmpty`, `validateLength`, `validateMin`, `validateMax`
- 数値型（8バイト以下）: `validateMinMax`, `validateMin`, `validateMax`

### 簡単な例

- 本プロジェクトではバリデーション関連の静的メソッドの static import を許可しているため、以下の例では static import を前提としています。

```java
// コンパクトコンストラクタでバリデーションを行う例
public Example {
    // [1] 空白検証: `title` は `null`、`""`、`" "` など空白のみ、または空の場合にはできません。
    validateNotBlank(title, TITLE_REQUIRED);
    
    // [2] 前処理: `title` の前後の空白を除去してからバリデーションを続けられます。
    title = title.strip();

    // [3] 長さ検証: `title` の長さは 3 以上 100 以下である必要があります。
    validateMin(title, 3, TITLE_TOO_SHORT);
    validateMax(title, 100, TITLE_TOO_LONG);
}
```

### 推奨構成

```java
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_ID_REQUIRED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_INPUT_TYPE_MISMATCHED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_NAME_INVALID_LENGTH;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_NAME_REQUIRED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_NICKNAME_REQUIRED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_OWNER_ID_REQUIRED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_PROFILE_ID_REQUIRED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_URL_INVALID_FORMAT;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_URL_INVALID_LENGTH;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_URL_REQUIRED;
import static nettee.blolet.blog.exception.BlogErrorCode.BLOG_USERNAME_REQUIRED;
import static validation.club.common.Preconditions.validateLength;
import static validation.club.common.Preconditions.validateNotBlank;
import static validation.club.common.Preconditions.validateNotNull;
import static validation.club.common.Preconditions.validateRegex;

public final class BlogValidator {
    
    private BlogValidator() {
    }
    
    public static void validate(BlogValidationTarget field, Object value) {
        switch (field) {
            case BLOG_ID -> validateNotNull(value, BLOG_ID_REQUIRED);
            case BLOG_USER_ID -> {
                String str = castToString(value);
                validateNotBlank(str, BLOG_OWNER_ID_REQUIRED);
            }
            case BLOG_PROFILE_ID -> {
                String str = castToString(value);
                validateNotBlank(str, BLOG_PROFILE_ID_REQUIRED);
            }
            case BLOG_USERNAME -> {
                String str = castToString(value);
                validateNotBlank(str, BLOG_USERNAME_REQUIRED);
            }
            case BLOG_NICKNAME -> {
                String str = castToString(value);
                validateNotBlank(str, BLOG_NICKNAME_REQUIRED);
            }
            case BLOG_NAME -> {
                String str = castToString(value);
                validateNotBlank(str, BLOG_NAME_REQUIRED);
                
                str = str.strip();
                validateLength(str, 3, 30, BLOG_NAME_INVALID_LENGTH);
            }
            case BLOG_URL_IDENTIFIER -> {
                String str = castToString(value);
                validateNotBlank(str, BLOG_URL_REQUIRED);
                
                str = str.strip();
                validateRegex(str, "^[A-Za-z0-9_-]+$", BLOG_URL_INVALID_FORMAT);
                validateLength(str, 3, 15, BLOG_URL_INVALID_LENGTH);
            }
        }
    }
    
    private static String castToString(Object value) {
        if (value == null) return null;
        
        if (!(value instanceof String str)) {
            throw BLOG_INPUT_TYPE_MISMATCHED.exception();
        }
        
        return str;
    }
    
    public enum BlogValidationTarget {
        BLOG_ID,
        BLOG_USER_ID,
        BLOG_PROFILE_ID,
        BLOG_NAME,
        BLOG_URL_IDENTIFIER,
        BLOG_USERNAME,
        BLOG_NICKNAME
    }
}
```

```java
@Builder
public record BlogUpdateCommand(
        @Schema(description = "ブログ名", example = "Sun☀️ Moon🌙")
        String name,
        @Schema(description = "ブログのURL識別子", example = "wch-os")
        String urlIdentifier
) {
    public BlogUpdateCommand {
        validate(NAME, name);
        validate(URL_IDENTIFIER, urlIdentifier);
    }
}
```