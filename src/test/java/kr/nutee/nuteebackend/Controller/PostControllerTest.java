package kr.nutee.nuteebackend.Controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getPreferencePosts() throws Exception {
        mockMvc.perform(post("/sns/post/preference")
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
//                    .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void getCategoryPosts() {
    }

    @Test
    void createPost() {
    }

    @Test
    void getPost() {
    }

    @Test
    void updatePost() {
    }

    @Test
    void deletePost() {
    }

    @Test
    void reportPost() {
    }

    @Test
    void getComments() {
    }

    @Test
    void createComment() {
    }

    @Test
    void updateComment() {
    }

    @Test
    void createReComment() {
    }

    @Test
    void deleteComment() {
    }

    @Test
    void likePost() {
    }

    @Test
    void unlikePost() {
    }

    @Test
    void retweetPost() {
    }
}