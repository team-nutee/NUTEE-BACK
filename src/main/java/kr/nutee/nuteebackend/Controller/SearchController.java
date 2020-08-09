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
@RequestMapping(path = "/sns/search",consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@ResponseBody
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
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(postService.searchPost((long)lastId, limit, text))
                .build();

        ResponseResource resource = new ResponseResource(response, SearchController.class, text);

        return ResponseEntity.ok().body(resource);
    }
}
