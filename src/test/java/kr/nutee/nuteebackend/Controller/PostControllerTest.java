package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.DTO.Request.*;
import kr.nutee.nuteebackend.DTO.Response.PostResponse;
import kr.nutee.nuteebackend.DTO.Response.User;
import kr.nutee.nuteebackend.Domain.Interest;
import kr.nutee.nuteebackend.Domain.Major;
import kr.nutee.nuteebackend.Domain.Member;
import kr.nutee.nuteebackend.Domain.Post;
import kr.nutee.nuteebackend.Enum.Category;
import org.junit.jupiter.api.*;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.annotation.Rollback;
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


public class PostControllerTest extends BaseControllerTest {

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
                .andExpect(jsonPath("body.comments",hasSize(14)))
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
        Long postId = 1L;

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
                .andExpect(jsonPath("body",hasSize(7)))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andDo(document("get-favorite-posts",
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
                                fieldWithPath("body[].likers").description("likers").optional(),
                                fieldWithPath("body[].likers[].id").description("id of likers").optional(),
                                fieldWithPath("body[].likers[].nickname").description("nickname of likers").optional(),
                                fieldWithPath("body[].likers[].image").description("profile image of likers").optional(),
                                fieldWithPath("body[].likers[].image.src").type(JsonFieldType.STRING).description("profile image path of likers").optional(),
                                fieldWithPath("body[].commentNum").description("commentNum"),
                                fieldWithPath("body[].retweet").type(JsonFieldType.OBJECT).description("post that sharing other post").optional(),
                                fieldWithPath("body[].retweet.id").description("id of retweet"),
                                fieldWithPath("body[].retweet.title").description("title of retweet"),
                                fieldWithPath("body[].retweet.content").description("content of retweet"),
                                fieldWithPath("body[].retweet.createdAt").description("created time of retweet"),
                                fieldWithPath("body[].retweet.updatedAt").description("updated time of retweet"),
                                fieldWithPath("body[].retweet.user").description("user who retweeted"),
                                fieldWithPath("body[].retweet.user.id").description("user id who retweeted"),
                                fieldWithPath("body[].retweet.user.nickname").description("user nickname who retweeted"),
                                fieldWithPath("body[].retweet.user.image").description("user profile image who retweeted"),
                                fieldWithPath("body[].retweet.images").description("images of retweet"),
                                fieldWithPath("body[].retweet.images[].src").description("images path of retweet"),
                                fieldWithPath("body[].retweet.likers").description("user who liked retweet"),
                                fieldWithPath("body[].retweet.commentNum").description("commentNum of retweet"),
                                fieldWithPath("body[].retweet.category").description("category of retweeted post"),
                                fieldWithPath("body[].retweet.hits").description("views of retweet"),
                                fieldWithPath("body[].retweet.blocked").description("blocking flag of retweet"),
                                fieldWithPath("body[].retweet.deleted").description("whether to delete a retweet"),
                                fieldWithPath("body[].category").description("category"),
                                fieldWithPath("body[].hits").description("views"),
                                fieldWithPath("body[].blocked").description("blocking flag of the post"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.get-favorite-posts.href").description("link to favorite posts")
                        )
                ));


    }

    @Test @Order(12)
    @DisplayName("카테고리 게시판 목록 읽기")
    void getCategoryPosts() throws Exception {
        //given
        Long lastId = 0L;
        int limit = 10;
        String category = "INTER2";

        //when
        MockHttpServletRequestBuilder builder = get("/sns/post/category/{category}",category)
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
                .andExpect(jsonPath("body",hasSize(3)))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andExpect(jsonPath("_links.get-category-posts").exists())
                .andDo(document("get-category-posts",
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
                                fieldWithPath("body[].likers[].id").description("id of likers"),
                                fieldWithPath("body[].likers[].nickname").description("nickname of likers"),
                                fieldWithPath("body[].likers[].image").description("profile image of likers"),
                                fieldWithPath("body[].likers[].image.src").type(JsonFieldType.STRING).description("profile image path of likers").optional(),
                                fieldWithPath("body[].commentNum").description("commentNum"),
                                fieldWithPath("body[].retweet").description("post that sharing other post"),
                                fieldWithPath("body[].category").description("category"),
                                fieldWithPath("body[].hits").description("views"),
                                fieldWithPath("body[].blocked").description("Blocking flag of the post"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.get-favorite-posts.href").description("link to favorite posts"),
                                fieldWithPath("_links.get-category-posts.href").description("link to category posts")
                        )
                ));

    }

    @Test @Order(13)
    @DisplayName("게시글 신고 성공")
    void reportPost() throws Exception {
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

    @Test @Order(14)
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

    @Test @Order(15)
    @DisplayName("게시글 신고 실패 (존재하지 않는 게시물)")
    void reportPost_isNull() throws Exception {
        //given


        //when


        //then


    }

    @Test @Order(16)
    @DisplayName("댓글 목록 읽기 성공")
    void getComments() throws Exception {
        //given
        Long postId = 1L;
        Long lastId = 0L;
        int limit = 10;

        //when
        MockHttpServletRequestBuilder builder = get("/sns/post/{postId}/comments",postId)
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
                .andExpect(jsonPath("body",hasSize(14)))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andExpect(jsonPath("_links.get-category-posts").exists())
                .andDo(document("get-comments",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("get-post").description("link to favorite posts"),
                                linkWithRel("get-favorite-posts").description("link to favorite posts"),
                                linkWithRel("get-category-posts").description("link to category posts"),
                                linkWithRel("get-comments").description("link to favorite posts")
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
                                fieldWithPath("body[].id").description("id of comments"),
                                fieldWithPath("body[].content").description("content of comments"),
                                fieldWithPath("body[].createdAt").description("created time of comments"),
                                fieldWithPath("body[].updatedAt").description("updated time of comments"),
                                fieldWithPath("body[].reComment").description("comment's reComment").optional(),
                                fieldWithPath("body[].reComment[].id").type(JsonFieldType.NUMBER).description("comment's reComment's id").optional(),
                                fieldWithPath("body[].reComment[].content").type(JsonFieldType.STRING).description("comment's reComment's content").optional(),
                                fieldWithPath("body[].reComment[].createdAt").type(JsonFieldType.STRING).description("comment's reComment's created time").optional(),
                                fieldWithPath("body[].reComment[].updatedAt").type(JsonFieldType.STRING).description("comment's reComment's updated time").optional(),
                                fieldWithPath("body[].reComment[].user").type(JsonFieldType.OBJECT).description("comment's reComment's writer").optional(),
                                fieldWithPath("body[].reComment[].user.id").type(JsonFieldType.NUMBER).description("comment's reComment's writer's id").optional(),
                                fieldWithPath("body[].reComment[].user.nickname").type(JsonFieldType.STRING).description("comment's reComment's writer's nickname").optional(),
                                fieldWithPath("body[].reComment[].user.image").type(JsonFieldType.OBJECT).description("comment's reComment's writer's image").optional(),
                                fieldWithPath("body[].reComment[].user.image.src").type(JsonFieldType.STRING).description("comment's reComment's writer's image's path").optional(),
                                fieldWithPath("body[].user").description("comment's writer"),
                                fieldWithPath("body[].user.id").description("comment's writer id"),
                                fieldWithPath("body[].user.nickname").description("comment's writer nickname"),
                                fieldWithPath("body[].user.image").description("comment's writer profile image"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.get-post.href").description("link to post"),
                                fieldWithPath("_links.get-favorite-posts.href").description("link to favorite posts"),
                                fieldWithPath("_links.get-category-posts.href").description("link to category posts"),
                                fieldWithPath("_links.get-comments.href").description("link to comment list")
                        )
                ));


    }

    @Test @Order(17)
    @DisplayName("댓글 작성 성공")
    void createComment() throws Exception {
        //given
        Long postId = 1L;
        CommentRequest body = CommentRequest.builder()
                .content("포스트 1에 댓글 새로 작성.")
                .build();

        //when
        MockHttpServletRequestBuilder builder = post("/sns/post/{postId}/comment",postId)
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
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.get-post").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andExpect(jsonPath("_links.get-category-posts").exists())
                .andExpect(jsonPath("_links.get-comments").exists())
                .andDo(document("create-comment",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("get-post").description("link to post"),
                                linkWithRel("get-favorite-posts").description("link to favorite posts"),
                                linkWithRel("get-category-posts").description("link to category posts"),
                                linkWithRel("get-comments").description("link to comment list")
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
                                fieldWithPath("body.id").description("id of comment"),
                                fieldWithPath("body.content").description("content of comment"),
                                fieldWithPath("body.createdAt").description("created time of comment"),
                                fieldWithPath("body.updatedAt").description("updated time of comment"),
                                fieldWithPath("body.reComment").description("comment's reComment"),
                                fieldWithPath("body.user").description("comment's writer"),
                                fieldWithPath("body.user.id").description("comment's writer id"),
                                fieldWithPath("body.user.nickname").description("comment's writer nickname"),
                                fieldWithPath("body.user.image").description("comment's writer profile image"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.get-post.href").description("link to post"),
                                fieldWithPath("_links.get-favorite-posts.href").description("link to favorite posts"),
                                fieldWithPath("_links.get-category-posts.href").description("link to category posts"),
                                fieldWithPath("_links.get-comments.href").description("link to comments")
                        )
                ));
    }

    @Test @Order(18)
    @DisplayName("댓글 수정 성공")
    void updateComment() throws Exception {
        //given
        Long postId = 1L;
        Long commentId = 1L;
        CommentRequest body = CommentRequest.builder()
                .content("포스트 1에 있는 댓글 수정.")
                .build();

        //when
        MockHttpServletRequestBuilder builder = patch("/sns/post/{postId}/comment/{commentId}",postId,commentId)
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
                .andExpect(jsonPath("_links.get-post").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andExpect(jsonPath("_links.get-category-posts").exists())
                .andExpect(jsonPath("_links.get-comments").exists())
                .andDo(document("update-comment",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("get-post").description("link to post"),
                                linkWithRel("get-favorite-posts").description("link to favorite posts"),
                                linkWithRel("get-category-posts").description("link to category posts"),
                                linkWithRel("get-comments").description("link to comment list")
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
                                fieldWithPath("body.id").description("comment's id"),
                                fieldWithPath("body.content").description("comment's content"),
                                fieldWithPath("body.createdAt").description("created time of comment"),
                                fieldWithPath("body.updatedAt").description("updated time of comment"),
                                fieldWithPath("body.reComment").type(JsonFieldType.ARRAY).description("comment's comment").optional(),
                                fieldWithPath("body.reComment[].id").type(JsonFieldType.NUMBER).description("comment's comment id").optional(),
                                fieldWithPath("body.reComment[].content").type(JsonFieldType.STRING).description("comment's comment content").optional(),
                                fieldWithPath("body.reComment[].createdAt").type(JsonFieldType.STRING).description("created time of comment's comment").optional(),
                                fieldWithPath("body.reComment[].updatedAt").type(JsonFieldType.STRING).description("updated time of comment's comment").optional(),
                                fieldWithPath("body.reComment[].user").type(JsonFieldType.OBJECT).description("comment's comment writer").optional(),
                                fieldWithPath("body.reComment[].user.id").type(JsonFieldType.NUMBER).description("comment's comment writer id").optional(),
                                fieldWithPath("body.reComment[].user.nickname").type(JsonFieldType.STRING).description("comment's comment writer nickname").optional(),
                                fieldWithPath("body.reComment[].user.image").type(JsonFieldType.OBJECT).description("comment's comment writer profile image").optional(),
                                fieldWithPath("body.reComment[].user.image.src").type(JsonFieldType.STRING).description("comment's comment writer profile image path").optional(),
                                fieldWithPath("body.user").description("post writer"),
                                fieldWithPath("body.user.id").description("id of post writer"),
                                fieldWithPath("body.user.nickname").description("nickname of post writer"),
                                fieldWithPath("body.user.image").description("profile image of post writer"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.get-post.href").description("link to post"),
                                fieldWithPath("_links.get-favorite-posts.href").description("link to favorite posts"),
                                fieldWithPath("_links.get-category-posts.href").description("link to category posts"),
                                fieldWithPath("_links.get-comments.href").description("link to comments")
                        )
                ));
    }

    @Test @Order(19)
    @DisplayName("답글 생성 성공")
    void createReComment() throws Exception {
        //given
        Long postId = 1L;
        Long parentId = 1L;
        CommentRequest body = CommentRequest.builder()
                .content("포스트 1에 댓글1에 답글 새로 작성.")
                .build();

        //when
        MockHttpServletRequestBuilder builder = post("/sns/post/{postId}/comment/{parentId}",postId,parentId)
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
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.get-post").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andExpect(jsonPath("_links.get-category-posts").exists())
                .andExpect(jsonPath("_links.get-comments").exists())
                .andDo(document("create-reComment",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("get-post").description("link to post"),
                                linkWithRel("get-favorite-posts").description("link to favorite posts"),
                                linkWithRel("get-category-posts").description("link to category posts"),
                                linkWithRel("get-comments").description("link to comment list")
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
                                fieldWithPath("body.id").description("id"),
                                fieldWithPath("body.content").description("content"),
                                fieldWithPath("body.createdAt").description("createdAt"),
                                fieldWithPath("body.updatedAt").description("updatedAt"),
                                fieldWithPath("body.reComment").description("reComment"),
                                fieldWithPath("body.user").description("user"),
                                fieldWithPath("body.user.id").description("user id"),
                                fieldWithPath("body.user.nickname").description("user nickname"),
                                fieldWithPath("body.user.image").description("user profile image"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.get-post.href").description("link to post"),
                                fieldWithPath("_links.get-favorite-posts.href").description("link to favorite posts"),
                                fieldWithPath("_links.get-category-posts.href").description("link to category posts"),
                                fieldWithPath("_links.get-comments.href").description("link to comments")
                        )
                ));


    }

    @Test @Order(20)
    @DisplayName("댓글 삭제 성공")
    void deleteComment() throws Exception {
        //given
        Long postId = 1L;
        Long commentId = 1L;

        //when
        MockHttpServletRequestBuilder builder = delete("/sns/post/{postId}/comment/{commentId}",postId,commentId)
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
                .andExpect(jsonPath("_links.get-post").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andExpect(jsonPath("_links.get-category-posts").exists())
                .andExpect(jsonPath("_links.get-comments").exists())
                .andDo(document("delete-comment",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("get-post").description("link to post"),
                                linkWithRel("get-favorite-posts").description("link to favorite posts"),
                                linkWithRel("get-category-posts").description("link to category posts"),
                                linkWithRel("get-comments").description("link to comment list")
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
                                fieldWithPath("body[].content").description("content"),
                                fieldWithPath("body[].createdAt").description("createdAt"),
                                fieldWithPath("body[].updatedAt").description("updatedAt"),
                                fieldWithPath("body[].reComment").description("reComment"),
                                fieldWithPath("body[].user").description("user"),
                                fieldWithPath("body[].user.id").description("user id"),
                                fieldWithPath("body[].user.nickname").description("user nickname"),
                                fieldWithPath("body[].user.image").description("user profile image"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.get-post.href").description("link to post"),
                                fieldWithPath("_links.get-favorite-posts.href").description("link to favorite posts"),
                                fieldWithPath("_links.get-category-posts.href").description("link to category posts"),
                                fieldWithPath("_links.get-comments.href").description("link to comments")
                        )
                ));

    }

    @Test @Order(21)
    @DisplayName("게시글 좋아요 성공")
    void postLike() throws Exception {
        //given
        Long postId = 1L;

        //when
        MockHttpServletRequestBuilder builder = post("/sns/post/{postId}/like",postId)
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
                .andExpect(jsonPath("_links.get-post").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andExpect(jsonPath("_links.get-category-posts").exists())
                .andExpect(jsonPath("_links.get-comments").exists())
                .andDo(document("like-post",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("get-post").description("link to post"),
                                linkWithRel("get-favorite-posts").description("link to favorite posts"),
                                linkWithRel("get-category-posts").description("link to category posts"),
                                linkWithRel("get-comments").description("link to comment list")
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
                                fieldWithPath("body.images[].src").description("image paths"),
                                fieldWithPath("body.likers").description("user who likes the post"),
                                fieldWithPath("body.likers[].id").description("user who likes the post"),
                                fieldWithPath("body.likers[].nickname").description("user who likes the post"),
                                fieldWithPath("body.likers[].image").description("user who likes the post"),
                                fieldWithPath("body.likers[].image.src").type(JsonFieldType.STRING).description("user who likes the post").optional(),
                                fieldWithPath("body.comments").description("comments of the post"),
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
                                fieldWithPath("body.retweet").description("post that sharing other post"),
                                fieldWithPath("body.category").description("category of the post"),
                                fieldWithPath("body.hits").description(" user's join number of the post"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.get-post.href").description("link to post"),
                                fieldWithPath("_links.get-favorite-posts.href").description("link to favorite posts"),
                                fieldWithPath("_links.get-category-posts.href").description("link to category posts"),
                                fieldWithPath("_links.get-comments.href").description("link to comments")
                        )
                ));

    }

    @Test @Order(22)
    @DisplayName("게시글 좋아요 취소 성공")
    void postUnlike() throws Exception {
        //given
        Long postId = 2L;


        //when
        MockHttpServletRequestBuilder builder = delete("/sns/post/{postId}/like",postId)
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
                .andExpect(jsonPath("_links.get-post").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andExpect(jsonPath("_links.get-category-posts").exists())
                .andExpect(jsonPath("_links.get-comments").exists())
                .andDo(document("unlike-post",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("get-post").description("link to post"),
                                linkWithRel("get-favorite-posts").description("link to favorite posts"),
                                linkWithRel("get-category-posts").description("link to category posts"),
                                linkWithRel("get-comments").description("link to comment list")
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
                                fieldWithPath("body.images[].src").description("image paths"),
                                fieldWithPath("body.likers").type(JsonFieldType.OBJECT).description("user who likes the post").optional(),
                                fieldWithPath("body.likers[].id").type(JsonFieldType.NUMBER).description("user who likes the post").optional(),
                                fieldWithPath("body.likers[].nickname").type(JsonFieldType.STRING).description("user who likes the post").optional(),
                                fieldWithPath("body.likers[].image").type(JsonFieldType.OBJECT).description("user who likes the post").optional(),
                                fieldWithPath("body.likers[].image.src").type(JsonFieldType.STRING).type(JsonFieldType.STRING).description("user who likes the post").optional(),
                                fieldWithPath("body.comments").description("comments of the post"),
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
                                fieldWithPath("body.retweet").description("post that sharing other post"),
                                fieldWithPath("body.category").description("category of the post"),
                                fieldWithPath("body.hits").description(" user's join number of the post"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.get-post.href").description("link to post"),
                                fieldWithPath("_links.get-favorite-posts.href").description("link to favorite posts"),
                                fieldWithPath("_links.get-category-posts.href").description("link to category posts"),
                                fieldWithPath("_links.get-comments.href").description("link to category posts")
                        )
                ));


    }

    @Test @Order(16)
    @DisplayName("게시글 리트윗 성공")
    void createRetweet() throws Exception {
        //given
        Long postId = 1L;
        RetweetRequest body = RetweetRequest.builder()
                .title("포스트 1의 리트윗2의 제목입니다.")
                .content("포스트 1의 리트윗2의 내용입니다.")
                .category("INTER1")
                .build();


        //when
        MockHttpServletRequestBuilder builder = post("/sns/post/{postId}/retweet",postId)
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
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.get-post").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andExpect(jsonPath("_links.get-category-posts").exists())
                .andExpect(jsonPath("_links.get-comments").exists())
                .andDo(document("retweet-post",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("get-post").description("link to post"),
                                linkWithRel("get-favorite-posts").description("link to favorite posts"),
                                linkWithRel("get-category-posts").description("link to category posts"),
                                linkWithRel("get-comments").description("link to comment list")
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
                                fieldWithPath("body.likers").description("user who likes the post"),
                                fieldWithPath("body.images").description("author's profile image path"),
                                fieldWithPath("body.comments").description("comments of the post"),fieldWithPath("body.retweet").description("post that sharing other post"),
                                fieldWithPath("body.retweet.id").description("id of retweet"),
                                fieldWithPath("body.retweet.title").description("title of retweet"),
                                fieldWithPath("body.retweet.content").description("content of retweet"),
                                fieldWithPath("body.retweet.createdAt").description("created time of retweet"),
                                fieldWithPath("body.retweet.updatedAt").description("updated time of retweet"),
                                fieldWithPath("body.retweet.user").description("user who retweeted"),
                                fieldWithPath("body.retweet.user.id").description("user id who retweeted"),
                                fieldWithPath("body.retweet.user.nickname").description("user nickname who retweeted"),
                                fieldWithPath("body.retweet.user.image").description("user profile image who retweeted"),
                                fieldWithPath("body.retweet.images").description("images of retweet"),
                                fieldWithPath("body.retweet.images[].src").description("images path of retweet"),
                                fieldWithPath("body.retweet.likers").description("user who liked retweet"),
                                fieldWithPath("body.retweet.commentNum").description("comments of retweet"),
                                fieldWithPath("body.retweet.category").description("category of retweet"),
                                fieldWithPath("body.retweet.hits").description("views of retweet"),
                                fieldWithPath("body.retweet.blocked").description("blocking flag of retweet"),
                                fieldWithPath("body.retweet.deleted").description("whether to delete a retweet"),
                                fieldWithPath("body.category").description("category of the post"),
                                fieldWithPath("body.hits").description(" user's join number of the post"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.get-post.href").description("link to post"),
                                fieldWithPath("_links.get-favorite-posts.href").description("link to favorite posts"),
                                fieldWithPath("_links.get-category-posts.href").description("link to category posts"),
                                fieldWithPath("_links.get-comments.href").description("link to comments")
                        )
                ));
    }
}
