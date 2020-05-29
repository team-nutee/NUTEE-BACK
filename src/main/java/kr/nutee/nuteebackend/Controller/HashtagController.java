package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.Repository.HashtagRepository;
import kr.nutee.nuteebackend.Service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/sns/hashtag",consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@ResponseBody
@Slf4j
public class HashtagController {

    private final PostService postService;

    @GetMapping(path = "/{tag}")
    public ResponseEntity<Object> getHashtags(
            @PathVariable String tag,
            @RequestParam("lastId") int lastId,
            @RequestParam("limit") int limit
    ){
        return new ResponseEntity<>(postService.getHashtagPosts((long)lastId,limit,tag), HttpStatus.OK);
    }
}
