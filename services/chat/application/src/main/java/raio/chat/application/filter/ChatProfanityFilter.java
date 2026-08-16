package raio.chat.application.filter;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 채팅 정규식 1차 필터.
 * <b>명백한</b> 위반은
 * 여기서 즉시 걸러 AI 호출 자체를 줄인다.
 *
 * <h3>정밀도 우선</h3>
 * <p>맥락에 따라 혐오가 될 수도, 아닐 수도 있는 표현은 여기서 판정하지 않는다. 이 필터에 걸린
 * 메시지는 한순간도 노출되지 않으므로, 오탐이 나면 정상 대화가 통째로 사라진다. 그래서 재현율이
 * 아니라 <b>정밀도</b>를 우선한다 — 애매하면 통과시켜 AI 판정에 맡긴다.
 *
 * <h3>성능</h3>
 * <p>단어는 카테고리별 리스트로 관리하되, 실제 매칭은 전부 합쳐 컴파일한 단일 {@link Pattern} 으로
 * 수행한다. 패턴을 하나씩 순회하며 매칭하면 단어 수만큼 정규식 엔진을 반복 실행하게 되지만,
 * 하나로 합치면 엔진이 단일 패스로 처리하므로 단어가 늘어도 비용이 거의 증가하지 않는다.
 * 패턴은 클래스 로딩 시 한 번만 컴파일되며, {@link Pattern} 은 스레드 안전하므로 매 요청 재사용한다.
 *
 * <h3>현재 한계</h3>
 * <p>금칙어를 하드코딩하고 있어 단어를 추가하려면 재배포가 필요하다. 부하 테스트를 위한 임시
 * 구성이며, 이후 DB 나 Redis 로 옮겨 갱신 가능하게 만들어야 한다.
 *
 * <p>또한 표기를 변형한 우회(중간에 공백·특수문자 삽입 등)는 잡지 못한다. 정규화 전처리가 필요한
 * 별도 문제이며, 여기서 놓친 표현은 AI 판정으로 넘어가므로 완전히 누락되지는 않는다.
 */
@Component
public class ChatProfanityFilter {

    // ===== 욕설/비속어 =====
    private static final List<String> PROFANITY = List.of(
            "씨발", "시발", "씨팔", "시팔", "씨바", "쓰발", "ㅆㅂ", "ㅅㅂ",
            "개새끼", "개새기", "개색기", "개색끼", "개세끼",
            "병신", "병싄", "븅신", "빙신", "ㅂㅅ",
            "지랄", "지럴", "ㅈㄹ",
            "좆까", "좆같", "좆나", "존나", "존내", "졸라", "ㅈㄴ",
            "미친놈", "미친년", "미친새끼", "미친자식",
            "닥쳐", "닥치라", "닥칠래",
            "새끼야", "새꺄", "썅놈", "썅년", "쌍놈", "쌍년",
            "개소리", "개지랄", "개수작",
            "느금마", "느검마", "니미", "니애미", "니엄마", "애미없",
            "꺼져라", "꺼지라고", "뒈져", "뒤져라"
    );

    // ===== 성적 표현 =====
    private static final List<String> SEXUAL = List.of(
            "걸레년", "창녀", "섹스"
    );

    // ===== 혐오/차별 표현 =====
    private static final List<String> HATE_SPEECH = List.of(
            "장애인새끼", "장애인병신", "정신병자새끼", "애자새끼",
            "틀딱", "연금충", "노인충", "급식충", "맘충", "설명충",
            "김치녀", "한남충", "된장녀", "보슬아치",
            "짱깨", "짱개", "쪽바리", "쪽발이", "왜놈",
            "깜둥이", "니그로",
            "전라디언", "홍어새끼", "과메기충",
            "게이새끼", "호모새끼", "트랜스새끼"
    );

    // ===== 폭력/협박 =====
    private static final List<String> THREAT = List.of(
            "죽여버린다", "죽여버릴", "죽여줄까", "죽여버려",
            "패죽인다", "때려죽인다", "칼로찌를",
            "자살해라", "죽어버려", "뒤지고싶냐"
    );

    // ===== 우회 표기 =====
    private static final List<String> BYPASS = List.of(
            "18놈", "18년아", "18새끼",
            "ㅅㅂㄹㅁ", "ㄲㅈ",
            "ㄴㄱㅁ", "ㅄ", "ㅆㄹㄱ"
    );

    /**
     * 전체 카테고리를 합쳐 한 번만 컴파일한 패턴.
     *
     * <p>{@link Pattern#quote} 로 각 단어를 리터럴 처리해, 단어에 정규식 특수문자가 섞여 있어도
     * 의도치 않게 해석되지 않도록 한다.
     */
    private static final Pattern EXPLICIT_VIOLATION_PATTERN = Pattern.compile(
            Stream.of(PROFANITY, SEXUAL, HATE_SPEECH, THREAT, BYPASS)
                    .flatMap(List::stream)
                    .map(Pattern::quote)
                    .collect(Collectors.joining("|")),
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 명백한 위반이면 true.
     *
     * <p>AI 호출 없이 즉시 차단해도 되는 확신 수준일 때만 true 를 반환한다. 판단이 애매하면
     * false 를 반환해 AI 판정으로 넘긴다.
     */
    public boolean containsProfanity(String message) {
        if (message == null || message.isBlank()) {
            return false;
        }
        return EXPLICIT_VIOLATION_PATTERN.matcher(message).find();
    }
}
