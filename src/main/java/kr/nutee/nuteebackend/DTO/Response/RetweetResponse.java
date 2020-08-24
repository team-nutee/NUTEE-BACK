package kr.nutee.nuteebackend.DTO.Response;


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
    private List<ImageResponse> images;
    private List<User> likers;
    private int commentNum;
    private String category;
    private int hits;
    private boolean isDeleted;


    public RetweetResponse(Long id, String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt,
                           boolean isBlocked, User user, List<ImageResponse> images, List<User> likers,
                           int commentNum, String category, int hits, boolean isDeleted) {
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
        this.category = category;
        this.hits = hits;
        this.isDeleted = isDeleted;
    }
}