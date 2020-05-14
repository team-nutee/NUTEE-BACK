package kr.nutee.nuteebackend.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/sns/user",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
@RequiredArgsConstructor
@ResponseBody
@Slf4j
public class UserController {

    @GetMapping(path = "/{userId}")
    public void getUser(@PathVariable String userId){

    }

    @GetMapping(path = "/{userId}/posts")
    public void getUserPosts(@PathVariable String userId){

    }

    @PatchMapping(path = "/nickname")
    public void updateNickname(){

    }

    @PostMapping(path = "/pwchange")
    public void passwordChange(){

    }

    @PostMapping(path = "/profile")
    public void uploadProfileImage(){

    }

    @DeleteMapping(path = "/profile")
    public void deleteProfileImage(){

    }
}
