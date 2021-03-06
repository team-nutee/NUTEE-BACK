package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.Domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long>{
    Post findPostById(Long postId);

    @Query("SELECT p FROM Post p WHERE p.category = :category AND p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Post> findPostsByCategory(String category, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.category = :category AND p.isDeleted = false AND p.id < :lastId ORDER BY p.createdAt DESC")
    List<Post> findPostsByCategoryEqualsAndIdLessThan(String category,Long lastId, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "WHERE (p.title LIKE CONCAT('%',:text,'%') " +
            "OR p.content LIKE CONCAT('%',:text,'%') " +
            "OR p.member.nickname LIKE CONCAT('%',:text,'%')) " +
            "AND p.isDeleted = false " +
            "ORDER BY p.createdAt DESC")
    List<Post> findPostsByText(String text, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "WHERE (p.title LIKE CONCAT('%',:text,'%') " +
            "OR p.content LIKE CONCAT('%',:text,'%') " +
            "OR p.member.nickname LIKE CONCAT('%',:text,'%')) " +
            "AND p.isDeleted = false " +
            "AND p.id < :lastId " +
            "ORDER BY p.createdAt DESC")
    List<Post> findPostsByTextAndIdLessThan(String text,Long lastId, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN p.postHashtags ph " +
            "LEFT JOIN ph.hashtag h " +
            "WHERE h.id = :hashtagId " +
            "AND p.isDeleted = false " +
            "ORDER BY p.createdAt DESC")
    List<Post> findPostsByHashtagId(Long hashtagId, Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN p.postHashtags ph " +
            "LEFT JOIN ph.hashtag h " +
            "WHERE h.id = :hashtagId " +
            "AND p.isDeleted = false " +
            "AND p.id < :lastId " +
            "ORDER BY p.createdAt DESC")
    List<Post> findPostsByHashtagIdAndIdLessThan(Long hashtagId, Long lastId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.member.id = :memberId AND p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Post> findPostsByMemberId(Long memberId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.member.id = :memberId AND p.isDeleted = false AND p.id < :lastId ORDER BY p.createdAt DESC")
    List<Post> findPostsByMemberIdAndIdLessThan(Long memberId,Long lastId, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.isDeleted = false AND p.member.id = :memberId")
    int countPostsByMemberId(Long memberId);

    @Query("SELECT p FROM Post p WHERE p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Post> findAllPosts(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND p.id < :lastId ORDER BY p.createdAt DESC")
    List<Post> findAllPostsAndIdLessThan(Long lastId, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p " +
        "LEFT JOIN p.comments c " +
        "LEFT JOIN c.member m " +
        "WHERE m.id = :memberId " +
        "AND p.isDeleted = false " +
        "ORDER BY p.createdAt DESC")
    List<Post> findUserCommentPosts(Long memberId, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p " +
        "LEFT JOIN p.comments c " +
        "LEFT JOIN c.member m " +
        "WHERE m.id = :memberId " +
        "AND p.isDeleted = false " +
        "AND p.id < :lastId " +
        "ORDER BY p.createdAt DESC")
    List<Post> findUserCommentPostsAndIdLessThan(Long memberId, Long lastId, Pageable pageable);

    @Query("SELECT p FROM Post p " +
        "LEFT JOIN p.likes l " +
        "LEFT JOIN l.member m " +
        "WHERE m.id = :memberId " +
        "AND p.isDeleted = false " +
        "ORDER BY p.createdAt DESC")
    List<Post> findUserLikePosts(Long memberId, Pageable pageable);

    @Query("SELECT p FROM Post p " +
        "LEFT JOIN p.likes l " +
        "LEFT JOIN l.member m " +
        "WHERE m.id = :memberId " +
        "AND p.isDeleted = false " +
        "AND p.id < :lastId " +
        "ORDER BY p.createdAt DESC")
    List<Post> findUserLikePostsAndIdLessThan(Long memberId, Long lastId, Pageable pageable);
}
