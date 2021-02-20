package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.DTO.Resource.ResponseResource;
import kr.nutee.nuteebackend.DTO.Response.Response;
import kr.nutee.nuteebackend.Enum.Interest;
import kr.nutee.nuteebackend.Enum.Major;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping(path = "/sns/category",consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    @GetMapping("/interests")
    public ResponseEntity<ResponseResource> getInterests() {
        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(Interest.values())
            .build();

        ResponseResource resource = new ResponseResource(response, CategoryController.class);
        return ResponseEntity.ok().body(resource);
    }

    @GetMapping("/majors")
    public ResponseEntity<ResponseResource> getMajors() {
        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(Major.values())
            .build();

        ResponseResource resource = new ResponseResource(response, CategoryController.class);
        return ResponseEntity.ok().body(resource);
    }
}
