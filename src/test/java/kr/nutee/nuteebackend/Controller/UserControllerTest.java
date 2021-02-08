package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.DTO.Request.*;
import org.junit.jupiter.api.*;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

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
                .andDo(document("get-user"));

    }

    // 유저 한 명 포스트 조회
    @Test
    @Order(2)
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
                .andDo(document("get-user-posts"));

    }

//    // 닉네임 변경
//    @Test @Order(3)
//    @Deprecated
//    @DisplayName("닉네임 변경 성공")
//    void updateNickname() throws Exception {
//
//        //given
//        NicknameUpdateRequest body = NicknameUpdateRequest.builder()
//                .nickname("새 닉네임")
//                .build();
//
//        //when
//        MockHttpServletRequestBuilder builder = patch("/sns/user/nickname")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "Bearer " + token)
//                .accept(MediaTypes.HAL_JSON_VALUE)
//                .content(objectMapper.writeValueAsString(body));
//
//        //then
//        mockMvc.perform(builder)
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
//                .andExpect(jsonPath("code").exists())
//                .andExpect(jsonPath("message").exists())
//                .andExpect(jsonPath("body").exists())
//                .andExpect(jsonPath("body.id").exists())
//                .andExpect(jsonPath("body.nickname").exists())
//                .andExpect(jsonPath("body.image").isEmpty())
//                .andExpect(jsonPath("body.interests").exists())
//                .andExpect(jsonPath("body.majors").exists())
//                .andExpect(jsonPath("body.postNum").exists())
//                .andExpect(jsonPath("body.commentNum").exists())
//                .andExpect(jsonPath("body.likeNum").exists())
//                .andExpect(jsonPath("_links.self").exists())
//                .andDo(document("patch-nickname"));
//    }
//
//    // 패스워드 변경
//    @Test @Order(4)
//    @Deprecated
//    @DisplayName("패스워드 변경 성공")
//    void passwordChange() throws Exception {
//
//        //given
//        PasswordUpdateRequest body = PasswordUpdateRequest.builder()
//                .password("새 비밀번호")
//                .build();
//
//        //when
//        MockHttpServletRequestBuilder builder = post("/sns/user/pwchange")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "Bearer " + token)
//                .accept(MediaTypes.HAL_JSON_VALUE)
//                .content(objectMapper.writeValueAsString(body));
//
//        //then
//        mockMvc.perform(builder)
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
//                .andExpect(jsonPath("code").exists())
//                .andExpect(jsonPath("message").exists())
//                .andExpect(jsonPath("body").exists())
//                .andExpect(jsonPath("_links.self").exists())
//                .andDo(document("change-password"));
//
//    }
//
//    // 프로필 이미지 등록
//    @Test @Order(5)
//    @Deprecated
//    @DisplayName("프로필 이미지 등록 성공")
//    void uploadProfileImage() throws Exception {
//
//        //given
//        ProfileRequest body = ProfileRequest.builder()
//                .src("123.jpg")
//                .build();
//
//        //when
//        MockHttpServletRequestBuilder builder = post("/sns/user/profile")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "Bearer " + token)
//                .accept(MediaTypes.HAL_JSON_VALUE)
//                .content(objectMapper.writeValueAsString(body));
//
//        //then
//        mockMvc.perform(builder)
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
//                .andExpect(jsonPath("code").exists())
//                .andExpect(jsonPath("message").exists())
//                .andExpect(jsonPath("body").exists())
//                .andExpect(jsonPath("_links.self").exists())
//                .andDo(document("update-profile"));
//    }
//
//    // 프로필 이미지 삭제
//    @Test @Order(6)
//    @Deprecated
//    @DisplayName("프로필 이미지 삭제 성공")
//    void deleteProfileImage() throws Exception {
//
//        //when
//        MockHttpServletRequestBuilder builder = delete("/sns/user/profile")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "Bearer " + token)
//                .accept(MediaTypes.HAL_JSON_VALUE);
//
//        //then
//        mockMvc.perform(builder)
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
//                .andExpect(jsonPath("code").exists())
//                .andExpect(jsonPath("message").exists())
//                .andExpect(jsonPath("body").exists())
//                .andExpect(jsonPath("_links.self").exists())
//                .andDo(document("delete-profile"));
//    }
//
//    // 관심 분야 변경
//    @Test @Order(7)
//    @Deprecated
//    @DisplayName("관심 분야 변경 성공")
//    void updateInterests() throws Exception {
//
//        //given
//        List<String> interests = new ArrayList<>();
//        interests.add("it");
//        interests.add("media");
//
//        InterestsUpdateRequest body = InterestsUpdateRequest.builder()
//                .interests(interests)
//                .build();
//
//        //when
//        MockHttpServletRequestBuilder builder = patch("/sns/user/interests")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "Bearer " + token)
//                .accept(MediaTypes.HAL_JSON_VALUE)
//                .content(objectMapper.writeValueAsString(body));
//
//        //then
//        mockMvc.perform(builder)
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
//                .andExpect(jsonPath("code").exists())
//                .andExpect(jsonPath("message").exists())
//                .andExpect(jsonPath("body", hasSize(2)))
//                .andExpect(jsonPath("_links.self").exists())
//                .andDo(document("update-interests"));
//    }
//
//    // 전공 변경
//    @Test @Order(8)
//    @Deprecated
//    @DisplayName("전공 변경 성공")
//    void updateMajors() throws Exception {
//
//        //given
//        List<String> majorList = new ArrayList<>();
//        majorList.add("it");
//        majorList.add("english");
//
//        MajorsUpdateRequest body = MajorsUpdateRequest.builder()
//                .majors(majorList)
//                .build();
//
//        //when
//        MockHttpServletRequestBuilder builder = patch("/sns/user/majors")
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "Bearer " + token)
//                .accept(MediaTypes.HAL_JSON_VALUE)
//                .content(objectMapper.writeValueAsString(body));
//
//        //then
//        mockMvc.perform(builder)
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
//                .andExpect(jsonPath("code").exists())
//                .andExpect(jsonPath("message").exists())
//                .andExpect(jsonPath("body", hasSize(2)))
//                .andExpect(jsonPath("_links.self").exists())
//                .andDo(document("update-majors"));
//
//    }

    // 본인 조회
    @Test
    @Order(9)
    @DisplayName("유저 조회 성공")
    void getMe() throws Exception {
        //given

        //when
        MockHttpServletRequestBuilder builder = get("/sns/user/me")
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
            .andDo(document("get-me"));
    }
}

