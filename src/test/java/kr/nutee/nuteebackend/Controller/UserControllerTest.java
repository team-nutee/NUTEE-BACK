package kr.nutee.nuteebackend.Controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserControllerTest {
    @Test
    @DisplayName("유저 컨트롤러 만들기")
    void create_new_user_controller(){
        UserController userController = new UserController();
        assertNotNull(userController);
    }
}