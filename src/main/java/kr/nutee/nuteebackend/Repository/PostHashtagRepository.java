package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.Domain.Hashtag;
import kr.nutee.nuteebackend.Domain.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface PostHashtagRepository extends JpaRepository<PostHashtag,Long>{
    List<PostHashtag> findPostHashtagsByHashtagId(Long hashtagId);
}
