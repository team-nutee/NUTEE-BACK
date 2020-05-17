package kr.nutee.nuteebackend.DTO;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image implements Serializable {
    String src;

    @Override
    public String toString() {
        return "Image{" +
                "src='" + src + '\'' +
                '}';
    }
}
