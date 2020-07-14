package kr.nutee.nuteebackend.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.nutee.nuteebackend.DTO.Request.CreatePostRequest;
import kr.nutee.nuteebackend.DTO.Response.PostResponse;
import kr.nutee.nuteebackend.DTO.Response.PostShowResponse;
import kr.nutee.nuteebackend.DTO.Response.User;
import kr.nutee.nuteebackend.Domain.Member;
import kr.nutee.nuteebackend.Repository.MemberRepository;
import kr.nutee.nuteebackend.Service.PostService;
import kr.nutee.nuteebackend.Service.Util;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class PostControllerTest extends BaseControllerTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    Util util;

    @Test
    @DisplayName("이미지가 없는 포스트를 생성하는 테스트")
    void createPost1() throws Exception {

        //given
        CreatePostRequest body = CreatePostRequest.builder()
                .title("제목 테스트")
                .content("내용 테스트")
                .category("IT")
                .build();

        Long memberId = 1L;
        Member member = memberRepository.findMemberById(memberId);
        System.out.println(member);
        User user = User.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .image(util.transferImage(member.getImage()))
                .build();

        //when
        MockHttpServletRequestBuilder builder = post("/sns/post")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + token)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body").exists())
                .andExpect(jsonPath("body.id").exists())
                .andExpect(jsonPath("body.title").value("제목 테스트"))
                .andExpect(jsonPath("body.content").value("내용 테스트"))
                .andExpect(jsonPath("body.category").value("IT"))
                .andExpect(jsonPath("body.user").value(user))
                .andExpect(jsonPath("body.images").isEmpty())
                .andExpect(jsonPath("body.likers").isEmpty())
                .andExpect(jsonPath("body.comments").isEmpty())
                .andExpect(jsonPath("body.retweet").isEmpty())
                .andExpect(jsonPath("body.hits").value(0))
                .andExpect(jsonPath("body.blocked").value(false))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-post").exists())
                .andExpect(jsonPath("_links.remove-post").exists());



    }


}