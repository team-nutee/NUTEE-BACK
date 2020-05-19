package kr.nutee.nuteebackend.DTO.Response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
public class ImageResponse implements Serializable {

    String src;

    @Override
    public String toString() {
        return "Image{" +
                "src='" + src + '\'' +
                '}';
    }

    @QueryProjection
    public ImageResponse(String src) {
        this.src = src;
    }
}
