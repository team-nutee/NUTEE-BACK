package kr.nutee.nuteebackend.Controller;


import kr.nutee.nuteebackend.Controller.Common.RestDocsConfiguration;
import kr.nutee.nuteebackend.DTO.LoginToken;
import kr.nutee.nuteebackend.DTO.Request.CreatePostRequest;
import kr.nutee.nuteebackend.DTO.Request.ImageRequest;
import kr.nutee.nuteebackend.DTO.Request.SignupDTO;
import kr.nutee.nuteebackend.DTO.Request.UpdatePostRequest;
import kr.nutee.nuteebackend.DTO.Response.PostResponse;
import kr.nutee.nuteebackend.DTO.Response.User;
import kr.nutee.nuteebackend.Domain.Member;
import kr.nutee.nuteebackend.Domain.Post;
import kr.nutee.nuteebackend.Enum.Category;
import kr.nutee.nuteebackend.Repository.MemberRepository;
import kr.nutee.nuteebackend.Repository.PostRepository;
import kr.nutee.nuteebackend.Service.PostService;
import kr.nutee.nuteebackend.Service.Util;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.in;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(RestDocsConfiguration.class)
@ExtendWith(RestDocumentationExtension.class)
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostControllerTest extends BaseControllerTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostService postService;

    @Autowired
    Util util;

    @Test @Order(1)
    @DisplayName("포스트 생성 이미지 X 성공")
    void createPost() throws Exception {

        //given
        CreatePostRequest body = CreatePostRequest.builder()
                .title("제목 테스트")
                .content("내용 테스트")
                .category(Category.INTER1.getCategory())
                .build();

        Long memberId = 1L;
        Member member = memberRepository.findMemberById(memberId);
        User user = User.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .image(util.transferImage(member.getImage()))
                .build();

        //when
        MockHttpServletRequestBuilder builder = post("/sns/post")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").exists())
                .andExpect(jsonPath("body.id").exists())
                .andExpect(jsonPath("body.title").value("제목 테스트"))
                .andExpect(jsonPath("body.content").value("내용 테스트"))
                .andExpect(jsonPath("body.category").value(Category.INTER1.getCategory()))
                .andExpect(jsonPath("body.user").value(user))
                .andExpect(jsonPath("body.images").isEmpty())
                .andExpect(jsonPath("body.likers").isEmpty())
                .andExpect(jsonPath("body.comments").isEmpty())
                .andExpect(jsonPath("body.retweet").isEmpty())
                .andExpect(jsonPath("body.hits").value(0))
                .andExpect(jsonPath("body.blocked").value(false))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-post").exists())
                .andExpect(jsonPath("_links.remove-post").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andExpect(jsonPath("_links.get-category-posts").exists());
    }

    @Test @Order(2)
    @DisplayName("포스트 생성 이미지 O 성공")
    void createPostImage() throws Exception {

        //given
        List<ImageRequest> images = new ArrayList<>();
        ImageRequest pathOfImage = new ImageRequest("pathOfImage.jpg");
        ImageRequest pathOfImage2 = new ImageRequest("pathOfImage2.jpg");
        images.add(pathOfImage);
        images.add(pathOfImage2);

        CreatePostRequest body = CreatePostRequest.builder()
                .title("제목 테스트2")
                .content("내용 테스트2")
                .images(images)
                .category("IT2")
                .build();

        Long memberId = 1L;
        Member member = memberRepository.findMemberById(memberId);
        User user = User.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .image(util.transferImage(member.getImage()))
                .build();

        //when
        MockHttpServletRequestBuilder builder = post("/sns/post")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").exists())
                .andExpect(jsonPath("body.id").exists())
                .andExpect(jsonPath("body.title").value("제목 테스트2"))
                .andExpect(jsonPath("body.content").value("내용 테스트2"))
                .andExpect(jsonPath("body.category").value("IT2"))
                .andExpect(jsonPath("body.user").value(user))
                .andExpect(jsonPath("body.images").exists())
                .andExpect(jsonPath("body.images[0].src").value("pathOfImage.jpg"))
                .andExpect(jsonPath("body.images[1].src").value("pathOfImage2.jpg"))
                .andExpect(jsonPath("body.likers").isEmpty())
                .andExpect(jsonPath("body.comments").isEmpty())
                .andExpect(jsonPath("body.retweet").isEmpty())
                .andExpect(jsonPath("body.hits").value(0))
                .andExpect(jsonPath("body.blocked").value(false))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-post").exists())
                .andExpect(jsonPath("_links.remove-post").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andExpect(jsonPath("_links.get-category-posts").exists())
                .andDo(document("create-post",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("update-post").description("link to update the post"),
                                linkWithRel("remove-post").description("link to remove the post"),
                                linkWithRel("get-favorite-posts").description("link to get favorite list"),
                                linkWithRel("get-category-posts").description("link to get category list")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        requestFields(
                                fieldWithPath("title").description("title of new post(not null)"),
                                fieldWithPath("content").description("description of new post(not null)"),
                                fieldWithPath("images[].src").description("images of new post"),
                                fieldWithPath("category").description("category of new post(not null)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
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
                                fieldWithPath("body.images[].src").description("image paths"),
                                fieldWithPath("body.likers").description("user who likes the post"),
                                fieldWithPath("body.comments").description("comments of the post"),
                                fieldWithPath("body.retweet").description("post that sharing other post"),
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

    @Test @Order(3)
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

    @Test @Order(4)
    @DisplayName("포스트 수정 성공")
    void updatePost() throws Exception {
        //given
        List<ImageRequest> images = new ArrayList<>();
        ImageRequest pathOfImage = new ImageRequest("pathOfImageUpdate.jpg");
        images.add(pathOfImage);
        Long postId = 1L;

        UpdatePostRequest body = UpdatePostRequest.builder()
                .title("제목 수정")
                .content("내용 수정")
                .images(images)
                .build();

        PostResponse post = util.transferPost(postRepository.findPostById(postId));
        User user = post.getUser();

        //when
        MockHttpServletRequestBuilder builder = patch("/sns/post/{postId}",postId)
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
                .andExpect(jsonPath("body.id").value(post.getId()))
                .andExpect(jsonPath("body.title").value("제목 수정"))
                .andExpect(jsonPath("body.content").value("내용 수정"))
                .andExpect(jsonPath("body.category").value(post.getCategory()))
                .andExpect(jsonPath("body.user").value(user))
                .andExpect(jsonPath("body.images").exists())
                .andExpect(jsonPath("body.images[0].src").value(body.getImages().get(0).getSrc()))
                .andExpect(jsonPath("body.likers").value(post.getLikers()))
                .andExpect(jsonPath("body.comments").value(post.getComments()))
                .andExpect(jsonPath("body.retweet").value(post.getRetweet()))
                .andExpect(jsonPath("body.hits").value(post.getHits()))
                .andExpect(jsonPath("body.blocked").value(post.isBlocked()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-post").exists())
                .andExpect(jsonPath("_links.remove-post").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andExpect(jsonPath("_links.get-category-posts").exists())
                .andDo(document("update-post",
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
                        requestFields(
                                fieldWithPath("title").description("title of new post(not null)"),
                                fieldWithPath("content").description("description of new post(not null)"),
                                fieldWithPath("images[].src").description("images of new post")
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
                                fieldWithPath("body.images[].src").description("image paths"),
                                fieldWithPath("body.likers").description("user who likes the post"),
                                fieldWithPath("body.comments").description("comments of the post"),
                                fieldWithPath("body.retweet").description("post that sharing other post"),
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

    @Test @Order(5)
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

    @Test @Order(6)
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

    @Test @Order(7)
    @DisplayName("포스트 읽기 성공")
    void getPost() throws Exception {
        //given
        Long postId = 1L;

        //when
        MockHttpServletRequestBuilder builder = get("/sns/post/{postId}",postId)
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
                .andDo(document("get-post",
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

    @Test @Order(8)
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
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
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

    @Test @Order(9)
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
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
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

    @Test @Order(10)
    @DisplayName("포스트 삭제 성공")
    @Transactional
    void deletePost() throws Exception {

        //given
        LoginToken token = login(SignupDTO.builder().userId("mf0001").password("P@ssw0rd").build());
        Long postId = 1L;

        //when
        MockHttpServletRequestBuilder builder = delete("/sns/post/{postId}",postId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token.getAccessToken())
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
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andExpect(jsonPath("_links.get-category-posts").exists())
                .andDo(document("delete-post",
                        links(
                                linkWithRel("self").description("link to self"),
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
                                fieldWithPath("body.id").description("postId of deleted post"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.get-favorite-posts.href").description("link to favorite posts"),
                                fieldWithPath("_links.get-category-posts.href").description("link to category posts")
                        )
                ));
    }

    @Test @Order(11)
    @DisplayName("즐겨찾기 게시판 목록 읽기 성공")
    void getFavoritePosts() throws Exception {
        //given

        Long lastId = 0L;
        int limit = 10;

        //when
        MockHttpServletRequestBuilder builder = get("/sns/post/favorite",lastId)
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
                .andExpect(jsonPath("body",hasSize(6)))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andDo(document("favorite-posts",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("get-favorite-posts").description("link to favorite posts")
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
                                fieldWithPath("body[].likers").description("likers"),
                                fieldWithPath("body[].commentNum").description("commentNum"),
                                fieldWithPath("body[].retweet").description("retweet"),
                                fieldWithPath("body[].category").description("category"),
                                fieldWithPath("body[].hits").description("hits"),
                                fieldWithPath("body[].blocked").description("isBlocked"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.get-favorite-posts.href").description("link to favorite posts")
                        )
                ));


    }

    @Test @Order(12)
    @DisplayName("카테고리 게시판 목록 읽기")
    void getCategoryPosts() throws Exception {
        //given

        //when

        //then

    }

    void setPostList(){
        //given
        Member member1 = memberRepository.findMemberById(1L);
        Member member2 = memberRepository.findMemberById(2L);
        Member member3 = memberRepository.findMemberById(3L);

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

    }


    public static void setDatabase(){
        List<String> interests1 = new ArrayList<>();
        interests1.add("INTER1");
        interests1.add("INTER2");
        interests1.add("INTER3");
        interests1.add("INTER4");

        List<String> interests2 = new ArrayList<>();
        interests2.add("INTER1");
        interests2.add("INTER3");

        List<String> interests3 = new ArrayList<>();
        interests3.add("INTER3");
        interests3.add("INTER4");
        interests3.add("INTER5");

        List<String> majors1 = new ArrayList<>();
        majors1.add("MAJOR1");
        majors1.add("MAJOR2");

        List<String> majors2 = new ArrayList<>();
        majors2.add("MAJOR2");
        majors2.add("MAJOR3");

        List<String> majors3 = new ArrayList<>();
        majors3.add("MAJOR1");
        majors3.add("MAJOR3");

        SignupDTO signupDTO1 = SignupDTO.builder()
                .userId("mf0001")
                .nickname("moon1")
                .schoolEmail("nutee.skhu.2020@gmail.com")
                .password("P@ssw0rd")
                .otp("000000")
                .interests(interests1)
                .majors(majors1)
                .build();

        SignupDTO signupDTO2 = SignupDTO.builder()
                .userId("mf0002")
                .nickname("moon2")
                .schoolEmail("nutee.skhu.2020@gmail.com")
                .password("P@ssw0rd")
                .otp("000000")
                .interests(interests2)
                .majors(majors2)
                .build();

        SignupDTO signupDTO3 = SignupDTO.builder()
                .userId("mf0003")
                .nickname("moon3")
                .schoolEmail("nutee.skhu.2020@gmail.com")
                .password("P@ssw0rd")
                .otp("000000")
                .interests(interests3)
                .majors(majors3)
                .build();

//        sendOtp();
        createMember(signupDTO1);
        createMember(signupDTO2);
        createMember(signupDTO3);



    }

    public static void sendOtp() {
        RestTemplate rest = new RestTemplate();
        Map<String,String> map = new HashMap<>();
        map.put("schoolEmail","nutee.skhu.2020@gmail.com");
        rest.postForObject("http://localhost:8080/auth/sendotp", map, String.class);
    }

    public static Member createMember(SignupDTO dto) {
        RestTemplate rest = new RestTemplate();
        return rest.postForObject("http://localhost:8080/auth/signup", dto, Member.class);
    }

    public LoginToken login(SignupDTO dto) {
        RestTemplate rest = new RestTemplate();
        return rest.postForObject("http://localhost:8080/auth/login", dto, LoginToken.class);
    }
}