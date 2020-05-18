package kr.nutee.nuteebackend.Service;

import kr.nutee.nuteebackend.Domain.Member;
import kr.nutee.nuteebackend.Domain.Post;
import kr.nutee.nuteebackend.Repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    //
    public List<Post> getFeedsList(){
        return null;
    }
    //id로부터 유저를 가져온다.
    public Member getUser(Long id){
        return memberRepository.search
    }




}
