package kr.nutee.nuteebackend.DTO;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Comment implements Serializable {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Comment> reComments;
    User user;

    @QueryProjection
    public Comment(Long id, String content, LocalDateTime createdAt, LocalDateTime updatedAt, List<Comment> reComments, User user) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reComments = reComments;
        this.user = user;
    }
}
