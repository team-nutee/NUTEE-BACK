package kr.nutee.nuteebackend.Interceptor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class HttpInterceptor extends HandlerInterceptorAdapter {

    @Value("${jwt.secret}")
    String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    public Map<String,Object> getTokenInfo(HttpServletRequest request){
        Map<String,Object> map = new HashMap<>();
        String token = request.getHeader("Authorization").split(" ")[1];
        Claims body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        map.put("sub",body.getSubject());
        map.put("role",body.get("role",String.class));
        map.put("id",body.get("id",Integer.class).toString());
        return map;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        String token = request.getHeader("Authorization").split(" ")[1];
        Claims body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        Map<String,Object> map = new HashMap<>();
        map.put("sub",body.getSubject());
        map.put("role",body.get("role",String.class));
        map.put("id",body.get("id",Integer.class));
        request.setAttribute("user",map);
        return true;
    }
}
