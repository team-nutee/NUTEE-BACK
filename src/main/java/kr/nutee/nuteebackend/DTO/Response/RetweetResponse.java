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
public class RetweetResponse implements Serializable {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isBlocked;
    private User user;
    private List<ImageResponse> imageResponses;
    private List<LikeResponse> likers;
    private List<CommentResponse> commentResponses;
    private String category;

    @QueryProjection
    public RetweetResponse(Long id, String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt,
                           boolean isBlocked, User user, List<ImageResponse> imageResponses, List<LikeResponse> likers,
                           List<CommentResponse> commentResponses, String category) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isBlocked = isBlocked;
        this.user = user;
        this.imageResponses = imageResponses;
        this.likers = likers;
        this.commentResponses = commentResponses;
        this.category = category;
    }
}