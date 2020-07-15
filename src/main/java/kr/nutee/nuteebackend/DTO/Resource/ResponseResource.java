package kr.nutee.nuteebackend.DTO.Resource;

import kr.nutee.nuteebackend.DTO.Response.Response;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

public class ResponseResource extends EntityModel<Response> {

    public ResponseResource(Response content, Link... links){
        super(content,links);
    }
}
