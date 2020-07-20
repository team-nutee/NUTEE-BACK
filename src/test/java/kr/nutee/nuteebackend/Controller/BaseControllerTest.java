package kr.nutee.nuteebackend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nutee.nuteebackend.Controller.Common.RestDocsConfiguration;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
@Disabled
@Transactional
public class BaseControllerTest {

    protected String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtZjMxMTAiLCJyb2xlIjoiUk9MRV9NQU5BR0VSIiwiaWQiOjEsImV4cCI6MTU5NTc1MjA0NSwiaWF0IjoxNTk1MTQ3MjQ1fQ.wi-xBh_Mj6gpM8zAcvoOwUf9gCR2dT0JdDa6QuvuXgIADxlFCGB9nxqKw9SETqCrBm_KUYC0TQgGHst_bx3zEA";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeAll
    static void setDatabase(){

    }

}
