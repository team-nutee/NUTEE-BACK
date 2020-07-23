package kr.nutee.nuteebackend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nutee.nuteebackend.DTO.Request.*;
import kr.nutee.nuteebackend.DTO.Resource.ResponseResource;
import kr.nutee.nuteebackend.DTO.Response.PostResponse;
import kr.nutee.nuteebackend.DTO.Response.Response;
import kr.nutee.nuteebackend.Enum.ErrorCode;
import kr.nutee.nuteebackend.Exception.EmptyAttributeException;
import kr.nutee.nuteebackend.Exception.GlobalExceptionHandler;
import kr.nutee.nuteebackend.Exception.NotAllowedException;
import kr.nutee.nuteebackend.Interceptor.HttpInterceptor;
import kr.nutee.nuteebackend.Service.MemberService;
import kr.nutee.nuteebackend.Service.PostService;
import kr.nutee.nuteebackend.Service.Util;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(path = "/sns/post",consumes = MediaType.APPLICATION_JSON_VALUE)
@ResponseBody
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final Util util;

    /*
        즐겨찾기 게시판 불러오기
     */
    @GetMapping(path = "/favorite")
    public ResponseEntity<Response> getFavoritePosts(
            HttpServletRequest request,
            @RequestParam("lastId") int lastId,
            @RequestParam("limit") int limit
    ){
        Long memberId = util.getTokenMemberId(request);

        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(postService.getFavoritePosts((long)lastId,limit,memberId))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        카테고리 게시판 불러오기
     */
    @GetMapping(path = "/category/{category}")
    public ResponseEntity<Response> getCategoryPosts(
            @RequestParam("lastId") int lastId,
            @RequestParam("limit") int limit,
            @PathVariable String category
    ){
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(postService.getCategoryPosts((long)lastId,limit,category))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
        글 작성
     */
    @PostMapping(path = "")
    public ResponseEntity<ResponseResource> createPost(
            HttpServletRequest request,
            @RequestBody @Valid CreatePostRequest body
    ){
        Long id = util.getTokenMemberId(request);

        PostResponse post = postService.createPost(id, body);


        if(body.getTitle().trim().equals("")||body.getContent().trim().equals("")){
            throw new EmptyAttributeException("제목 혹은 내용이 없습니다.",ErrorCode.EMPTY_ATTRIBUTE,HttpStatus.BAD_REQUEST);
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
        resource.add(linkTo(PostController.class).slash(body.getCategory()).withRel("get-category-posts"));


        return ResponseEntity.created(createdURI).body(resource);
    }

    /*
        글 읽기
     */
    @GetMapping(path = "/{postId}")
    public ResponseEntity<ResponseResource> getPost(
            HttpServletRequest request,
            @PathVariable String postId
    ){
        Long memberId = util.getTokenMemberId(request);
        PostResponse post = postService.getPost(Long.parseLong(postId), memberId);
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(post)
                .build();

        ResponseResource resource = new ResponseResource(response, PostController.class, post.getId());
        resource.add(linkTo(PostController.class).slash(post.getId()).withRel("update-post"));
        resource.add(linkTo(PostController.class).slash(post.getId()).withRel("remove-post"));

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
    ){
        Long memberId = util.getTokenMemberId(request);

        PostResponse post = postService.updatePost(Long.parseLong(postId), memberId, body);

        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(post)
                .build();

        ResponseResource resource = new ResponseResource(response, PostController.class, post.getId());
        resource.add(linkTo(PostController.class).slash(post.getId()).withRel("update-post"));
        resource.add(linkTo(PostController.class).slash(post.getId()).withRel("remove-post"));
        return ResponseEntity.ok().body(resource);
    }

    /*
        글 삭제
     */
    @DeleteMapping(path = "/{postId}")
    public ResponseEntity<ResponseResource> deletePost(
            HttpServletRequest request,
            @PathVariable String postId
    ){
        Long memberId = util.getTokenMemberId(request);
        Map<String, Long> post = postService.deletePost(Long.parseLong(postId), memberId);
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(postService.deletePost(Long.parseLong(postId),memberId))
                .build();

        ResponseResource resource = new ResponseResource(response, PostController.class, post.get("id"));
        return ResponseEntity.ok().body(resource);
    }

    /*
        글 신고
     */
    @PostMapping(path = "/{postId}/report")
    public ResponseEntity<Response> reportPost(
            @PathVariable String postId,
            HttpServletRequest request,
            @RequestBody @Valid ReportRequest body
            ){
        Long memberId = util.getTokenMemberId(request);
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(postService.reportPost(Long.parseLong(postId),memberId,body.getContent()))
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    /*
        댓글목록 읽기
     */
    @GetMapping(path = "/{postId}/comments")
    public ResponseEntity<Response> getComments(
            @PathVariable String postId,
            @RequestParam("lastId") Long lastId,
            @RequestParam("limit") int limit

    ){
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(postService.getComments(Long.parseLong(postId)))
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    /*
        댓글 작성
     */
    @PostMapping(path = "/{postId}/comment")
    public ResponseEntity<Response> createComment(
            @PathVariable String postId,
            HttpServletRequest request,
            @RequestBody @Valid CommentRequest body
    ){
        Long memberId = util.getTokenMemberId(request);
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(postService.createComment(memberId,Long.parseLong(postId),body.getContent()))
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    /*
        댓글 수정
     */
    @PatchMapping(path = "/{postId}/comment/{commentId}")
    public ResponseEntity<Response> updateComment(
            @PathVariable String postId,
            @PathVariable String commentId,
            HttpServletRequest request,
            @RequestBody @Valid CommentRequest body
    ){
        Long memberId = util.getTokenMemberId(request);
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(postService.updateComment(memberId,Long.parseLong(commentId),body.getContent()))
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    /*
        답글 생성
     */
    @PostMapping(path = "/{postId}/comment/{parentId}")
    public ResponseEntity<Response> createReComment(
            @PathVariable String postId,
            @PathVariable String parentId,
            HttpServletRequest request,
            @RequestBody @Valid CommentRequest body
    ){
        Long memberId = util.getTokenMemberId(request);
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(postService.createReComment(memberId,Long.parseLong(parentId),Long.parseLong(postId),body.getContent()))
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    /*
        댓글 삭제
     */
    @DeleteMapping(path = "/{postId}/comment/{commentId}")
    public ResponseEntity<Response> deleteComment(
            @PathVariable String postId,
            @PathVariable String commentId,
            HttpServletRequest request
    ){
        Long memberId = util.getTokenMemberId(request);
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(postService.deleteComment(memberId,Long.parseLong(commentId),Long.parseLong(postId)))
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping(path = "/{postId}/like")
    public ResponseEntity<Response> likePost(
            @PathVariable String postId,
            HttpServletRequest request
    ){
        Long memberId = util.getTokenMemberId(request);
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(postService.likePost(Long.parseLong(postId),memberId))
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping(path = "/{postId}/like")
    public ResponseEntity<Response> unlikePost(
            @PathVariable String postId,
            HttpServletRequest request
    ){
        Long memberId = util.getTokenMemberId(request);
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(postService.unlikePost(Long.parseLong(postId),memberId))
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @PostMapping(path = "/{postId}/retweet")
    public ResponseEntity<Response> retweetPost(
            @PathVariable String postId,
            HttpServletRequest request,
            @RequestBody @Valid RetweetRequest body
    ){
        Long memberId = util.getTokenMemberId(request);
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(postService.createRetweet(Long.parseLong(postId),memberId,body))
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}

