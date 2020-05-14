package kr.nutee.nuteebackend.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/sns/hashtag",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
@RequiredArgsConstructor
@ResponseBody
@Slf4j
public class HashtagController {

    @GetMapping(path = "/{tag}")
    public void getHashtags(@PathVariable String tag){

    }
}
