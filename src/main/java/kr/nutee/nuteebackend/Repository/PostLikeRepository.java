package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.Domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike,Long> {
    List<PostLike> findPostLikesByPostId(Long postId);
    PostLike findPostLikeById(Long postId);
    PostLike deletePostLikeById(Long likeId);
}
