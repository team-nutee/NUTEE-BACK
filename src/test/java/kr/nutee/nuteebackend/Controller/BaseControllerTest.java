package kr.nutee.nuteebackend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nutee.nuteebackend.Common.RestDocsConfiguration;
import kr.nutee.nuteebackend.DTO.Request.CreatePostRequest;
import kr.nutee.nuteebackend.DTO.Request.ImageRequest;
import kr.nutee.nuteebackend.DTO.Request.RetweetRequest;
import kr.nutee.nuteebackend.Domain.Interest;
import kr.nutee.nuteebackend.Domain.Major;
import kr.nutee.nuteebackend.Domain.Member;
import kr.nutee.nuteebackend.Domain.Post;
import kr.nutee.nuteebackend.Enum.Category;
import kr.nutee.nuteebackend.Repository.InterestRepository;
import kr.nutee.nuteebackend.Repository.MajorRepository;
import kr.nutee.nuteebackend.Repository.MemberRepository;
import kr.nutee.nuteebackend.Repository.PostRepository;
import kr.nutee.nuteebackend.Service.MemberService;
import kr.nutee.nuteebackend.Service.PostService;
import kr.nutee.nuteebackend.Service.Util;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(RestDocsConfiguration.class)
@ExtendWith(RestDocumentationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class BaseControllerTest {

    @Autowired
    protected MajorRepository majorRepository;

    @Autowired
    protected InterestRepository interestRepository;

    @Autowired
    protected MemberService memberService;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected PostRepository postRepository;

    @Autowired
    protected PostService postService;

    @Autowired
    protected Util util;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtZjAwMDEiLCJyb2xlIjoiUk9MRV9NQU5BR0VSIiwiaWQiOjEsImV4cCI6MTkxMjA2NDU4NiwiaWF0IjoxNTk2NzA0NTg2fQ.VmpRq6R0NhyteAp2ToaPPbjAANcSfZTMKvrXxCd3iFBcm3gVLn9GYd6lJQ07gRIyk_U38x4t7VEpzA2qcbMAgA";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @AfterAll
    void deleteData() {
        System.out.println("");
    }

    @BeforeAll
    void setData(){
        //given
        //멤버 3명 디비에 생성
        Member member1 = Member.builder()
                .id(1L)
                .userId("mf0001")
                .nickname("moon1")
                .schoolEmail("mf0001@gmail.com")
                .isDeleted(false)
                .isBlocked(false)
                .password(passwordEncoder.encode("P@ssw0rd"))
                .build();

        Member member2 = Member.builder()
                .id(2L)
                .userId("mf0002")
                .nickname("moon2")
                .schoolEmail("mf0002@gmail.com")
                .isDeleted(false)
                .isBlocked(false)
                .password(passwordEncoder.encode("P@ssw0rd"))
                .build();

        Member member3 = Member.builder()
                .id(3L)
                .userId("mf0003")
                .nickname("moon3")
                .schoolEmail("mf0003@gmail.com")
                .isDeleted(false)
                .isBlocked(false)
                .password(passwordEncoder.encode("P@ssw0rd"))
                .build();

        memberService.insertUser(member1);
        memberService.insertUser(member2);
        memberService.insertUser(member3);

        //흥미,전공 등록
        List<String> interests1 = new ArrayList<>();
        interests1.add("INTER1");
        interests1.add("INTER2");
        interests1.add("INTER3");
        interests1.add("INTER4");
        interests1.forEach(v -> interestRepository.save(
                Interest.builder()
                        .interest(v)
                        .member(member1)
                        .build()
        ));

        List<String> interests2 = new ArrayList<>();
        interests2.add("INTER1");
        interests2.add("INTER3");
        interests2.forEach(v -> interestRepository.save(
                Interest.builder()
                        .interest(v)
                        .member(member2)
                        .build()
        ));

        List<String> interests3 = new ArrayList<>();
        interests3.add("INTER3");
        interests3.add("INTER4");
        interests3.add("INTER5");
        interests3.forEach(v -> interestRepository.save(
                Interest.builder()
                        .interest(v)
                        .member(member3)
                        .build()
        ));

        List<String> majors1 = new ArrayList<>();
        majors1.add("MAJOR1");
        majors1.add("MAJOR2");
        majors1.forEach(v -> majorRepository.save(
                Major.builder()
                        .major(v)
                        .member(member1)
                        .build()
        ));

        List<String> majors2 = new ArrayList<>();
        majors2.add("MAJOR2");
        majors2.add("MAJOR3");
        majors2.forEach(v -> majorRepository.save(
                Major.builder()
                        .major(v)
                        .member(member2)
                        .build()
        ));

        List<String> majors3 = new ArrayList<>();
        majors3.add("MAJOR1");
        majors3.add("MAJOR3");
        majors3.forEach(v -> majorRepository.save(
                Major.builder()
                        .major(v)
                        .member(member3)
                        .build()
        ));

        List<ImageRequest> list1 = new ArrayList<>();
        list1.add(ImageRequest.builder().src("image1Path(1).jpg").build());
        list1.add(ImageRequest.builder().src("image1Path(2).jpg").build());
        list1.add(ImageRequest.builder().src("image1Path(3).jpg").build());

        List<ImageRequest> list2 = new ArrayList<>();
        list2.add(ImageRequest.builder().src("image2Path(1).jpg").build());
        list2.add(ImageRequest.builder().src("image2Path(2).jpg").build());

        postService.createPost(member1.getId(),
                CreatePostRequest.builder()
                        .title("제목1")
                        .content("내용1")
                        .category(Category.INTER1.getCategory())
                        .images(list1)
                        .build()
        );

        postService.createPost(member1.getId(),
                CreatePostRequest.builder()
                        .title("제목2")
                        .content("내용2")
                        .category(Category.INTER2.getCategory())
                        .images(list2)
                        .build()
        );

        postService.createPost(member1.getId(),
                CreatePostRequest.builder()
                        .title("제목3")
                        .content("내용3")
                        .category(Category.INTER3.getCategory())
                        .images(null)
                        .build()
        );

        postService.createPost(member1.getId(),
                CreatePostRequest.builder()
                        .title("제목4")
                        .content("내용4")
                        .category(Category.INTER4.getCategory())
                        .images(null)
                        .build()
        );

        postService.createPost(member2.getId(),
                CreatePostRequest.builder()
                        .title("제목5")
                        .content("내용5")
                        .category(Category.INTER2.getCategory())
                        .images(null)
                        .build()
        );

        postService.createPost(member3.getId(),
                CreatePostRequest.builder()
                        .title("제목6")
                        .content("내용6")
                        .category(Category.INTER5.getCategory())
                        .images(null)
                        .build()
        );

        postService.createPost(member2.getId(),
                CreatePostRequest.builder()
                        .title("제목7")
                        .content("내용7")
                        .category(Category.INTER3.getCategory())
                        .images(null)
                        .build()
        );

        postService.createPost(member1.getId(),
                CreatePostRequest.builder()
                        .title("제목8")
                        .content("내용8")
                        .category(Category.INTER2.getCategory())
                        .images(null)
                        .build()
        );

        //4번 글 삭제
        postService.deletePost(4L,1L);

        //8번 글 블락
        Post post8 = postRepository.findPostById(8L);
        post8.setBlocked(true);
        postRepository.save(post8);

        //given
        Long memberId = 1L;
        Long postId1 = 1L;
        Long postId2 = 2L;
        Long parentId1 = 1L;

        //1번 글 댓글 12개 달기
        postService.createComment(memberId,postId1,"댓글 1입니다.");
        postService.createComment(memberId,postId1,"댓글 2입니다.");
        postService.createComment(memberId,postId1,"댓글 3입니다.");
        postService.createComment(memberId,postId1,"댓글 4입니다.");
        postService.createComment(memberId,postId1,"댓글 5입니다.");
        postService.createComment(memberId,postId1,"댓글 6입니다.");
        postService.createComment(memberId,postId1,"댓글 7입니다.");
        postService.createComment(memberId,postId1,"댓글 8입니다.");
        postService.createComment(memberId,postId1,"댓글 9입니다.");
        postService.createComment(memberId,postId1,"댓글 10입니다.");
        postService.createComment(memberId,postId1,"댓글 11입니다.");
        postService.createComment(memberId,postId1,"댓글 12입니다.");

        //2번 글 댓글 3개 달기
        postService.createComment(memberId,postId2,"댓글 1입니다.");
        postService.createComment(memberId,postId2,"댓글 2입니다.");
        postService.createComment(memberId,postId2,"댓글 3입니다.");

        //1번 글 댓글 2개 달기
        postService.createComment(memberId,postId1,"댓글 13입니다.");
        postService.createComment(memberId,postId1,"댓글 14입니다.");

        //1번 글 대댓글 2개 달기
        postService.createReComment(memberId,parentId1,postId1,"댓글1의 답글 1입니다.");
        postService.createReComment(memberId,parentId1,postId1,"댓글1의 답글 2입니다.");

        //1번 글 좋아요 누르기
        postService.likePost(2L,1L);

        //1번 글 리트윗 실행
        RetweetRequest body = RetweetRequest.builder()
                .category("INTER1")
                .content("1번 글을 리트윗합니다.")
                .title("1번글을 리트윗합니다.")
                .build();

        postService.createRetweet(1L,1L,body);
    }

}
