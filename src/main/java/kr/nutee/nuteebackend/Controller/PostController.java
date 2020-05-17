package kr.nutee.nuteebackend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nutee.nuteebackend.DTO.Request.CreatePostRequest;
import kr.nutee.nuteebackend.Interceptor.HttpInterceptor;
import kr.nutee.nuteebackend.Service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    @Autowired
    HttpInterceptor httpInterceptor;

    @Autowired
    PostService postService;

    /*
        쿼리로 lastId, limit, 카테고리 받음
     */
    @PostMapping(path = "/all")
    public String getAllPosts(
            HttpServletRequest request,
            @RequestParam("lastId") int lastId,
            @RequestParam("limit") int limit
    ){
        System.out.println(request.getAttribute("user"));
        return "SUCCESS";
    }

    @GetMapping(path = "")
    public void getCategoryPosts(){

    }

    @PostMapping(path = "")
    public void createPost(
            HttpServletRequest request,
            @RequestBody @Valid CreatePostRequest body
    ){
        Long id = getTokenMemberId(request);
        postService.createPost(id,body);
    }

    @GetMapping(path = "/{postId}")
    public void getPost(@PathVariable String postId){

    }

    @PatchMapping(path = "/{postId}")
    public void updatePost(@PathVariable String postId){

    }

    @DeleteMapping(path = "/{postId}")
    public void deletePost(@PathVariable String postId){

    }

    @PostMapping(path = "/{postId}/report")
    public void reportPost(@PathVariable String postId){

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

