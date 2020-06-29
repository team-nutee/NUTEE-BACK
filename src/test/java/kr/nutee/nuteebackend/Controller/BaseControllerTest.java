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

    protected String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtZjMxMTEiLCJyb2xlIjoiUk9MRV9NQU5BR0VSIiwiaWQiOjIsImV4cCI6MTU5Mzg1MzAwNSwiaWF0IjoxNTkzMjQ4MjA1fQ.q7pO_JbnQ8_oFpDlr7W3athBLu6vHiBRu8a8LmmFccspKrdTX3eYBCCJIu6XvIiqdzRLDVPC_XuQJVptjwUb_g";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

}
