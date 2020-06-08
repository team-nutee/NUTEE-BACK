package kr.nutee.nuteebackend.Service;

import kr.nutee.nuteebackend.DTO.Response.UserData;
import kr.nutee.nuteebackend.Domain.Interest;
import kr.nutee.nuteebackend.Domain.Major;
import kr.nutee.nuteebackend.Domain.Member;
import kr.nutee.nuteebackend.Domain.Post;
import kr.nutee.nuteebackend.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ResponseBody
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final InterestRepository interestRepository;
    private final MajorRepository majorRepository;
    private final PasswordEncoder bcryptEncoder;
    private final Util util;

    //
    public List<Post> getFeedsList(){
        return null;
    }
    //id로부터 유저를 가져온다.
    public Member getUser(Long id){
        return memberRepository.findMemberById(id);
    }

    public UserData getUserData(Long memberId){
        Member member = memberRepository.findMemberById(memberId);
        int commentNum = commentRepository.countCommentsByMemberId(memberId);
        int postNum = postRepository.countPostsByMemberId(memberId);
        int likeNum = postLikeRepository.countPostLikesByMemberId(memberId);

        return fillUserData(member, commentNum, postNum, likeNum);
    }

    public UserData updateNickname(Long memberId,String nickname){
        Member member = memberRepository.findMemberById(memberId);
        int commentNum = commentRepository.countCommentsByMemberId(memberId);
        int postNum = postRepository.countPostsByMemberId(memberId);
        int likeNum = postLikeRepository.countPostLikesByMemberId(memberId);
        member.setNickname(nickname);
        memberRepository.save(member);
        return fillUserData(member, commentNum, postNum, likeNum);
    }

    @Transactional
    public void updatePassword(Long memberId,String password){
        Member member = memberRepository.findMemberById(memberId);
        member.setPassword(bcryptEncoder.encode(password));
        memberRepository.save(member);
    }

    @Transactional
    public void updateInterests(Long memberId, List<String> interests){
        Member member = memberRepository.findMemberById(memberId);
        List<Interest> interestList = new ArrayList<>();
        interests.forEach(v-> interestList.add(interestRepository.save(Interest.builder().interest(v).member(member).build())));
        member.setInterests(interestList);
        memberRepository.save(member);
    }

    @Transactional
    public void updateMajors(Long memberId, List<String> majors){
        Member member = memberRepository.findMemberById(memberId);
        List<Major> majorList = new ArrayList<>();
        majors.forEach(v-> majorList.add(majorRepository.save(Major.builder().major(v).member(member).build())));
        member.setMajors(majorList);
        memberRepository.save(member);
    }

    private UserData fillUserData(Member member, int commentNum, int postNum, int likeNum) {
        return UserData.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .image(util.transferImage(member.getImage()))
                .interests(member.getInterests().stream().map(Interest::getInterest).collect(Collectors.toList()))
                .majors(member.getMajors().stream().map(Major::getMajor).collect(Collectors.toList()))
                .commentNum(commentNum)
                .likeNum(likeNum)
                .postNum(postNum)
                .build();
    }





}
