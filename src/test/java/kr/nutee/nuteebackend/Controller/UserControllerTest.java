package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.Common.RestDocsConfiguration;
import kr.nutee.nuteebackend.DTO.Request.*;
import kr.nutee.nuteebackend.Domain.Interest;
import kr.nutee.nuteebackend.Domain.Major;
import kr.nutee.nuteebackend.Domain.Member;
import kr.nutee.nuteebackend.Domain.Post;
import kr.nutee.nuteebackend.Enum.Category;
import kr.nutee.nuteebackend.Repository.MemberRepository;
import kr.nutee.nuteebackend.Service.ImageService;
import kr.nutee.nuteebackend.Service.MemberService;
import kr.nutee.nuteebackend.Service.PostService;
import kr.nutee.nuteebackend.Service.Util;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(RestDocsConfiguration.class)
@ExtendWith(RestDocumentationExtension.class)
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest extends BaseControllerTest {

    @BeforeAll
    void setPostList(){
        //given
        //멤버 3명 디비에 생성
        Member member1 = Member.builder()
                .id(1L)
                .userId("mf0001")
                .nickname("moon1")
                .schoolEmail("mf0001@gmail.com")
                .isDeleted(false)
                .isBlocked(false)
                .password(passwordEncoder.encode("P@ssw0rd"))
                .build();

        Member member2 = Member.builder()
                .id(2L)
                .userId("mf0002")
                .nickname("moon2")
                .schoolEmail("mf0002@gmail.com")
                .isDeleted(false)
                .isBlocked(false)
                .password(passwordEncoder.encode("P@ssw0rd"))
                .build();

        Member member3 = Member.builder()
                .id(3L)
                .userId("mf0003")
                .nickname("moon3")
                .schoolEmail("mf0003@gmail.com")
                .isDeleted(false)
                .isBlocked(false)
                .password(passwordEncoder.encode("P@ssw0rd"))
                .build();

        memberService.insertUser(member1);
        memberService.insertUser(member2);
        memberService.insertUser(member3);

        //흥미,전공 등록
        List<String> interests1 = new ArrayList<>();
        interests1.add("INTER1");
        interests1.add("INTER2");
        interests1.add("INTER3");
        interests1.add("INTER4");
        interests1.forEach(v -> interestRepository.save(
                Interest.builder()
                        .interest(v)
                        .member(member1)
                        .build()
        ));

        List<String> interests2 = new ArrayList<>();
        interests2.add("INTER1");
        interests2.add("INTER3");
        interests2.forEach(v -> interestRepository.save(
                Interest.builder()
                        .interest(v)
                        .member(member2)
                        .build()
        ));

        List<String> interests3 = new ArrayList<>();
        interests3.add("INTER3");
        interests3.add("INTER4");
        interests3.add("INTER5");
        interests3.forEach(v -> interestRepository.save(
                Interest.builder()
                        .interest(v)
                        .member(member3)
                        .build()
        ));

        List<String> majors1 = new ArrayList<>();
        majors1.add("MAJOR1");
        majors1.add("MAJOR2");
        majors1.forEach(v -> majorRepository.save(
                Major.builder()
                        .major(v)
                        .member(member1)
                        .build()
        ));

        List<String> majors2 = new ArrayList<>();
        majors2.add("MAJOR2");
        majors2.add("MAJOR3");
        majors2.forEach(v -> majorRepository.save(
                Major.builder()
                        .major(v)
                        .member(member2)
                        .build()
        ));

        List<String> majors3 = new ArrayList<>();
        majors3.add("MAJOR1");
        majors3.add("MAJOR3");
        majors3.forEach(v -> majorRepository.save(
                Major.builder()
                        .major(v)
                        .member(member3)
                        .build()
        ));

        List<ImageRequest> list1 = new ArrayList<>();
        list1.add(ImageRequest.builder().src("image1Path(1).jpg").build());
        list1.add(ImageRequest.builder().src("image1Path(2).jpg").build());
        list1.add(ImageRequest.builder().src("image1Path(3).jpg").build());

        List<ImageRequest> list2 = new ArrayList<>();
        list2.add(ImageRequest.builder().src("image2Path(1).jpg").build());
        list2.add(ImageRequest.builder().src("image2Path(2).jpg").build());

        postService.createPost(member1.getId(),
                CreatePostRequest.builder()
                        .title("제목1")
                        .content("내용1")
                        .category(Category.INTER1.getCategory())
                        .images(list1)
                        .build()
        );

        postService.createPost(member1.getId(),
                CreatePostRequest.builder()
                        .title("제목2")
                        .content("내용2")
                        .category(Category.INTER2.getCategory())
                        .images(list2)
                        .build()
        );

        postService.createPost(member1.getId(),
                CreatePostRequest.builder()
                        .title("제목3")
                        .content("내용3")
                        .category(Category.INTER3.getCategory())
                        .images(null)
                        .build()
        );

        postService.createPost(member1.getId(),
                CreatePostRequest.builder()
                        .title("제목4")
                        .content("내용4")
                        .category(Category.INTER4.getCategory())
                        .images(null)
                        .build()
        );

        postService.createPost(member2.getId(),
                CreatePostRequest.builder()
                        .title("제목5")
                        .content("내용5")
                        .category(Category.INTER2.getCategory())
                        .images(null)
                        .build()
        );

        postService.createPost(member3.getId(),
                CreatePostRequest.builder()
                        .title("제목6")
                        .content("내용6")
                        .category(Category.INTER5.getCategory())
                        .images(null)
                        .build()
        );

        postService.createPost(member2.getId(),
                CreatePostRequest.builder()
                        .title("제목7")
                        .content("내용7")
                        .category(Category.INTER3.getCategory())
                        .images(null)
                        .build()
        );

        postService.createPost(member1.getId(),
                CreatePostRequest.builder()
                        .title("제목8")
                        .content("내용8")
                        .category(Category.INTER2.getCategory())
                        .images(null)
                        .build()
        );

        //4번 글 삭제
        postService.deletePost(4L,1L);

        //8번 글 블락
        Post post8 = postRepository.findPostById(8L);
        post8.setBlocked(true);
        postRepository.save(post8);

        //given
        Long memberId = 1L;
        Long postId1 = 1L;
        Long postId2 = 2L;
        Long parentId1 = 1L;

        //1번 글 댓글 12개 달기
        postService.createComment(memberId,postId1,"댓글 1입니다.");
        postService.createComment(memberId,postId1,"댓글 2입니다.");
        postService.createComment(memberId,postId1,"댓글 3입니다.");
        postService.createComment(memberId,postId1,"댓글 4입니다.");
        postService.createComment(memberId,postId1,"댓글 5입니다.");
        postService.createComment(memberId,postId1,"댓글 6입니다.");
        postService.createComment(memberId,postId1,"댓글 7입니다.");
        postService.createComment(memberId,postId1,"댓글 8입니다.");
        postService.createComment(memberId,postId1,"댓글 9입니다.");
        postService.createComment(memberId,postId1,"댓글 10입니다.");
        postService.createComment(memberId,postId1,"댓글 11입니다.");
        postService.createComment(memberId,postId1,"댓글 12입니다.");

        //2번 글 댓글 3개 달기
        postService.createComment(memberId,postId2,"댓글 1입니다.");
        postService.createComment(memberId,postId2,"댓글 2입니다.");
        postService.createComment(memberId,postId2,"댓글 3입니다.");

        //1번 글 댓글 2개 달기
        postService.createComment(memberId,postId1,"댓글 13입니다.");
        postService.createComment(memberId,postId1,"댓글 14입니다.");

        //1번 글 대댓글 2개 달기
        postService.createReComment(memberId,parentId1,postId1,"댓글1의 답글 1입니다.");
        postService.createReComment(memberId,parentId1,postId1,"댓글1의 답글 2입니다.");

        //1번 글 좋아요 누르기
        postService.likePost(2L,1L);

        //1번 글 리트윗 실행
        RetweetRequest body = RetweetRequest.builder()
                .category("INTER1")
                .content("1번 글을 리트윗합니다.")
                .title("1번글을 리트윗합니다.")
                .build();

        postService.createRetweet(1L,1L,body);
    }

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

