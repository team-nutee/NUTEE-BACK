package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.DTO.Request.*;
import kr.nutee.nuteebackend.DTO.Resource.ResponseResource;
import kr.nutee.nuteebackend.DTO.Response.CommentResponse;
import kr.nutee.nuteebackend.DTO.Response.PostResponse;
import kr.nutee.nuteebackend.DTO.Response.Response;
import kr.nutee.nuteebackend.Domain.Post;
import kr.nutee.nuteebackend.Enum.ErrorCode;
import kr.nutee.nuteebackend.Exception.EmptyAttributeException;
import kr.nutee.nuteebackend.Repository.PostRepository;
import kr.nutee.nuteebackend.Service.PostService;
import kr.nutee.nuteebackend.Service.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RefreshScope
@RequestMapping(path = "/sns/post", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final Util util;

    /*
        즐겨찾기 게시판 불러오기
     */
    @GetMapping(path = "/favorite")
    public ResponseEntity<ResponseResource> getFavoritePosts(
            HttpServletRequest request,
            @RequestParam("lastId") int lastId,
            @RequestParam("limit") int limit
    ) {
        Long memberId = util.getTokenMemberId(request);

        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(postService.getFavoritePosts((long) lastId, limit, memberId))
                .build();

        ResponseResource resource = new ResponseResource(response, PostController.class);
        resource.add(linkTo(PostController.class).slash("favorite").withRel("get-favorite-posts"));

        return ResponseEntity.ok().body(resource);
    }

    /*
        카테고리 게시판 불러오기
     */
    @GetMapping(path = "/category/{category}")
    public ResponseEntity<ResponseResource> getCategoryPosts(
            @RequestParam("lastId") int lastId,
            @RequestParam("limit") int limit,
            @PathVariable String category
    ) {
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(postService.getCategoryPosts((long) lastId, limit, category))
                .build();

        ResponseResource resource = new ResponseResource(response, PostController.class);
        resource.add(linkTo(PostController.class).slash("favorite").withRel("get-favorite-posts"));
        resource.add(linkTo(PostController.class).slash("category").slash(category).withRel("get-category-posts"));

        return ResponseEntity.ok().body(resource);
    }

    /*
        모든 게시물 불러오기
     */
    @GetMapping(path = "/all")
    public ResponseEntity<ResponseResource> getAllPosts(
        @RequestParam("lastId") int lastId,
        @RequestParam("limit") int limit
    ) {
        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(postService.getAllPosts((long) lastId, limit))
            .build();

        ResponseResource resource = new ResponseResource(response, PostController.class);

        return ResponseEntity.ok().body(resource);
    }

    /*
        글 작성
     */
    @PostMapping(path = "")
    public ResponseEntity<ResponseResource> createPost(
            HttpServletRequest request,
            @RequestBody @Valid CreatePostRequest body
    ) {
        Long id = util.getTokenMemberId(request);

        PostResponse post = postService.createPost(id, body);


        if (body.getTitle().trim().equals("") || body.getContent().trim().equals("")) {
            throw new EmptyAttributeException("제목 혹은 내용이 없습니다.", ErrorCode.EMPTY_ATTRIBUTE, HttpStatus.BAD_REQUEST);
        }

        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(post)
                .build();

        ResponseResource resource = new ResponseResource(response, PostController.class, post.getId());
        WebMvcLinkBuilder selfLinkBuilder = linkTo(PostController.class).slash(post.getId());
        URI createdURI = selfLinkBuilder.toUri();
        resource.add(linkTo(PostController.class).slash(post.getId()).withRel("update-post"));
        resource.add(linkTo(PostController.class).slash(post.getId()).withRel("remove-post"));
        resource.add(linkTo(PostController.class).slash("favorite").withRel("get-favorite-posts"));
        resource.add(linkTo(PostController.class).slash("category").slash(body.getCategory()).withRel("get-category-posts"));

        return ResponseEntity.created(createdURI).body(resource);
    }

    /*
        글 읽기
     */
    @GetMapping(path = "/{postId}")
    public ResponseEntity<ResponseResource> getPost(
            HttpServletRequest request,
            @PathVariable String postId
    ) {
        Long memberId = util.getTokenMemberId(request);
        PostResponse post = postService.getPost(Long.parseLong(postId), memberId);
        String category = postRepository.findPostById(post.getId()).getCategory();
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(post)
                .build();

        ResponseResource resource = new ResponseResource(response, PostController.class, post.getId());
        resource.add(linkTo(PostController.class).slash(post.getId()).withRel("update-post"));
        resource.add(linkTo(PostController.class).slash(post.getId()).withRel("remove-post"));
        resource.add(linkTo(PostController.class).slash("favorite").withRel("get-favorite-posts"));
        resource.add(linkTo(PostController.class).slash(category).slash(category).withRel("get-category-posts"));
        return ResponseEntity.ok().body(resource);
    }

    /*
        글 수정
     */
    @PatchMapping(path = "/{postId}")
    public ResponseEntity<ResponseResource> updatePost(
            HttpServletRequest request,
            @PathVariable String postId,
            @RequestBody @Valid UpdatePostRequest body
    ) {
        Long memberId = util.getTokenMemberId(request);

        PostResponse post = postService.updatePost(Long.parseLong(postId), memberId, body);
        String category = postRepository.findPostById(post.getId()).getCategory();

        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(post)
                .build();

        ResponseResource resource = new ResponseResource(response, PostController.class, post.getId());
        resource.add(linkTo(PostController.class).slash(post.getId()).withRel("update-post"));
        resource.add(linkTo(PostController.class).slash(post.getId()).withRel("remove-post"));
        resource.add(linkTo(PostController.class).slash("favorite").withRel("get-favorite-posts"));
        resource.add(linkTo(PostController.class).slash("category").slash(category).withRel("get-category-posts"));
        return ResponseEntity.ok().body(resource);
    }

    /*
        글 삭제
     */
    @DeleteMapping(path = "/{postId}")
    public ResponseEntity<ResponseResource> deletePost(
            HttpServletRequest request,
            @PathVariable String postId
    ) {
        Long memberId = util.getTokenMemberId(request);

        Map<String, Long> post = postService.deletePost(Long.parseLong(postId), memberId);

        String category = postRepository.findPostById(post.get("id")).getCategory();

        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(post)
                .build();

        ResponseResource resource = new ResponseResource(response, PostController.class, post.get("id"));
        resource.add(linkTo(PostController.class).slash("favorite").withRel("get-favorite-posts"));
        resource.add(linkTo(PostController.class).slash("category").slash(category).withRel("get-category-posts"));
        return ResponseEntity.ok().body(resource);
    }

    /*
        글 신고
     */
    @PostMapping(path = "/{postId}/report")
    public ResponseEntity<ResponseResource> reportPost(
            @PathVariable String postId,
            HttpServletRequest request,
            @RequestBody @Valid ReportPostRequest body
    ) {
        Long memberId = util.getTokenMemberId(request);

        String category = postRepository.findPostById(Long.parseLong(postId)).getCategory();

        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(postService.reportPost(Long.parseLong(postId), memberId, body.getContent()))
                .build();

        ResponseResource resource = new ResponseResource(response, PostController.class, Long.parseLong(postId));
        resource.add(linkTo(PostController.class).slash(Long.parseLong(postId)).withRel("update-post"));
        resource.add(linkTo(PostController.class).slash(Long.parseLong(postId)).withRel("remove-post"));
        resource.add(linkTo(PostController.class).slash("favorite").withRel("get-favorite-posts"));
        resource.add(linkTo(PostController.class).slash("category").slash(category).withRel("get-category-posts"));
        return ResponseEntity.ok().body(resource);
    }

    /*
        게시글 좋아요
     */
    @PostMapping(path = "/{postId}/like")
    public ResponseEntity<ResponseResource> likePost(
        @PathVariable String postId,
        HttpServletRequest request
    ) {
        Long memberId = util.getTokenMemberId(request);
        PostResponse post = postService.likePost(Long.parseLong(postId), memberId);
        String category = post.getCategory();
        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(post)
            .build();

        ResponseResource resource = new ResponseResource(response, PostController.class, Long.parseLong(postId));
        resource.add(linkTo(PostController.class).slash(Long.parseLong(postId)).withRel("get-post"));
        resource.add(linkTo(PostController.class).slash("favorite").withRel("get-favorite-posts"));
        resource.add(linkTo(PostController.class).slash("category").slash(category).withRel("get-category-posts"));
        resource.add(linkTo(PostController.class).slash(Long.parseLong(postId)).slash("comments").withRel("get-comments"));

        return ResponseEntity.ok().body(resource);
    }

    /*
        게시글 좋아요 취소
     */
    @DeleteMapping(path = "/{postId}/unlike")
    public ResponseEntity<ResponseResource> unlikePost(
        @PathVariable String postId,
        HttpServletRequest request
    ) {
        Long memberId = util.getTokenMemberId(request);
        PostResponse post = postService.unlikePost(Long.parseLong(postId), memberId);
        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(post)
            .build();
        ResponseResource resource = new ResponseResource(response, PostController.class, Long.parseLong(postId));
        resource.add(linkTo(PostController.class).slash(Long.parseLong(postId)).withRel("get-post"));
        resource.add(linkTo(PostController.class).slash("favorite").withRel("get-favorite-posts"));
        resource.add(linkTo(PostController.class).slash("category").slash(post.getCategory()).withRel("get-category-posts"));
        resource.add(linkTo(PostController.class).slash(Long.parseLong(postId)).slash("comments").withRel("get-comments"));

        return ResponseEntity.ok().body(resource);
    }

    /*
        댓글목록 읽기
     */
    @GetMapping(path = "/{postId}/comments")
    public ResponseEntity<ResponseResource> getComments(
            @PathVariable String postId,
            @RequestParam("lastId") Long lastId,
            @RequestParam("limit") int limit
    ) {
        Post post = postRepository.findPostById(Long.parseLong(postId));
        List<CommentResponse> comments = postService.getComments(Long.parseLong(postId));
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(comments)
                .build();
        ResponseResource resource = new ResponseResource(response, PostController.class, Long.parseLong(postId));
        resource.add(linkTo(PostController.class).slash(Long.parseLong(postId)).withRel("get-post"));
        resource.add(linkTo(PostController.class).slash("favorite").withRel("get-favorite-posts"));
        resource.add(linkTo(PostController.class).slash("category").slash(post.getCategory()).withRel("get-category-posts"));
        resource.add(linkTo(PostController.class).slash(Long.parseLong(postId)).slash("comments").withRel("get-comments"));

        return ResponseEntity.ok().body(resource);
    }

    /*
        댓글 작성
     */
    @PostMapping(path = "/{postId}/comment")
    public ResponseEntity<ResponseResource> createComment(
            @PathVariable String postId,
            HttpServletRequest request,
            @RequestBody @Valid CommentRequest body
    ) {
        Long memberId = util.getTokenMemberId(request);

        Post post = postRepository.findPostById(Long.parseLong(postId));
        String category = post.getCategory();

        CommentResponse comment = postService.createComment(memberId, Long.parseLong(postId), body.getContent());

        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(comment)
                .build();

        ResponseResource resource = new ResponseResource(response, PostController.class, Long.parseLong(postId));
        WebMvcLinkBuilder selfLinkBuilder = linkTo(PostController.class).slash(post.getId()).slash("comment").slash(comment.getId());
        URI createdURI = selfLinkBuilder.toUri();
        resource.add(linkTo(PostController.class).slash(Long.parseLong(postId)).withRel("get-post"));
        resource.add(linkTo(PostController.class).slash("favorite").withRel("get-favorite-posts"));
        resource.add(linkTo(PostController.class).slash("category").slash(category).withRel("get-category-posts"));
        resource.add(linkTo(PostController.class).slash(Long.parseLong(postId)).slash("comments").withRel("get-comments"));

        return ResponseEntity.created(createdURI).body(resource);
    }

    /*
        댓글 수정
     */
    @PatchMapping(path = "/{postId}/comment/{commentId}")
    public ResponseEntity<ResponseResource> updateComment(
            @PathVariable String postId,
            @PathVariable String commentId,
            HttpServletRequest request,
            @RequestBody @Valid CommentRequest body
    ) {
        Long memberId = util.getTokenMemberId(request);

        Post post = postRepository.findPostById(Long.parseLong(postId));
        String category = post.getCategory();

        CommentResponse comment = postService.updateComment(memberId, Long.parseLong(commentId), body.getContent());
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(comment)
                .build();

        ResponseResource resource = new ResponseResource(response, PostController.class, Long.parseLong(postId));
        resource.add(linkTo(PostController.class).slash(Long.parseLong(postId)).withRel("get-post"));
        resource.add(linkTo(PostController.class).slash("favorite").withRel("get-favorite-posts"));
        resource.add(linkTo(PostController.class).slash("category").slash(category).withRel("get-category-posts"));
        resource.add(linkTo(PostController.class).slash(Long.parseLong(postId)).slash("comments").withRel("get-comments"));

        return ResponseEntity.ok().body(resource);
    }

    /*
        답글 생성
     */
    @PostMapping(path = "/{postId}/comment/{parentId}")
    public ResponseEntity<ResponseResource> createReComment(
            @PathVariable String postId,
            @PathVariable String parentId,
            HttpServletRequest request,
            @RequestBody @Valid CommentRequest body
    ) {
        Long memberId = util.getTokenMemberId(request);

        Post post = postRepository.findPostById(Long.parseLong(postId));
        String category = post.getCategory();
        CommentResponse reComment = postService.createReComment(memberId, Long.parseLong(parentId), Long.parseLong(postId), body.getContent());
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(reComment)
                .build();

        ResponseResource resource = new ResponseResource(response, PostController.class, Long.parseLong(postId));
        WebMvcLinkBuilder selfLinkBuilder = linkTo(PostController.class).slash(post.getId()).slash("comment").slash(parentId);
        URI createdURI = selfLinkBuilder.toUri();
        resource.add(linkTo(PostController.class).slash(Long.parseLong(postId)).withRel("get-post"));
        resource.add(linkTo(PostController.class).slash("favorite").withRel("get-favorite-posts"));
        resource.add(linkTo(PostController.class).slash("category").slash(category).withRel("get-category-posts"));
        resource.add(linkTo(PostController.class).slash(Long.parseLong(postId)).slash("comments").withRel("get-comments"));

        return ResponseEntity.created(createdURI).body(resource);
    }

    /*
        댓글 삭제
     */
    @DeleteMapping(path = "/{postId}/comment/{commentId}")
    public ResponseEntity<ResponseResource> deleteComment(
            @PathVariable String postId,
            @PathVariable String commentId,
            HttpServletRequest request
    ) {
        Long memberId = util.getTokenMemberId(request);

        Post post = postRepository.findPostById(Long.parseLong(postId));
        String category = post.getCategory();

        List<CommentResponse> comment = postService.deleteComment(memberId, Long.parseLong(commentId), Long.parseLong(postId));

        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(comment)
                .build();

        ResponseResource resource = new ResponseResource(response, PostController.class, Long.parseLong(postId));
        resource.add(linkTo(PostController.class).slash(Long.parseLong(postId)).withRel("get-post"));
        resource.add(linkTo(PostController.class).slash("favorite").withRel("get-favorite-posts"));
        resource.add(linkTo(PostController.class).slash("category").slash(category).withRel("get-category-posts"));
        resource.add(linkTo(PostController.class).slash(Long.parseLong(postId)).slash("comments").withRel("get-comments"));

        return ResponseEntity.ok().body(resource);
    }

    /*
        댓글 신고
     */
    @PostMapping(path = "/{postId}/comment/{commentId}/report")
    public ResponseEntity<ResponseResource> reportComment(
        @PathVariable String postId,
        @PathVariable String commentId,
        @RequestBody ReportCommentRequest body,
        HttpServletRequest request
    ) {
        Long memberId = util.getTokenMemberId(request);

        CommentResponse comment = postService.reportComment(Long.parseLong(commentId),memberId, body.getContent());

        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(comment)
            .build();

        ResponseResource resource = new ResponseResource(response, PostController.class, Long.parseLong(postId));

        return ResponseEntity.ok().body(resource);
    }

    /*
        댓글 좋아요
     */
    @PostMapping(path = "/{postId}/comment/{commentId}/like")
    public ResponseEntity<ResponseResource> likeComment(
        @PathVariable String postId,
        @PathVariable String commentId,
        HttpServletRequest request
        ) {
        Long memberId = util.getTokenMemberId(request);
        CommentResponse comment = postService.likeComment(Long.parseLong(commentId), memberId);
        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(comment)
            .build();

        ResponseResource resource = new ResponseResource(response, PostController.class, Long.parseLong(postId));

        return ResponseEntity.ok().body(resource);
    }

    /*
        댓글 좋아요 취소
     */
    @DeleteMapping(path = "/{postId}/comment/{commentId}/unlike")
    public ResponseEntity<ResponseResource> unlikeComment(
        @PathVariable String postId,
        @PathVariable String commentId,
        HttpServletRequest request
    ) {
        Long memberId = util.getTokenMemberId(request);
        CommentResponse comment = postService.unlikeComment(Long.parseLong(commentId), memberId);
        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(comment)
            .build();
        ResponseResource resource = new ResponseResource(response, PostController.class, Long.parseLong(postId));

        return ResponseEntity.ok().body(resource);
    }



    /*
        게시글 리트윗
     */
    @PostMapping(path = "/{postId}/retweet")
    public ResponseEntity<ResponseResource> retweetPost(
            @PathVariable String postId,
            HttpServletRequest request,
            @RequestBody @Valid RetweetRequest body
    ) {
        Long memberId = util.getTokenMemberId(request);
        PostResponse post = postService.createRetweet(Long.parseLong(postId), memberId, body);
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(post)
                .build();
        ResponseResource resource = new ResponseResource(response, PostController.class, Long.parseLong(postId));
        WebMvcLinkBuilder selfLinkBuilder = linkTo(PostController.class).slash(post.getId()).slash("retweet");
        URI createdURI = selfLinkBuilder.toUri();
        resource.add(linkTo(PostController.class).slash(Long.parseLong(postId)).withRel("get-post"));
        resource.add(linkTo(PostController.class).slash("favorite").withRel("get-favorite-posts"));
        resource.add(linkTo(PostController.class).slash("category").slash(post.getCategory()).withRel("get-category-posts"));
        resource.add(linkTo(PostController.class).slash(Long.parseLong(postId)).slash("comments").withRel("get-comments"));

        return ResponseEntity.created(createdURI).body(resource);
    }

}

