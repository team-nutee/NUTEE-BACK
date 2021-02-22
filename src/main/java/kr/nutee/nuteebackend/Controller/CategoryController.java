package kr.nutee.nuteebackend.Controller;

import java.util.Arrays;
import kr.nutee.nuteebackend.DTO.Resource.ResponseResource;
import kr.nutee.nuteebackend.DTO.Response.Response;
import kr.nutee.nuteebackend.Enum.InterestCategory;
import kr.nutee.nuteebackend.Enum.MajorCategory;
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
        String[] interests = Arrays.stream(InterestCategory.values())
            .map(InterestCategory::getInterest).toArray(String[]::new);
        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(interests)
            .build();

        ResponseResource resource = new ResponseResource(response, CategoryController.class);
        return ResponseEntity.ok().body(resource);
    }

    @GetMapping("/majors")
    public ResponseEntity<ResponseResource> getMajors() {
        String[] majors = Arrays.stream(MajorCategory.values())
            .map(MajorCategory::getMajor).toArray(String[]::new);
        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(majors)
            .build();

        ResponseResource resource = new ResponseResource(response, CategoryController.class);
        return ResponseEntity.ok().body(resource);
    }
}
