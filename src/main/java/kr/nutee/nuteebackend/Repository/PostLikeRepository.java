package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.Domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike,Long> {
    List<PostLike> findPostLikesByPostId(Long postId);
    PostLike findPostLikeById(Long postId);
    PostLike deletePostLikeById(Long likeId);
    int countPostLikesByMemberId(Long memberId);
}
