package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.DTO.PostSearchCondition;
import kr.nutee.nuteebackend.Domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long>, QuerydslPredicateExecutor<Post> ,PostRepositoryCustom {
}
