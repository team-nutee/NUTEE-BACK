package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.Domain.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostReportRepository extends JpaRepository<PostReport,Long>{
    List<PostReport> findPostReportsByPostId(Long postId);
}
