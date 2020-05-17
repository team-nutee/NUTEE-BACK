package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.Domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByUserId(String userId);
    Member findMemberById(Long memberId);
    List<Member> findAll();
}
