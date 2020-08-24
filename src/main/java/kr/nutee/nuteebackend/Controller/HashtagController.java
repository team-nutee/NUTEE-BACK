package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.Repository.HashtagRepository;
import kr.nutee.nuteebackend.Service.PostService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/sns/hashtag", consumes = MediaType.APPLICATION_JSON_VALUE)
@RefreshScope
@RequiredArgsConstructor
@ResponseBody
@Slf4j
public class HashtagController {

    private final PostService postService;

    /*
        해당 해시태그 게시글 목록 불러오기
     */
    @GetMapping(path = "/{tag}")
    public ResponseEntity<Object> getHashtags(
            @PathVariable String tag,
            @RequestParam("lastId") int lastId,
            @RequestParam("limit") int limit
    ){
        return new ResponseEntity<>(postService.getHashtagPosts((long)lastId,limit,tag), HttpStatus.OK);
    }
}
