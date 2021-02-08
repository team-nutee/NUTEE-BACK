package kr.nutee.nuteebackend.Controller;

import java.util.List;
import kr.nutee.nuteebackend.DTO.Resource.ResponseResource;
import kr.nutee.nuteebackend.DTO.Response.PostShowResponse;
import kr.nutee.nuteebackend.DTO.Response.Response;
import kr.nutee.nuteebackend.Service.PostService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RefreshScope
@RequestMapping(path = "/sns/search",consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final PostService postService;

    /*
        검색 된 글 목록 가져오기
     */
    @GetMapping(path = "/{text}")
    public ResponseEntity<ResponseResource> searchPosts(
            @PathVariable String text,
            @RequestParam("lastId") int lastId,
            @RequestParam("limit") int limit
    ){

        List<PostShowResponse> postShowResponses = postService
            .searchPost((long) lastId, limit, text);

        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(postShowResponses)
            .build();

        ResponseResource resource = new ResponseResource(response, SearchController.class,text);
        return ResponseEntity.ok().body(resource);
    }
}
