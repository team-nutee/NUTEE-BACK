package kr.nutee.nuteebackend.DTO;

import lombok.Data;

@Data
public class PostSearchCondition {
    //흥미, 작성자 번호, 학과, 글내용
    private String interest;
    private Long writerNum;
    private String major;
    private String content;
}
