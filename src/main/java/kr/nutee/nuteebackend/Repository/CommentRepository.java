package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.Domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findCommentById(Long id);

    @Query("SELECT c FROM Comment c WHERE c.isDeleted = false AND c.post.id = :postId AND c.parent IS NULL")
    List<Comment> findAllCommentsByPostId(Long postId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.isDeleted = false AND c.member.id = :memberId")
    int countCommentsByMemberId(Long memberId);
}
