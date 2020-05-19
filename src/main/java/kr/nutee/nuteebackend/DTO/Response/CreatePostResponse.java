package kr.nutee.nuteebackend.DTO.Response;

import com.querydsl.core.annotations.QueryProjection;
import kr.nutee.nuteebackend.DTO.Comment;
import kr.nutee.nuteebackend.DTO.Image;
import kr.nutee.nuteebackend.DTO.Like;
import kr.nutee.nuteebackend.DTO.User;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
public class CreatePostResponse implements Serializable {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isBlocked;
    private User user;
    private List<Image> images;
    private List<Like> likers;
    private List<Comment> comments;
    private CreatePostResponse retweet;
    private String interest;
    private String major;

    @QueryProjection
    public CreatePostResponse(Long id,String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isBlocked, User user, List<Image> images, List<Like> likers, List<Comment> comments, CreatePostResponse retweet, String interest, String major) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isBlocked = isBlocked;
        this.user = user;
        this.images = images;
        this.likers = likers;
        this.comments = comments;
        this.retweet = retweet;
        this.interest = interest;
        this.major = major;
    }
}
