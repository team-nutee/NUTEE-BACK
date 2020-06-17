package kr.nutee.nuteebackend.DTO.Response;


import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
public class CommentResponse implements Serializable {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ReCommentResponse> reCommentResponses;
    User user;

    public CommentResponse(Long id, String content, LocalDateTime createdAt, LocalDateTime updatedAt, List<ReCommentResponse> reCommentResponses, User user) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reCommentResponses = reCommentResponses;
        this.user = user;
    }
}
