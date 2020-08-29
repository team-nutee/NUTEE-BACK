package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.Domain.Hashtag;
import kr.nutee.nuteebackend.Domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface MemberRepository extends JpaRepository<Member, Long>{
    Member findByUserId(String userId);
    Member findMemberById(Long memberId);
    Member findMemberByNicknameContains(String nickname);
}
