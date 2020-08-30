package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.Common.RestDocsConfiguration;
import kr.nutee.nuteebackend.DTO.Request.*;
import kr.nutee.nuteebackend.Domain.Interest;
import kr.nutee.nuteebackend.Domain.Major;
import kr.nutee.nuteebackend.Domain.Member;
import kr.nutee.nuteebackend.Domain.Post;
import kr.nutee.nuteebackend.Enum.Category;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class UserControllerTest extends BaseControllerTest {

    // 유저 한 명 조회
    @Test
    @Order(1)
    @DisplayName("유저 조회 성공")
    void getUser() throws Exception {
        //given
        Long userId = 1L;

        //when
        MockHttpServletRequestBuilder builder = get("/sns/user/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .accept(MediaTypes.HAL_JSON_VALUE);

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").exists())
                .andExpect(jsonPath("body.id").exists())
                .andExpect(jsonPath("body.nickname").exists())
                .andExpect(jsonPath("body.image").isEmpty())
                .andExpect(jsonPath("body.interests").exists())
                .andExpect(jsonPath("body.majors").exists())
                .andExpect(jsonPath("body.postNum").exists())
                .andExpect(jsonPath("body.commentNum").exists())
                .andExpect(jsonPath("body.likeNum").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("get-user",
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
                                fieldWithPath("body.id").description("id of user"),
                                fieldWithPath("body.nickname").description("nickname of user"),
                                fieldWithPath("body.image").description("profile image of user"),
                                fieldWithPath("body.interests").description("interests of user"),
                                fieldWithPath("body.majors").description("majors of user"),
                                fieldWithPath("body.postNum").description("posts of user"),
                                fieldWithPath("body.commentNum").description("comments of user"),
                                fieldWithPath("body.likeNum").description("favorite posts of user"),
                                fieldWithPath("_links.self.href").description("link to self")

                        )
                ));

    }

    // 유저 한 명 포스트 조회
    @Test @Order(2)
    @DisplayName("유저 포스트 조회 성공")
    void getUserPosts() throws Exception {
        //given
        Long userId = 1L;
        Long lastId = 0L;
        int limit = 10;

        //when
        MockHttpServletRequestBuilder builder = get("/sns/user/{userId}/posts", userId)
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
                .andExpect(jsonPath("body",hasSize(5)))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("get-user-posts",
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
                                fieldWithPath("body[].likers[].id").description("likers").optional(),
                                fieldWithPath("body[].likers[].nickname").description("likers").optional(),
                                fieldWithPath("body[].likers[].image").description("likers").optional(),
                                fieldWithPath("body[].likers[].image.src").type(JsonFieldType.STRING).description("likers").optional(),
                                fieldWithPath("body[].commentNum").description("commentNum"),
                                fieldWithPath("body[].retweet").type(JsonFieldType.OBJECT).description("retweet").optional(),
                                fieldWithPath("body[].retweet.id").description("retweet"),
                                fieldWithPath("body[].retweet.title").description("retweet"),
                                fieldWithPath("body[].retweet.content").description("retweet"),
                                fieldWithPath("body[].retweet.createdAt").description("retweet"),
                                fieldWithPath("body[].retweet.updatedAt").description("retweet"),
                                fieldWithPath("body[].retweet.user").description("retweet"),
                                fieldWithPath("body[].retweet.user.id").description("retweet"),
                                fieldWithPath("body[].retweet.user.nickname").description("retweet"),
                                fieldWithPath("body[].retweet.user.image").description("retweet"),
                                fieldWithPath("body[].retweet.images").description("retweet"),
                                fieldWithPath("body[].retweet.images[].src").description("retweet"),
                                fieldWithPath("body[].retweet.likers").description("retweet"),
                                fieldWithPath("body[].retweet.commentNum").description("retweet"),
                                fieldWithPath("body[].retweet.category").description("retweet"),
                                fieldWithPath("body[].retweet.hits").description("retweet"),
                                fieldWithPath("body[].retweet.blocked").description("retweet"),
                                fieldWithPath("body[].retweet.deleted").description("retweet"),
                                fieldWithPath("body[].category").description("category"),
                                fieldWithPath("body[].hits").description("hits"),
                                fieldWithPath("body[].blocked").description("isBlocked"),
                                fieldWithPath("_links.self.href").description("link to self")

                        )
                ));

    }

    // 닉네임 변경
    @Test @Order(3)
    @DisplayName("닉네임 변경 성공")
    void updateNickname() throws Exception {

        //given
        NicknameUpdateRequest body = NicknameUpdateRequest.builder()
                .nickname("새 닉네임")
                .build();

        //when
        MockHttpServletRequestBuilder builder = patch("/sns/user/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").exists())
                .andExpect(jsonPath("body.id").exists())
                .andExpect(jsonPath("body.nickname").exists())
                .andExpect(jsonPath("body.image").isEmpty())
                .andExpect(jsonPath("body.interests").exists())
                .andExpect(jsonPath("body.majors").exists())
                .andExpect(jsonPath("body.postNum").exists())
                .andExpect(jsonPath("body.commentNum").exists())
                .andExpect(jsonPath("body.likeNum").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("patch-nickname",
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
                                fieldWithPath("body.id").description("id of user"),
                                fieldWithPath("body.nickname").description("nickname of user"),
                                fieldWithPath("body.image").description("profile image of user"),
                                fieldWithPath("body.interests").description("interests of user"),
                                fieldWithPath("body.majors").description("majors of user"),
                                fieldWithPath("body.postNum").description("posts of user"),
                                fieldWithPath("body.commentNum").description("comments of user"),
                                fieldWithPath("body.likeNum").description("favorite posts of user"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));
    }

    // 패스워드 변경
    @Test @Order(4)
    @DisplayName("패스워드 변경 성공")
    void passwordChange() throws Exception {

        //given
        PasswordUpdateRequest body = PasswordUpdateRequest.builder()
                .password("새 비밀번호")
                .build();

        //when
        MockHttpServletRequestBuilder builder = post("/sns/user/pwchange")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("change-password",
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
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));

    }

    // 프로필 이미지 등록
    @Test @Order(5)
    @DisplayName("프로필 이미지 등록 성공")
    void uploadProfileImage() throws Exception {

        //given
        ProfileRequest body = ProfileRequest.builder()
                .src("123.jpg")
                .build();

        //when
        MockHttpServletRequestBuilder builder = post("/sns/user/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-profile",
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
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));
    }

    // 프로필 이미지 삭제
    @Test @Order(6)
    @DisplayName("프로필 이미지 삭제 성공")
    void deleteProfileImage() throws Exception {

        //when
        MockHttpServletRequestBuilder builder = delete("/sns/user/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .accept(MediaTypes.HAL_JSON_VALUE);

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("delete-profile",
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
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));
    }

    // 관심 분야 변경
    @Test @Order(7)
    @DisplayName("관심 분야 변경 성공")
    void updateInterests() throws Exception {

        //given
        List<String> interests = new ArrayList<>();
        interests.add("it");
        interests.add("media");

        InterestsUpdateRequest body = InterestsUpdateRequest.builder()
                .interests(interests)
                .build();

        //when
        MockHttpServletRequestBuilder builder = patch("/sns/user/interests")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body", hasSize(2)))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-interests",
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
                                fieldWithPath("body[].id").description("id of interest"),
                                fieldWithPath("body[].interest").description("title of interest"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));
    }

    // 전공 변경
    @Test @Order(8)
    @DisplayName("전공 변경 성공")
    void updateMajors() throws Exception {

        //given
        List<String> majorList = new ArrayList<>();
        majorList.add("it");
        majorList.add("english");

        MajorsUpdateRequest body = MajorsUpdateRequest.builder()
                .majors(majorList)
                .build();

        //when
        MockHttpServletRequestBuilder builder = patch("/sns/user/majors")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body", hasSize(2)))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-majors",
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
                                fieldWithPath("body[].id").description("id of major"),
                                fieldWithPath("body[].major").description("title of major"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));

    }
}

