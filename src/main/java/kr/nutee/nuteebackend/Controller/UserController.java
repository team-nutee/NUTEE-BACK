package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.DTO.Request.*;
import kr.nutee.nuteebackend.Service.ImageService;
import kr.nutee.nuteebackend.Service.MemberService;
import kr.nutee.nuteebackend.Service.PostService;
import kr.nutee.nuteebackend.Service.Util;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@ResponseBody
@Slf4j
public class UserController {

    private Util util;
    private MemberService memberService;
    private PostService postService;
    private ImageService imageService;

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
            @RequestBody @Valid PasswordUpdateRequest body
    ){
        Long memberId = util.getTokenMemberId(request);
        memberService.updatePassword(memberId,body.getPassword());
        return new ResponseEntity<>(
                "비밀번호 변경에 성공하였습니다.", HttpStatus.OK
        );
    }

    @PostMapping(path = "/profile")
    public ResponseEntity<Object> uploadProfileImage(
            HttpServletRequest request,
            @RequestBody @Valid ProfileRequest body
    ){
        Long memberId = util.getTokenMemberId(request);
        imageService.uploadProfile(memberId,body.getSrc());
        return new ResponseEntity<>(
                "프로필 이미지 등록에 성공하였습니다.", HttpStatus.OK
        );
    }

    @DeleteMapping(path = "/profile")
    public ResponseEntity<Object> deleteProfileImage(
            HttpServletRequest request
    ){
        Long memberId = util.getTokenMemberId(request);
        imageService.deleteProfile(memberId);
        return new ResponseEntity<>(
                "프로필 이미지 삭제에 성공하였습니다.", HttpStatus.OK
        );
    }

    @PatchMapping(path = "/interests")
    public ResponseEntity<Object> updateInterests(
            HttpServletRequest request,
            @RequestBody InterestsUpdateRequest body
    ){
        Long memberId = util.getTokenMemberId(request);
        memberService.updateInterests(memberId,body.getInterests());
        return new ResponseEntity<>(
                "관심사 변경에 성공하였습니다.", HttpStatus.OK
        );
    }

    @PatchMapping(path = "/majors")
    public ResponseEntity<Object> updateMajors(
            HttpServletRequest request,
            @RequestBody MajorsUpdateRequest body
    ){
        Long memberId = util.getTokenMemberId(request);
        memberService.updateMajors(memberId,body.getMajors());
        return new ResponseEntity<>(
                "전공 변경에 성공하였습니다.", HttpStatus.OK
        );
    }
}
