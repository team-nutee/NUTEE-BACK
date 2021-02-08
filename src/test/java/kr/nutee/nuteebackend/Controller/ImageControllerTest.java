package kr.nutee.nuteebackend.Controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import kr.nutee.nuteebackend.DTO.Request.CreatePostRequest;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class ImageControllerTest extends BaseControllerTest {

    @Test
    @DisplayName("이미지 등록 성공")
    void uploadImage() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile(
            "images",
            "imageName.jpg",
            String.valueOf(ContentType.IMAGE_JPEG),
            "hello file".getBytes()
        );

        //when
        MockHttpServletRequestBuilder builder = multipart("/sns/upload")
            .file(file)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .header("Authorization", "Bearer " + token)
            .accept(MediaTypes.HAL_JSON_VALUE)
            .content("{}");

        //then
        mockMvc.perform(builder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
            .andExpect(jsonPath("code").exists())
            .andExpect(jsonPath("message").exists())
            .andExpect(jsonPath("body",hasSize(1)))
            .andExpect(jsonPath("_links.self").exists())
            .andDo(document("upload-images"));


    }
}
