package kr.nutee.nuteebackend.DTO.Response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@Builder
public class PostResponse implements Serializable {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isBlocked;
    private User user;
    private List<ImageResponse> images;
    private List<LikeResponse> likers;
    private List<CommentResponse> comments;
    private RetweetResponse retweet;
    private String interest;
    private String major;

    @QueryProjection
    public PostResponse(Long id, String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt,
                        boolean isBlocked, User user, List<ImageResponse> images, List<LikeResponse> likers,
                        List<CommentResponse> comments, RetweetResponse retweet, String interest, String major) {
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
