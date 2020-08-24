package kr.nutee.nuteebackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NuteeBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(NuteeBackendApplication.class, args);
    }
}
