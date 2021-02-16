package kr.nutee.nuteebackend.DTO.Response;


import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse implements Serializable {
    private Long id;
    private String content;
    private List<User> likers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ReCommentResponse> reComment;
    User user;
}
