package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.DTO.Request.CreatePostRequest;
import kr.nutee.nuteebackend.DTO.Request.NicknameUpdateRequest;
import kr.nutee.nuteebackend.Service.MemberService;
import kr.nutee.nuteebackend.Service.PostService;
import kr.nutee.nuteebackend.Service.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(path = "/sns/user",consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@ResponseBody
@Slf4j
public class UserController {

    private final Util util;
    private final MemberService memberService;
    private final PostService postService;

    @GetMapping(path = "/{userId}")
    public ResponseEntity<Object> getUser(
            @PathVariable String userId
    ){
        return new ResponseEntity<>(
                memberService.getUserData(Long.parseLong(userId)), HttpStatus.OK
        );
    }

    @GetMapping(path = "/{userId}/posts")
    public ResponseEntity<Object> getUserPosts(
            @PathVariable String userId,
            @RequestParam("lastId") int lastId,
            @RequestParam("limit") int limit
    ){
        return new ResponseEntity<>(
                postService.getUserPosts(Long.parseLong(userId),limit,(long)lastId), HttpStatus.OK
        );
    }

    @PatchMapping(path = "/nickname")
    public ResponseEntity<Object> updateNickname(
            HttpServletRequest request,
            @RequestBody @Valid NicknameUpdateRequest body
    ){
        Long memberId = util.getTokenMemberId(request);
        return new ResponseEntity<>(
                memberService.updateNickname(memberId,body.getNickname()), HttpStatus.OK
        );
    }

    @PostMapping(path = "/pwchange")
    public ResponseEntity<Object> passwordChange(
            HttpServletRequest request,
            @RequestBody @Valid NicknameUpdateRequest body
    ){
        memberService.
    }

    @PostMapping(path = "/profile")
    public void uploadProfileImage(){

    }

    @DeleteMapping(path = "/profile")
    public void deleteProfileImage(){

    }
}
