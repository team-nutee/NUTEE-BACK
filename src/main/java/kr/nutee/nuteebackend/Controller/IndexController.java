package kr.nutee.nuteebackend.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RefreshScope
@RequestMapping(path = "/sns",consumes = MediaType.APPLICATION_JSON_VALUE)
@ResponseBody
@RequiredArgsConstructor
@Slf4j
public class IndexController {

    @GetMapping("/api")
    public RepresentationModel index(){
        var index = new RepresentationModel<>();
        index.add(linkTo(PostController.class).withRel("post"));
        return index;
    }
}
