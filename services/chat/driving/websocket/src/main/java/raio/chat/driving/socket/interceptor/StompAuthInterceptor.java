package raio.chat.driving.socket.interceptor;

import org.springframework.http.server.*;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Map;

@Component
public class StompAuthInterceptor implements HandshakeInterceptor {

    public static final String USER_ID  = "userId";
    public static final String NICKNAME = "nickname";

    @Override
    public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse res,
                                   WebSocketHandler handler, Map<String, Object> attrs) {
        // FIXME: JWT 인증 연동 필요
//        String query = req.getURI().getQuery();
//        if (query != null) {
//            for (String pair : query.split("&")) {
//                int idx = pair.indexOf('=');
//                if (idx <= 0) continue;
//                String key = pair.substring(0, idx);
//                String val = pair.substring(idx + 1);
//                if ("userId".equals(key))   attrs.put(USER_ID, Long.parseLong(val));
//                if ("nickname".equals(key)) attrs.put(NICKNAME, val);
//            }
//        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest req, ServerHttpResponse res,
                               WebSocketHandler handler, Exception ex) {}
}