package kr.nutee.nuteebackend.Controller;


import kr.nutee.nuteebackend.Controller.Common.RestDocsConfiguration;
import kr.nutee.nuteebackend.DTO.Request.CreatePostRequest;
import kr.nutee.nuteebackend.DTO.Response.User;
import kr.nutee.nuteebackend.Domain.Member;
import kr.nutee.nuteebackend.Repository.MemberRepository;
import kr.nutee.nuteebackend.Service.Util;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(RestDocsConfiguration.class)
@ExtendWith(RestDocumentationExtension.class)
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
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE+";charset=UTF-8"))
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
                .andExpect(jsonPath("_links.remove-post").exists())
                .andDo(document("create-post",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("update-post").description("link to update the post"),
                                linkWithRel("remove-post").description("link to remove the post")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        requestFields(
                                fieldWithPath("title").description("title of new post(not null)"),
                                fieldWithPath("content").description("description of new post(not null)"),
                                fieldWithPath("images").description("images of new post"),
                                fieldWithPath("category").description("category of new post(not null)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        responseFields(
                                fieldWithPath("code").description("label code number"),
                                fieldWithPath("message").description("message"),
                                fieldWithPath("body").description("body of the response"),
                                fieldWithPath("body.id").description("id of the post"),
                                fieldWithPath("body.title").description("title of the post"),
                                fieldWithPath("body.content").description("description of the post"),
                                fieldWithPath("body.createdAt").description("created time of the post"),
                                fieldWithPath("body.updatedAt").description("updated time of the post"),
                                fieldWithPath("body.blocked").description("blocking flag of the post"),
                                fieldWithPath("body.user").description("author of the post"),
                                fieldWithPath("body.user.id").description("author's id number"),
                                fieldWithPath("body.user.nickname").description("author's nickname"),
                                fieldWithPath("body.user.image").description("author's profile image path"),
                                fieldWithPath("body.images").description("images of the post"),
                                fieldWithPath("body.likers").description("user who likes the post"),
                                fieldWithPath("body.comments").description("comments of the post"),
                                fieldWithPath("body.retweet").description("post that sharing other post"),
                                fieldWithPath("body.category").description("category of the post"),
                                fieldWithPath("body.hits").description(" user's join number of the post"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.update-post.href").description("link to update post"),
                                fieldWithPath("_links.remove-post.href").description("link to remove post")
                        )
                ));



    }


}