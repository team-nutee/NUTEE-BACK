package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.DTO.Resource.ResponseResource;
import kr.nutee.nuteebackend.DTO.Response.PostShowResponse;
import kr.nutee.nuteebackend.DTO.Response.Response;
import kr.nutee.nuteebackend.DTO.Response.UserData;
import kr.nutee.nuteebackend.Service.MemberService;
import kr.nutee.nuteebackend.Service.PostService;
import kr.nutee.nuteebackend.Service.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RefreshScope
@RequestMapping(path = "/sns/user",consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final Util util;
    private final MemberService memberService;
    private final PostService postService;

    /*
        내 데이터 호출
     */
    @GetMapping(path = "/me")
    public ResponseEntity<ResponseResource> getMe(
        HttpServletRequest request
    ) {
        Long memberId = util.getTokenMemberId(request);
        UserData user = memberService.getUserData(memberId);
        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(user)
            .build();

        ResponseResource resource = new ResponseResource(response, UserController.class, memberId);
        return ResponseEntity.ok().body(resource);
    }

    /*
        내가 작성한 게시글 호출
     */
    @GetMapping(path = "/me/posts")
    public ResponseEntity<ResponseResource> getMyPosts(
        @RequestParam("lastId") int lastId,
        @RequestParam("limit") int limit,
        HttpServletRequest request
    ){
        Long memberId = util.getTokenMemberId(request);
        List<PostShowResponse> posts = postService.getUserPosts(memberId, limit, (long)lastId);
        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(posts)
            .build();

        ResponseResource resource = new ResponseResource(response, UserController.class,"/me/posts");

        return ResponseEntity.ok().body(resource);
    }

    /*
        내가 댓글을 작성한 게시글 호출
     */
    @GetMapping(path = "/me/comment/posts")
    public ResponseEntity<ResponseResource> getMyCommentPosts(
        @RequestParam("lastId") int lastId,
        @RequestParam("limit") int limit,
        HttpServletRequest request
    ){
        Long memberId = util.getTokenMemberId(request);
        List<PostShowResponse> posts = postService.getUserCommentPosts(memberId, limit, (long)lastId);
        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(posts)
            .build();

        ResponseResource resource = new ResponseResource(response, UserController.class,"/me/comment/posts");

        return ResponseEntity.ok().body(resource);
    }

    /*
        내가 좋아요 누른 게시글 호출
     */
    @GetMapping(path = "/me/like/posts")
    public ResponseEntity<ResponseResource> getMyLikePosts(
        @RequestParam("lastId") int lastId,
        @RequestParam("limit") int limit,
        HttpServletRequest request
    ){
        Long memberId = util.getTokenMemberId(request);
        List<PostShowResponse> posts = postService.getUserLikePosts(memberId, limit, (long)lastId);
        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(posts)
            .build();

        ResponseResource resource = new ResponseResource(response, UserController.class,"/me/like/posts");

        return ResponseEntity.ok().body(resource);
    }

    /*
        유저 데이터 호출
     */
    @GetMapping(path = "/{userId}")
    public ResponseEntity<ResponseResource> getUser(
            @PathVariable String userId
    ) {
        UserData user = memberService.getUserData(Long.parseLong(userId));
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(user)
                .build();

        ResponseResource resource = new ResponseResource(response, UserController.class, Long.parseLong(userId));
        return ResponseEntity.ok().body(resource);
    }

    /*
        유저가 작성한 게시글 호출
     */
    @GetMapping(path = "/{userId}/posts")
    public ResponseEntity<ResponseResource> getUserPosts(
            @PathVariable String userId,
            @RequestParam("lastId") int lastId,
            @RequestParam("limit") int limit
    ){
        List<PostShowResponse> posts = postService.getUserPosts(Long.parseLong(userId), limit, (long)lastId);
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(posts)
                .build();

        ResponseResource resource = new ResponseResource(response, UserController.class, Long.parseLong(userId),"posts");

        return ResponseEntity.ok().body(resource);
    }
}
