package kr.nutee.nuteebackend.Controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import kr.nutee.nuteebackend.DTO.Resource.ResponseResource;
import kr.nutee.nuteebackend.DTO.Response.PostResponse;
import kr.nutee.nuteebackend.DTO.Response.PostShowResponse;
import kr.nutee.nuteebackend.DTO.Response.Response;
import kr.nutee.nuteebackend.Repository.HashtagRepository;
import kr.nutee.nuteebackend.Service.PostService;
import kr.nutee.nuteebackend.Service.Util;
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
@Slf4j
public class HashtagController {

    private final PostService postService;
    private final Util util;

    /*
        해당 해시태그 게시글 목록 불러오기
     */
    @GetMapping(path = "/{tag}")
    public ResponseEntity<ResponseResource> getHashtags(
        @PathVariable String tag,
        @RequestParam("lastId") int lastId,
        @RequestParam("limit") int limit,
        HttpServletRequest request
    ) {

        List<PostShowResponse> hashtagPosts = postService
            .getHashtagPosts((long) lastId, limit, tag);

        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(hashtagPosts)
            .build();

        ResponseResource resource = new ResponseResource(response, HashtagController.class,tag);
        return ResponseEntity.ok().body(resource);
    }
}
