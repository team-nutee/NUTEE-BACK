package kr.nutee.nuteebackend.Repository;

import java.util.List;
import kr.nutee.nuteebackend.Domain.CommentReport;
import kr.nutee.nuteebackend.Domain.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReportRepository extends JpaRepository<CommentReport,Long>{
    List<CommentReport> findCommentReportsByCommentId(Long postId);
}
