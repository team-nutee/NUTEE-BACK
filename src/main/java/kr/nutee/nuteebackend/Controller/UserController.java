package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.DTO.Request.*;
import kr.nutee.nuteebackend.DTO.Resource.ResponseResource;
import kr.nutee.nuteebackend.DTO.Response.PostShowResponse;
import kr.nutee.nuteebackend.DTO.Response.Response;
import kr.nutee.nuteebackend.DTO.Response.UserData;
import kr.nutee.nuteebackend.Domain.Image;
import kr.nutee.nuteebackend.Domain.Member;
import kr.nutee.nuteebackend.Service.ImageService;
import kr.nutee.nuteebackend.Service.MemberService;
import kr.nutee.nuteebackend.Service.PostService;
import kr.nutee.nuteebackend.Service.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


@RestController
@RefreshScope
@RequestMapping(path = "/sns/user",consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@ResponseBody
@Slf4j
public class UserController {

    private final Util util;
    private final MemberService memberService;
    private final PostService postService;
    private final ImageService imageService;

    /*
        내 데이터 호출
     */
    @GetMapping(path = "/me")
    public ResponseEntity<ResponseResource> getMe(
        HttpServletRequest request
    ) {
        Long memberId = util.getTokenMemberId(request);
        UserData user = memberService.getUserData(memberId);
        Response response = Response.builder()
            .code(10)
            .message("SUCCESS")
            .body(user)
            .build();

        ResponseResource resource = new ResponseResource(response, UserController.class, memberId);
        return ResponseEntity.ok().body(resource);
    }

    /*
        유저 데이터 호출
     */
    @GetMapping(path = "/{userId}")
    public ResponseEntity<ResponseResource> getUser(
            @PathVariable String userId
    ) {
        UserData user = memberService.getUserData(Long.parseLong(userId));
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(user)
                .build();

        ResponseResource resource = new ResponseResource(response, UserController.class, Long.parseLong(userId));
        return ResponseEntity.ok().body(resource);
    }

    /*
        유저가 작성한 게시글 호출
     */
    @GetMapping(path = "/{userId}/posts")
    public ResponseEntity<ResponseResource> getUserPosts(
            @PathVariable String userId,
            @RequestParam("lastId") int lastId,
            @RequestParam("limit") int limit
    ){
        List<PostShowResponse> posts = postService.getUserPosts(Long.parseLong(userId), limit, (long)lastId);
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(posts)
                .build();

        ResponseResource resource = new ResponseResource(response, UserController.class, Long.parseLong(userId),"posts");

        return ResponseEntity.ok().body(resource);
    }

    /*
        유저의 닉네임 변경
    */
    @PatchMapping(path = "/nickname")
    public ResponseEntity<ResponseResource> updateNickname(
            HttpServletRequest request,
            @RequestBody @Valid NicknameUpdateRequest body
    ){
        Long memberId = util.getTokenMemberId(request);
        UserData user = memberService.updateNickname(memberId, body.getNickname());
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(user)
                .build();

        ResponseResource resource = new ResponseResource(response, UserController.class, "nickname");
        return ResponseEntity.ok().body(resource);
    }

    /*
        유저의 비밀번호 변경
    */
    @PostMapping(path = "/pwchange")
    public ResponseEntity<ResponseResource> passwordChange(
            HttpServletRequest request,
            @RequestBody @Valid PasswordUpdateRequest body
    ){
        Long memberId = util.getTokenMemberId(request);
        memberService.updatePassword(memberId, body.getPassword());
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body("비밀번호 변경에 성공하였습니다.")
                .build();

        ResponseResource resource = new ResponseResource(response, UserController.class, "pwchange");
        return ResponseEntity.ok().body(resource);
    }

    /*
        유저 프로필 이미지 변경
    */
    @PostMapping(path = "/profile")
    public ResponseEntity<ResponseResource> uploadProfileImage(
            HttpServletRequest request,
            @RequestBody @Valid ProfileRequest body
    ){
        Long memberId = util.getTokenMemberId(request);
        Image image = imageService.uploadProfile(memberId,body.getSrc());
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(image.getSrc())
                .build();

        ResponseResource resource = new ResponseResource(response, UserController.class, "profile");
        return ResponseEntity.ok().body(resource);
    }

    /*
        유저의 프로필 이미지 삭제
    */
    @DeleteMapping(path = "/profile")
    public ResponseEntity<ResponseResource> deleteProfileImage(
            HttpServletRequest request
    ){
        Long memberId = util.getTokenMemberId(request);
        imageService.deleteProfile(memberId);
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body("프로필 이미지 삭제에 성공하였습니다.")
                .build();

        ResponseResource resource = new ResponseResource(response, UserController.class, "profile");
        return ResponseEntity.ok().body(resource);
    }

    /*
        유저 흥미 목록 변경
    */
    @PatchMapping(path = "/interests")
    public ResponseEntity<ResponseResource> updateInterests(
            HttpServletRequest request,
            @RequestBody InterestsUpdateRequest body
    ){
        Long memberId = util.getTokenMemberId(request);
        Member member = memberService.updateInterests(memberId, body.getInterests());
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(member.getInterests())
                .build();

        ResponseResource resource = new ResponseResource(response, UserController.class, "interests");
        return ResponseEntity.ok().body(resource);
    }

    /*
        유저 전공 변경
    */
    @PatchMapping(path = "/majors")
    public ResponseEntity<ResponseResource> updateMajors(
            HttpServletRequest request,
            @RequestBody MajorsUpdateRequest body
    ){
        Long memberId = util.getTokenMemberId(request);
        Member member = memberService.updateMajors(memberId, body.getMajors());
        Response response = Response.builder()
                .code(10)
                .message("SUCCESS")
                .body(member.getMajors())
                .build();

        ResponseResource resource = new ResponseResource(response, UserController.class, "majors");
        return ResponseEntity.ok().body(resource);
    }
}
