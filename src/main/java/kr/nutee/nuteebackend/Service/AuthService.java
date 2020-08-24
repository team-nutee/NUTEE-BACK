package kr.nutee.nuteebackend.Service;

import kr.nutee.nuteebackend.DTO.API.LoginRequest;
import kr.nutee.nuteebackend.DTO.API.LoginResponse;
import kr.nutee.nuteebackend.DTO.LoginToken;
import kr.nutee.nuteebackend.DTO.Resource.ResponseResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Slf4j
@Service
public class AuthService {

    private final RestTemplate restTemplate;

    public AuthService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public LoginResponse getLoginToken(LoginRequest body) {
        ResponseResource responseResource = restTemplate.postForObject("http://localhost:8080/auth/login", body, ResponseResource.class);
        assert responseResource != null;
        return (LoginResponse) Objects.requireNonNull(responseResource.getContent()).getBody();
    }

}
