package kr.nutee.nuteebackend.Controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import kr.nutee.nuteebackend.DTO.Request.CreatePostRequest;
import kr.nutee.nuteebackend.Enum.InterestCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class HashtagControllerTest extends BaseControllerTest {

    @Test
    @DisplayName("해쉬태그 목록 읽기 성공")
    void getHashtagPosts() throws Exception {
        //given
        String hashtag = "해쉬태그";
        Long lastId = 0L;
        int limit = 10;

        postService.createPost(1L, CreatePostRequest.builder()
            .content("#해쉬태그")
            .category(InterestCategory.ANIMAL.getInterest())
            .title("제목")
            .build());

        //when
        MockHttpServletRequestBuilder builder = get("/sns/hashtag/{hashtag}",hashtag)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token)
            .accept(MediaTypes.HAL_JSON_VALUE)
            .content("{}")
            .param("lastId", String.valueOf(lastId))
            .param("limit", String.valueOf(limit));

        //then
        mockMvc.perform(builder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
            .andExpect(jsonPath("code").exists())
            .andExpect(jsonPath("message").exists())
            .andExpect(jsonPath("body",hasSize(1)))
            .andExpect(jsonPath("_links.self").exists())
            .andDo(document("get-hashtag-posts"));


    }
}
