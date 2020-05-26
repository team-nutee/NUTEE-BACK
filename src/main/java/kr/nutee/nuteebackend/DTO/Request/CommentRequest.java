package kr.nutee.nuteebackend.DTO.Request;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    @NotEmpty
    String content;
}
