package kr.nutee.nuteebackend.Controller;

import kr.nutee.nuteebackend.Common.RestDocsConfiguration;
import kr.nutee.nuteebackend.DTO.Request.CreatePostRequest;
import kr.nutee.nuteebackend.DTO.Request.ImageRequest;
import kr.nutee.nuteebackend.Domain.Member;
import kr.nutee.nuteebackend.Enum.Category;
import kr.nutee.nuteebackend.Repository.MemberRepository;
import kr.nutee.nuteebackend.Service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(RestDocsConfiguration.class)
@ExtendWith(RestDocumentationExtension.class)
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HashtagControllerTest extends BaseControllerTest{

    @Autowired
    PostService postService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("해시태그 게시글 목록 불러오기")
    void getHashtags() throws Exception {
        //given
        String tag = "누티";
        Long lastId = 0L;
        int limit = 10;
        createPost();

        //when
        MockHttpServletRequestBuilder builder = get("/sns/hashtag/{tag}", tag)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .param("lastId", String.valueOf(lastId))
                .param("limit", String.valueOf(limit));

        //then
        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("body",hasSize(2)))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("get-hashtag",
                        links(
                                linkWithRel("self").description("link to self")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType header")
                        ),
                        responseFields(
                                fieldWithPath("code").description("label code number"),
                                fieldWithPath("message").description("message"),
                                fieldWithPath("body").description("body of the response"),
                                fieldWithPath("body[].id").description("id"),
                                fieldWithPath("body[].title").description("title"),
                                fieldWithPath("body[].content").description("content"),
                                fieldWithPath("body[].createdAt").description("createdAt"),
                                fieldWithPath("body[].updatedAt").description("updatedAt"),
                                fieldWithPath("body[].user").description("user"),
                                fieldWithPath("body[].user.id").description("user id"),
                                fieldWithPath("body[].user.nickname").description("user nickname"),
                                fieldWithPath("body[].user.image").description("user image"),
                                fieldWithPath("body[].images").description("images").optional(),
                                fieldWithPath("body[].images[].src").description("image path").optional(),
                                fieldWithPath("body[].likers").description("likers").optional(),
                                fieldWithPath("body[].likers[].id").description("id of likers").optional(),
                                fieldWithPath("body[].likers[].nickname").description("nickname of likers").optional(),
                                fieldWithPath("body[].likers[].image").description("profile image of likers").optional(),
                                fieldWithPath("body[].likers[].image.src").type(JsonFieldType.STRING).description("profile image path of likers").optional(),
                                fieldWithPath("body[].commentNum").description("commentNum"),
                                fieldWithPath("body[].retweet").type(JsonFieldType.OBJECT).description("retweet").optional(),
                                fieldWithPath("body[].category").description("category"),
                                fieldWithPath("body[].hits").description("views"),
                                fieldWithPath("body[].blocked").description("blocking flag of the post"),
                                fieldWithPath("_links.self.href").description("link to self")

                        )
                ));

    }

    void createPost() {
        Member member1 = memberRepository.findMemberById(1L);

        List<ImageRequest> list1 = new ArrayList<>();
        list1.add(ImageRequest.builder().src("image1Path(1).jpg").build());
        list1.add(ImageRequest.builder().src("image1Path(2).jpg").build());
        list1.add(ImageRequest.builder().src("image1Path(3).jpg").build());

        List<ImageRequest> list2 = new ArrayList<>();
        list2.add(ImageRequest.builder().src("image2Path(1).jpg").build());
        list2.add(ImageRequest.builder().src("image2Path(2).jpg").build());


        postService.createPost(member1.getId(),
                CreatePostRequest.builder()
                        .title("제목1")
                        .content("내용1 #누티 #해시태그")
                        .category(Category.INTER1.getCategory())
                        .images(list1)
                        .build()
        );

        postService.createPost(member1.getId(),
                CreatePostRequest.builder()
                        .title("제목2")
                        .content("내용2 #누티")
                        .category(Category.INTER2.getCategory())
                        .images(list2)
                        .build()
        );
    }
}
