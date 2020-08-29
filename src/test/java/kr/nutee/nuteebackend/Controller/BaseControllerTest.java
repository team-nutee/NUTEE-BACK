package kr.nutee.nuteebackend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(MockitoExtension.class)
@Disabled
public class BaseControllerTest {

    protected String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtZjAwMDEiLCJyb2xlIjoiUk9MRV9NQU5BR0VSIiwiaWQiOjEsImV4cCI6MTkxMjA2NDU4NiwiaWF0IjoxNTk2NzA0NTg2fQ.VmpRq6R0NhyteAp2ToaPPbjAANcSfZTMKvrXxCd3iFBcm3gVLn9GYd6lJQ07gRIyk_U38x4t7VEpzA2qcbMAgA";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

}
