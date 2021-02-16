package kr.nutee.nuteebackend.Controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class SearchControllerTest extends BaseControllerTest{

    @Test
    @DisplayName("검색 성공")
    void getSearchPosts() throws Exception {
        //given
        String text = "내용";
        Long lastId = 0L;
        int limit = 10;

        //when
        MockHttpServletRequestBuilder builder = get("/sns/search/{text}",text)
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
            .andExpect(jsonPath("body",hasSize(7)))
            .andExpect(jsonPath("_links.self").exists())
            .andDo(document("get-search-posts"));


    }
}
