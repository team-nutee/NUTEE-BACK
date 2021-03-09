package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.Domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface PostLikeRepository extends JpaRepository<PostLike,Long> {
    List<PostLike> findPostLikesByPostId(Long postId);
    PostLike findPostLikeById(Long postId);
    PostLike deletePostLikeById(Long likeId);
    @Query("SELECT COUNT(l) FROM PostLike l WHERE l.member.id = :memberId AND l.post.isDeleted = false")
    int countPostLikesByMemberId(Long memberId);
}
