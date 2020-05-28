package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.Domain.Hit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {
    void countHitsByPostId(Long postId);
    List<Hit> findHitsByPostId(Long postId);
}