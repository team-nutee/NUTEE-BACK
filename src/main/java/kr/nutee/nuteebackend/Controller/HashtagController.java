package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.DTO.Resource.ResponseResource;
import kr.nutee.nuteebackend.DTO.Response.Response;
import kr.nutee.nuteebackend.Service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/sns/hashtag", consumes = MediaType.APPLICATION_JSON_VALUE)
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
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(postService.getHashtagPosts((long)lastId,limit,tag))
                .build();

        ResponseResource resource = new ResponseResource(response, HashtagController.class, tag);

        return ResponseEntity.ok().body(resource);
    }
}
