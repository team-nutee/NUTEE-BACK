package kr.nutee.nuteebackend.Controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import kr.nutee.nuteebackend.DTO.Request.CreatePostRequest;
import kr.nutee.nuteebackend.Enum.Interest;
import kr.nutee.nuteebackend.Enum.Major;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class CategoryControllerTest extends BaseControllerTest  {

    @Test @Order(1)
    @DisplayName("내가 댓글 작성한 게시글 호출 성공")
    void getInterests() throws Exception {
        //given
        int size = Interest.values().length;

        //when
        MockHttpServletRequestBuilder builder = get("/sns/category/interests")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaTypes.HAL_JSON_VALUE);

        //then
        mockMvc.perform(builder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
            .andExpect(jsonPath("code").exists())
            .andExpect(jsonPath("message").exists())
            .andExpect(jsonPath("body",hasSize(size)))
            .andExpect(jsonPath("_links.self").exists())
            .andDo(document("get-interests"));
    }

    @Test @Order(1)
    @DisplayName("내가 댓글 작성한 게시글 호출 성공")
    void getMajors() throws Exception {
        //given
        int size = Major.values().length;

        //when
        MockHttpServletRequestBuilder builder = get("/sns/category/majors")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaTypes.HAL_JSON_VALUE);

        //then
        mockMvc.perform(builder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
            .andExpect(jsonPath("code").exists())
            .andExpect(jsonPath("message").exists())
            .andExpect(jsonPath("body",hasSize(size)))
            .andExpect(jsonPath("_links.self").exists())
            .andDo(document("get-majors"));
    }
}
