package kr.nutee.nuteebackend.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

@Component
public class HttpInterceptor extends HandlerInterceptorAdapter {

    @Value("${jwt.secret}")
    String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        String token = request.getHeader("Authorization").split(" ")[1];
        Claims body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        response.addHeader("sub",body.getSubject());
        response.addHeader("role",body.get("role",String.class));
        response.addHeader("id",body.get("id",Integer.class).toString());
        return true;
    }
}
