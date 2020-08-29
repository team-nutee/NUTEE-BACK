package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.Domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image,Long>{
    void deleteImagesByPostId(Long postId);
    void deleteImagesByMemberId(Long memberId);
}
