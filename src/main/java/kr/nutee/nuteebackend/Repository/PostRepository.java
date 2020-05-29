package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.Domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long>{
    Post findPostById(Long postId);

    @Query("SELECT p FROM Post p WHERE p.category = :category AND p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Post> findPostsByCategory(String category, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.category = :category AND p.isDeleted = false AND p.id < :lastId ORDER BY p.createdAt DESC")
    List<Post> findPostsByCategoryEqualsAndIdLessThan(String category,Long lastId, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "WHERE p.title LIKE CONCAT('%',:text,'%') " +
            "OR p.content LIKE CONCAT('%',:text,'%') " +
            "OR p.member.nickname LIKE CONCAT('%',:text,'%') " +
            "AND p.isDeleted = false " +
            "ORDER BY p.createdAt DESC")
    List<Post> findPostsByText(String text, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "WHERE p.title LIKE CONCAT('%',:text,'%') " +
            "OR p.content LIKE CONCAT('%',:text,'%') " +
            "OR p.member.nickname LIKE CONCAT('%',:text,'%') " +
            "AND p.isDeleted = false " +
            "AND p.id < :lastId " +
            "ORDER BY p.createdAt DESC")
    List<Post> findPostsByTextAndIdLessThan(String text,Long lastId, Pageable pageable);

}
