package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.DTO.PostSearchCondition;
import kr.nutee.nuteebackend.Domain.Post;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface PostRepositoryCustom {
    List<Post> search(PostSearchCondition condition);
}
