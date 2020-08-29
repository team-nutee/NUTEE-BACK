package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.Domain.Hit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {
    void countHitsByPostId(Long postId);
    List<Hit> findHitsByPostId(Long postId);
}
