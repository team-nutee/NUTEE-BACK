package kr.nutee.nuteebackend.DTO.Response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
public class PostShowResponse implements Serializable {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isBlocked;
    private User user;
    private List<ImageResponse> images;
    private List<User> likers;
    private int commentNum;
    private RetweetResponse retweet;
    private String category;
    private int hits;

    @QueryProjection
    public PostShowResponse(Long id, String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt,
                        boolean isBlocked, User user, List<ImageResponse> images, List<User> likers,
                        int commentNum, RetweetResponse retweet, String category, int hits) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isBlocked = isBlocked;
        this.user = user;
        this.images = images;
        this.likers = likers;
        this.commentNum = commentNum;
        this.retweet = retweet;
        this.category = category;
        this.hits = hits;
    }
}
