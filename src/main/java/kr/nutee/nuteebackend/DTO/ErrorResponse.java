package kr.nutee.nuteebackend.DTO;

import lombok.Data;

@Data
public class ErrorResponse {
    String statusCode;
    String message;
}
