package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.Common.RestDocsConfiguration;
import kr.nutee.nuteebackend.DTO.Request.CreatePostRequest;
import kr.nutee.nuteebackend.DTO.Request.ReportRequest;
import kr.nutee.nuteebackend.DTO.Request.UpdatePostRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Import(RestDocsConfiguration.class)
@ExtendWith(RestDocumentationExtension.class)
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ControllerFailureTest extends BaseControllerTest{

    @Test
    @Order(1)
    @DisplayName("포스트 생성 입력값 오류")
    void createPost_Bad_Request_Wrong_Input() throws Exception {

        //given
        CreatePostRequest body = CreatePostRequest.builder().build();

        //when
        MockHttpServletRequestBuilder builder = post("/sns/post")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(body));


        //then
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest());
    }

    @Test @Order(2)
    @DisplayName("포스트 수정 입력값 오류")
    void updatePost_Bad_Request_Wrong_Input() throws Exception {

        //given
        UpdatePostRequest body = UpdatePostRequest.builder()
                .title("")
                .content("내용 수정")
                .build();

        //when
        MockHttpServletRequestBuilder builder = patch("/sns/post/10")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(body));


        //then
        mockMvc.perform(builder)
                .andExpect(status().isBadRequest());
    }

    @Test @Order(3)
    @DisplayName("포스트 수정 권한 없는 사람 접근")
    void updatePost_Not_Allowed() throws Exception {
        //given
        UpdatePostRequest body = UpdatePostRequest.builder()
                .title("제목 수정")
                .content("내용 수정")
                .build();
        Long postId = 5L;

        //when
        MockHttpServletRequestBuilder builder = patch("/sns/post/{postId}",postId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(body));


        //then
        mockMvc.perform(builder)
                .andExpect(status().isForbidden());

    }

    @Test @Order(4)
    @DisplayName("포스트 읽기 실패(이미 삭제됨)")
    void getPost_Not_Exist() throws Exception {
        //given
        Long postId = 4L;

        //when
        MockHttpServletRequestBuilder builder = get("/sns/post/{postId}",postId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .accept(MediaTypes.HAL_JSON_VALUE);

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andExpect(jsonPath("_links.get-category-posts").exists())
                .andDo(document("get-post",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("get-favorite-posts").description("link to update the post"),
                                linkWithRel("get-category-posts").description("link to remove the post")
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
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.get-favorite-posts.href").description("link to get favorite list"),
                                fieldWithPath("_links.get-category-posts.href").description("link to get category list")
                        )
                ));
    }

    @Test @Order(5)
    @DisplayName("포스트 읽기 실패(제한됨)")
    void getPost_Blocked() throws Exception {
        //given
        Long postId = 8L;

        //when
        MockHttpServletRequestBuilder builder = get("/sns/post/{postId}",postId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .accept(MediaTypes.HAL_JSON_VALUE);

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-post").exists())
                .andExpect(jsonPath("_links.remove-post").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andExpect(jsonPath("_links.get-category-posts").exists())
                .andDo(document("get-post-block",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("update-post").description("link to update the post"),
                                linkWithRel("remove-post").description("link to remove the post"),
                                linkWithRel("get-favorite-posts").description("link to favorite posts"),
                                linkWithRel("get-category-posts").description("link to category posts")
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
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.update-post.href").description("link to update post"),
                                fieldWithPath("_links.remove-post.href").description("link to remove post"),
                                fieldWithPath("_links.get-favorite-posts.href").description("link to favorite posts"),
                                fieldWithPath("_links.get-category-posts.href").description("link to category posts")
                        )
                ));
    }

    @Test @Order(6)
    @DisplayName("게시글 신고 실패 (이미 신고 누른 게시물)")
    void reportPost_alreadyReport() throws Exception {
        //given
        Long postId = 1L;
        ReportRequest body = ReportRequest.builder()
                .content("역겨운 게시물이라 신고했습니다.")
                .build();

        //when
        MockHttpServletRequestBuilder builder = post("/sns/post/{postId}/report",postId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").exists())
                .andExpect(jsonPath("body.id").exists())
                .andExpect(jsonPath("body.title").exists())
                .andExpect(jsonPath("body.content").exists())
                .andExpect(jsonPath("body.category").exists())
                .andExpect(jsonPath("body.user").exists())
                .andExpect(jsonPath("body.hits").exists())
                .andExpect(jsonPath("body.blocked").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-post").exists())
                .andExpect(jsonPath("_links.remove-post").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andExpect(jsonPath("_links.get-category-posts").exists())
                .andDo(document("report-post",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("update-post").description("link to update the post"),
                                linkWithRel("remove-post").description("link to remove the post"),
                                linkWithRel("get-favorite-posts").description("link to update the post"),
                                linkWithRel("get-category-posts").description("link to remove the post")
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
                                fieldWithPath("body.id").description("id of the post"),
                                fieldWithPath("body.title").description("title of the post"),
                                fieldWithPath("body.content").description("description of the post"),
                                fieldWithPath("body.createdAt").description("created time of the post"),
                                fieldWithPath("body.updatedAt").description("updated time of the post"),
                                fieldWithPath("body.blocked").description("blocking flag of the post"),
                                fieldWithPath("body.user").description("author of the post"),
                                fieldWithPath("body.user.id").description("author's id number"),
                                fieldWithPath("body.user.nickname").description("author's nickname"),
                                fieldWithPath("body.user.image").description("author's profile image path"),
                                fieldWithPath("body.images[].src").description("image paths").optional(),
                                fieldWithPath("body.likers").description("user who likes the post").optional(),
                                fieldWithPath("body.comments").description("comments of the post").optional(),
                                fieldWithPath("body.comments[].id").description("comment's id"),
                                fieldWithPath("body.comments[].content").description("comment's content"),
                                fieldWithPath("body.comments[].createdAt").description("comment's created time"),
                                fieldWithPath("body.comments[].updatedAt").description("comment's updated time"),
                                fieldWithPath("body.comments[].reComment").description("comment's reComment").optional(),
                                fieldWithPath("body.comments[].reComment[].id").type(JsonFieldType.NUMBER).description("comment's reComment's id").optional(),
                                fieldWithPath("body.comments[].reComment[].content").type(JsonFieldType.STRING).description("comment's reComment's content").optional(),
                                fieldWithPath("body.comments[].reComment[].createdAt").type(JsonFieldType.STRING).description("comment's reComment's created time").optional(),
                                fieldWithPath("body.comments[].reComment[].updatedAt").type(JsonFieldType.STRING).description("comment's reComment's updated time").optional(),
                                fieldWithPath("body.comments[].reComment[].user").type(JsonFieldType.OBJECT).description("comment's reComment's writer").optional(),
                                fieldWithPath("body.comments[].reComment[].user.id").type(JsonFieldType.NUMBER).description("comment's reComment's writer's id").optional(),
                                fieldWithPath("body.comments[].reComment[].user.nickname").type(JsonFieldType.STRING).description("comment's reComment's writer's nickname").optional(),
                                fieldWithPath("body.comments[].reComment[].user.image").type(JsonFieldType.OBJECT).description("comment's reComment's writer's image").optional(),
                                fieldWithPath("body.comments[].reComment[].user.image.src").type(JsonFieldType.STRING).description("comment's reComment's writer's image's path").optional(),
                                fieldWithPath("body.comments[].user").description("comment's writer"),
                                fieldWithPath("body.comments[].user.id").description("comment's writer's id"),
                                fieldWithPath("body.comments[].user.nickname").description("comment's writer's nickname"),
                                fieldWithPath("body.comments[].user.image").description("comment's writer's image"),
                                fieldWithPath("body.comments[].user.image.src").type(JsonFieldType.STRING).description("comment's writer's image's path").optional(),
                                fieldWithPath("body.retweet").description("post that sharing other post").optional(),
                                fieldWithPath("body.category").description("category of the post"),
                                fieldWithPath("body.hits").description(" user's join number of the post"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.update-post.href").description("link to update post"),
                                fieldWithPath("_links.remove-post.href").description("link to remove post"),
                                fieldWithPath("_links.get-favorite-posts.href").description("link to get favorite list"),
                                fieldWithPath("_links.get-category-posts.href").description("link to get category list")
                        )
                ));


    }

    @Test @Order(7)
    @DisplayName("게시글 신고 실패 (존재하지 않는 게시물)")
    void reportPost_isNull() throws Exception {
        //given

        //when

        //then

    }

}
