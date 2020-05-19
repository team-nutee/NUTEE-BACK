package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.DTO.PostSearchCondition;
import kr.nutee.nuteebackend.DTO.Response.CreatePostResponse;
import kr.nutee.nuteebackend.Domain.Post;

import java.util.List;

interface PostRepositoryCustom {
    List<Post> search(PostSearchCondition condition);
}
