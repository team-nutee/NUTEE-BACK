package kr.nutee.nuteebackend.DTO.Response;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReCommentResponse implements Serializable {
    private Long id;
    private String content;
    private List<User> likers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    User user;
}
