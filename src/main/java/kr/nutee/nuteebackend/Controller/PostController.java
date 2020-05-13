package kr.nutee.nuteebackend.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/sns/post",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
@RequiredArgsConstructor
@ResponseBody
@Slf4j
public class PostController {

    @GetMapping(path = "/")
    public void getPosts(){

    }

    @PostMapping(path = "/")
    public void createPost(){

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

}

