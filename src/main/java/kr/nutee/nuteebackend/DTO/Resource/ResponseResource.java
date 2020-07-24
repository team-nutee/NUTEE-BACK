package kr.nutee.nuteebackend.DTO.Resource;

import kr.nutee.nuteebackend.DTO.Response.Response;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ResponseResource extends EntityModel<Response> {

    public ResponseResource(Response response, Class c, Long id, Link... links){
        super(response,links);
        add(linkTo(c).slash(id).withSelfRel());
    }

    public ResponseResource(Response response, Class c, Link... links){
        super(response,links);
        add(linkTo(c).withSelfRel());
    }
}
