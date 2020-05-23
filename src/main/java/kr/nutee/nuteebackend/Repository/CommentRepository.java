package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.Domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
//    List<Comment> findCommentsByPostIdAndDeletedIsFalseAndParentIsNull(Long postId);
}
