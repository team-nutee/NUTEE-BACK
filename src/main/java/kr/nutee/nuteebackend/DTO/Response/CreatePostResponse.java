package kr.nutee.nuteebackend.DTO.Response;

import kr.nutee.nuteebackend.DTO.Comment;
import kr.nutee.nuteebackend.DTO.Image;
import kr.nutee.nuteebackend.DTO.Like;
import kr.nutee.nuteebackend.DTO.User;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostResponse implements Serializable {
    private Long id;
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
}
