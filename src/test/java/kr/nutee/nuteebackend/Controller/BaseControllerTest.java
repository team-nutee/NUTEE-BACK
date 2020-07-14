package kr.nutee.nuteebackend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Disabled
public class BaseControllerTest {

    protected String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtZjMxMTAiLCJyb2xlIjoiUk9MRV9NQU5BR0VSIiwiaWQiOjEsImV4cCI6MTU5NTI0Mjg5OCwiaWF0IjoxNTk0NjM4MDk4fQ.yddp_qOLybxXgegDB8knvv6ECEU82Rxc1GEsauT89EPpVh1ua-KGeheNws-EbB74_F_FsbSfII_OsLnLg9pr9A";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

}
