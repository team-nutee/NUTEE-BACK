package kr.nutee.nuteebackend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nutee.nuteebackend.DTO.Request.CreatePostRequest;
import kr.nutee.nuteebackend.DTO.Request.ReportRequest;
import kr.nutee.nuteebackend.DTO.Request.UpdatePostRequest;
import kr.nutee.nuteebackend.Interceptor.HttpInterceptor;
import kr.nutee.nuteebackend.Service.MemberService;
import kr.nutee.nuteebackend.Service.PostService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping(path = "/sns/post",consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@ResponseBody
@Slf4j
public class PostController {

    private final HttpInterceptor httpInterceptor;
    private final PostService postService;
    private final MemberService memberService;

    /*
        즐겨찾기 게시판 불러오기
     */
    @PostMapping(path = "/preference")
    public String getPreferencePosts(
            HttpServletRequest request,
            @RequestParam("lastId") int lastId,
            @RequestParam("limit") int limit
    ){

        return "SUCCESS";
    }

    /*
        카테고리 게시판 불러오기
     */
    @GetMapping(path = "")
    public void getCategoryPosts(
            HttpServletRequest request,
            @RequestBody @Valid CreatePostRequest body
    ){
        Long id = getTokenMemberId(request);
        postService.getPreferencePosts(id);
    }

    /*
        글작성
     */
    @PostMapping(path = "")
    public ResponseEntity<Object> createPost(
            HttpServletRequest request,
            @RequestBody @Valid CreatePostRequest body
    ){
        Long id = getTokenMemberId(request);
        return new ResponseEntity<>(postService.createPost(id,body), HttpStatus.OK);
    }

    /*
        글 읽기
     */
    @GetMapping(path = "/{postId}")
    public ResponseEntity<Object> getPost(
            HttpServletRequest request,
            @PathVariable String postId
    ){
        Long id = getTokenMemberId(request);
        return new ResponseEntity<>(postService.getPost(id), HttpStatus.OK);
    }

    /*
        글 수정
     */
    @PatchMapping(path = "/{postId}")
    public ResponseEntity<Object> updatePost(
            HttpServletRequest request,
            @PathVariable String postId,
            @RequestBody @Valid UpdatePostRequest body
    ){
        Long memberId = getTokenMemberId(request);
        return new ResponseEntity<>(postService.updatePost(memberId,Long.parseLong(postId), body), HttpStatus.OK);
    }

    /*
        글 삭제
     */
    @DeleteMapping(path = "/{postId}")
    public ResponseEntity<Object> deletePost(
            @PathVariable String postId
    ){
        return new ResponseEntity<>(postService.deletePost(Long.parseLong(postId)),HttpStatus.OK);
    }

    /*
        글 신고
     */
    @PostMapping(path = "/{postId}/report")
    public ResponseEntity<Object> reportPost(
            @PathVariable String postId,
            HttpServletRequest request,
            @RequestBody @Valid ReportRequest body
            ){
        Long memberId = getTokenMemberId(request);
        return new ResponseEntity<>(postService.reportPost(Long.parseLong(postId),memberId,body.getContent()),HttpStatus.OK);
    }

    @GetMapping(path = "/{postId}/comments")
    public void getComments(@PathVariable String postId){

    }

    @PostMapping(path = "/{postId}/comment")
    public void createComment(@PathVariable String postId){

    }

    @PatchMapping(path = "/{postId}/comment/{commentId}")
    public void updateComment(@PathVariable String postId, @PathVariable String commentId){

    }

    @PostMapping(path = "/{postId}/comment/{parentId}")
    public void createReComment(@PathVariable String postId, @PathVariable String parentId){

    }

    @DeleteMapping(path = "/{postId}/comment/{commentId}")
    public void deleteComment(@PathVariable String postId, @PathVariable String commentId){

    }

    @PostMapping(path = "/{postId}/like")
    public void likePost(@PathVariable String postId){

    }

    @DeleteMapping(path = "/{postId}/like")
    public void unlikePost(@PathVariable String postId){

    }

    @PostMapping(path = "/{postId}/retweet")
    public void retweetPost(@PathVariable String postId){

    }

    public Long getTokenMemberId(HttpServletRequest request){
        ObjectMapper mapper = new ObjectMapper();
        Map map = mapper.convertValue(request.getAttribute("user"),Map.class);
        return Long.parseLong(map.get("id").toString());
    }

}

