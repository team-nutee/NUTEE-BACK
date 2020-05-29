package kr.nutee.nuteebackend.DTO.Request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetweetRequest implements Serializable {

    @NotEmpty
    String title;

    @NotEmpty
    String content;

    @NotEmpty
    String category;

    @Override
    public String toString() {
        return "RetweetRequest{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
