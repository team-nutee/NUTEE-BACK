package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.Domain.Hashtag;
import kr.nutee.nuteebackend.Domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report,Long>{
    List<Report> findReportsByPostId(Long postId);
}
