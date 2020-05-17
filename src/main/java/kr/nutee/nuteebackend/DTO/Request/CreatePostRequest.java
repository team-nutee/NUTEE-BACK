package kr.nutee.nuteebackend.DTO.Request;

import kr.nutee.nuteebackend.DTO.Image;
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

    List<Image> images;

    @NotEmpty
    String interest;

    @NotEmpty
    String major;

    @Override
    public String toString() {
        return "CreatePostRequest{" +
                "content='" + content + '\'' +
                ", images=" + images +
                '}';
    }
}
