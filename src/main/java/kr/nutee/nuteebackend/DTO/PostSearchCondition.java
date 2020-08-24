package kr.nutee.nuteebackend.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostSearchCondition {
    //흥미, 작성자 번호, 학과, 글내용
    private List<String> interests;
    private Long writerNum;
    private List<String> majors;
    private String content;
}
