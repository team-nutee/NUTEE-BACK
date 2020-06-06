package kr.nutee.nuteebackend.Controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class RestTest {

    RestTemplate restTemplate;

    @Test
    public void test_getForObject() {
        String str = restTemplate.getForObject("http://localhost:8080/auth/test", String.class, 25);
        System.out.println(str);
    }
}
