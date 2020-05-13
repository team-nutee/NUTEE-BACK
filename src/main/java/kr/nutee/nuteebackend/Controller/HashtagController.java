package kr.nutee.nuteebackend.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HashtagController {

    @GetMapping(path = "/{tag}")
    public void getHashtags(@PathVariable String tag){

    }
}
