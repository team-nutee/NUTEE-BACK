package kr.nutee.nuteebackend.Repository;

import java.util.List;
import kr.nutee.nuteebackend.Domain.CommentLike;
import kr.nutee.nuteebackend.Domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {
    List<CommentLike> findPostLikesByCommentId(Long commentId);
    CommentLike findPostLikeById(Long postId);
    CommentLike deletePostLikeById(Long likeId);
    int countPostLikesByMemberId(Long memberId);
}
