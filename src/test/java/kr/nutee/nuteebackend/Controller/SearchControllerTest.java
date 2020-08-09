package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.Common.RestDocsConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(RestDocsConfiguration.class)
@ExtendWith(RestDocumentationExtension.class)
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SearchControllerTest extends BaseControllerTest{

    // 검색
    @Test
    @DisplayName("검색 성공")
    void searchPosts() throws Exception {
        //given
        String text = "제목";
        Long lastId = 0L;
        int limit = 10;

        //when
        MockHttpServletRequestBuilder builder = get("/sns/search/{text}", text)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .param("lastId", String.valueOf(lastId))
                .param("limit", String.valueOf(limit));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body",hasSize(8)))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("search-posts",
                        links(
                                linkWithRel("self").description("link to self")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        responseFields(
                                fieldWithPath("code").description("label code number"),
                                fieldWithPath("message").description("message"),
                                fieldWithPath("body").description("body of the response"),
                                fieldWithPath("body[].id").description("id"),
                                fieldWithPath("body[].title").description("title"),
                                fieldWithPath("body[].content").description("content"),
                                fieldWithPath("body[].createdAt").description("createdAt"),
                                fieldWithPath("body[].updatedAt").description("updatedAt"),
                                fieldWithPath("body[].user").description("user"),
                                fieldWithPath("body[].user.id").description("user id"),
                                fieldWithPath("body[].user.nickname").description("user nickname"),
                                fieldWithPath("body[].user.image").description("user image"),
                                fieldWithPath("body[].images").description("images").optional(),
                                fieldWithPath("body[].images[].src").description("image path").optional(),
                                fieldWithPath("body[].likers").description("likers").optional(),
                                fieldWithPath("body[].likers[].id").description("id of likers").optional(),
                                fieldWithPath("body[].likers[].nickname").description("nickname of likers").optional(),
                                fieldWithPath("body[].likers[].image").description("profile image of likers").optional(),
                                fieldWithPath("body[].likers[].image.src").type(JsonFieldType.STRING).description("profile image path of likers").optional(),
                                fieldWithPath("body[].commentNum").description("commentNum"),
                                fieldWithPath("body[].retweet").type(JsonFieldType.OBJECT).description("retweet").optional(),
                                fieldWithPath("body[].category").description("category"),
                                fieldWithPath("body[].hits").description("views"),
                                fieldWithPath("body[].blocked").description("blocking flag of the post"),
                                fieldWithPath("_links.self.href").description("link to self")

                        )
                ));

    }

}
