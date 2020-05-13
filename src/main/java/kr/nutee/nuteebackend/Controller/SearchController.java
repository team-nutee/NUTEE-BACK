package kr.nutee.nuteebackend.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SearchController {

    @GetMapping(path = "/{text}")
    public void searchPost(@PathVariable String text){

    }
}
