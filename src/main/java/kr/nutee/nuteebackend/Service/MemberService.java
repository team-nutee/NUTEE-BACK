package kr.nutee.nuteebackend.Service;

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

    //비밀번호 확인
    public List<Post> getFeedsList(){
        return null;
    }
    //비밀번호 변경

    //프로필 이미지 업로드 !formData 형식



}
