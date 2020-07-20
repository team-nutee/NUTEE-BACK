package kr.nutee.nuteebackend.DTO.Request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
public class ImageRequest implements Serializable {

    String src;

    @Override
    public String toString() {
        return "Image{" +
                "src='" + src + '\'' +
                '}';
    }


    public ImageRequest(String src) {
        this.src = src;
    }
}
