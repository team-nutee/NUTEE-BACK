package kr.nutee.nuteebackend.DTO;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
public class Image implements Serializable {
    String src;

    @Override
    public String toString() {
        return "Image{" +
                "src='" + src + '\'' +
                '}';
    }

    @QueryProjection
    public Image(String src) {
        this.src = src;
    }
}
