package kr.nutee.nuteebackend.Controller;


import kr.nutee.nuteebackend.Controller.Common.RestDocsConfiguration;
import kr.nutee.nuteebackend.DTO.Request.CreatePostRequest;
import kr.nutee.nuteebackend.DTO.Request.ImageRequest;
import kr.nutee.nuteebackend.DTO.Request.UpdatePostRequest;
import kr.nutee.nuteebackend.DTO.Response.ImageResponse;
import kr.nutee.nuteebackend.DTO.Response.PostResponse;
import kr.nutee.nuteebackend.DTO.Response.User;
import kr.nutee.nuteebackend.Domain.Member;
import kr.nutee.nuteebackend.Domain.Post;
import kr.nutee.nuteebackend.Repository.MemberRepository;
import kr.nutee.nuteebackend.Repository.PostRepository;
import kr.nutee.nuteebackend.Service.Util;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;

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
class PostControllerTest extends BaseControllerTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    Util util;

    @Test
    @DisplayName("포스트 생성 이미지 X")
    void createPost() throws Exception {

        //given
        CreatePostRequest body = CreatePostRequest.builder()
                .title("제목 테스트")
                .content("내용 테스트")
                .category("IT")
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
                .andExpect(jsonPath("body.category").value("IT"))
                .andExpect(jsonPath("body.user").value(user))
                .andExpect(jsonPath("body.images").isEmpty())
                .andExpect(jsonPath("body.likers").isEmpty())
                .andExpect(jsonPath("body.comments").isEmpty())
                .andExpect(jsonPath("body.retweet").isEmpty())
                .andExpect(jsonPath("body.hits").value(0))
                .andExpect(jsonPath("body.blocked").value(false))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-post").exists())
                .andExpect(jsonPath("_links.remove-post").exists());
    }

    @Test
    @DisplayName("포스트 생성 이미지 O")
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
                .andDo(document("create-post",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("update-post").description("link to update the post"),
                                linkWithRel("remove-post").description("link to remove the post")
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
                                fieldWithPath("_links.remove-post.href").description("link to remove post")
                        )
                ));
    }

    @Test
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


    @Test
    @DisplayName("포스트 수정")
    public void updatePost() throws Exception {
        //given
        List<ImageRequest> images = new ArrayList<>();
        ImageRequest pathOfImage = new ImageRequest("pathOfImageUpdate.jpg");
        images.add(pathOfImage);

        UpdatePostRequest body = UpdatePostRequest.builder()
                .title("제목 수정")
                .content("내용 수정")
                .images(images)
                .build();

        PostResponse post = util.transferPost(postRepository.findPostById(10L));
        User user = post.getUser();

        //when
        MockHttpServletRequestBuilder builder = patch("/sns/post/{postId}",10L)
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
                .andDo(document("update-post",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("update-post").description("link to update the post"),
                                linkWithRel("remove-post").description("link to remove the post")
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
                                fieldWithPath("_links.remove-post.href").description("link to remove post")
                        )
                ));

    }

    @Test
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

    @Test
    @DisplayName("포스트 수정 권한 없는 사람 접근")
    public void updatePost_Not_Allowed() throws Exception {
        //given
        UpdatePostRequest body = UpdatePostRequest.builder()
                .title("제목 수정")
                .content("내용 수정")
                .build();

        //when
        MockHttpServletRequestBuilder builder = patch("/sns/post/9")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(body));


        //then
        mockMvc.perform(builder)
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("포스트 읽기")
    public void getPost() throws Exception {
        //given
        Long postId = 10L;

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
                .andDo(document("get-post",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("update-post").description("link to update the post"),
                                linkWithRel("remove-post").description("link to remove the post")
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
                                fieldWithPath("_links.remove-post.href").description("link to remove post")
                        )
                ));

    }

    @Test
    @DisplayName("포스트 삭제")
    public void deletePost() throws Exception {

        //given
        Long postId = 10L;

        //when
        MockHttpServletRequestBuilder builder = delete("/sns/post/{postId}",postId)
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
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("delete-post",
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
                                fieldWithPath("body.id").description("id of the post"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));
    }

    @Test
    @DisplayName("즐겨찾기 게시판 목록 읽기")
    public void getPreferencePosts() throws Exception {
        //given
        Long memberId =1L;
        Long lastId = 0L;
        int limit = 10;

        Member member = memberRepository.findMemberById(memberId);

        //when
        MockHttpServletRequestBuilder builder = get("/sns/preference",lastId)
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
                .andExpect(jsonPath("body").exists())
                .andExpect(jsonPath("body.id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("delete-post",
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
                                fieldWithPath("body.id").description("id of the post"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ));


    }

    @Test
    @DisplayName("카테고리 게시판 목록 읽기")
    public void getCategoryPosts() throws Exception {
        //given

        //when

        //then

    }
}