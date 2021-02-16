package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.DTO.Request.*;
import kr.nutee.nuteebackend.DTO.Response.PostResponse;
import kr.nutee.nuteebackend.DTO.Response.User;
import kr.nutee.nuteebackend.Domain.Member;
import kr.nutee.nuteebackend.Enum.Category;
import org.junit.jupiter.api.*;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
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
                .andDo(document("create-post"));
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
                .andDo(document("update-post"));

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
                .andDo(document("get-post"));

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
                .andExpect(jsonPath("_links.get-category-posts").exists());
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
                .andExpect(jsonPath("_links.get-category-posts").exists());
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
                .andDo(document("delete-post"));
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
                .andExpect(jsonPath("body",hasSize(10)))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.get-favorite-posts").exists())
                .andDo(document("get-favorite-posts"));


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
                .andDo(document("get-category-posts"));

    }

    @Test @Order(12)
    @DisplayName("카테고리 게시판 목록 읽기")
    void getAllPosts() throws Exception {
        //given
        Long lastId = 0L;
        int limit = 10;

        //when
        MockHttpServletRequestBuilder builder = get("/sns/post/all")
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
            .andDo(document("get-all-posts"));

    }

    @Test @Order(13)
    @DisplayName("게시글 신고 성공")
    void reportPost() throws Exception {
        //given
        Long postId = 1L;
        ReportPostRequest body = ReportPostRequest.builder()
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
                .andDo(document("report-post"));

    }

    @Test @Order(14)
    @DisplayName("게시글 신고 실패 (이미 신고 누른 게시물)")
    void reportPost_alreadyReport() throws Exception {
        //given
        Long postId = 1L;
        ReportPostRequest body = ReportPostRequest.builder()
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
                .andDo(document("report-post"));


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
                .andDo(document("get-comments"));


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
                .andDo(document("create-comment"));
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
                .andDo(document("update-comment"));
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
                .andDo(document("create-reComment"));


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
                .andDo(document("delete-comment"));

    }

    @Test @Order(20)
    @DisplayName("댓글 신고 성공")
    void reportComment() throws Exception {
        //given
        Long postId = 1L;
        Long commentId = 1L;
        String content = "역겨운 댓글입니다.";
        ReportCommentRequest body = ReportCommentRequest.builder()
            .content(content)
            .build();

        //when
        MockHttpServletRequestBuilder builder = post("/sns/post/{postId}/comment/{commentId}/report",postId,commentId)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token)
            .content(objectMapper.writeValueAsString(body))
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
            .andDo(document("report-comment"));
    }

    @Test @Order(21)
    @DisplayName("댓글 좋아요 성공")
    void commentLike() throws Exception {
        //given
        Long postId = 1L;
        Long commentId = 1L;

        //when
        MockHttpServletRequestBuilder builder = post("/sns/post/{postId}/comment/{commentId}/like",postId,commentId)
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
            .andDo(document("like-comment"));

    }

    @Test @Order(22)
    @DisplayName("게시글 좋아요 취소 성공")
    void commentUnlike() throws Exception {
        //given
        Long postId = 1L;
        Long commentId = 1L;


        //when
        MockHttpServletRequestBuilder builder = delete("/sns/post/{postId}/comment/{commentId}/unlike",postId,commentId)
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
            .andDo(document("unlike-comment"));


    }

    @Test @Order(23)
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
                .andDo(document("like-post"));

    }

    @Test @Order(24)
    @DisplayName("게시글 좋아요 취소 성공")
    void postUnlike() throws Exception {
        //given
        Long postId = 1L;


        //when
        MockHttpServletRequestBuilder builder = delete("/sns/post/{postId}/unlike",postId)
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
                .andDo(document("unlike-post"));


    }

    @Test @Order(25)
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
                .andDo(document("retweet-post"));
    }
}
