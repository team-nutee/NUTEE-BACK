package kr.nutee.nuteebackend.DTO.Request;

import kr.nutee.nuteebackend.DTO.Response.ImageResponse;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest implements Serializable {
    @NotEmpty
    String title;

    @NotEmpty
    String content;

    List<ImageResponse> images;

    @NotEmpty
    String category;

}
