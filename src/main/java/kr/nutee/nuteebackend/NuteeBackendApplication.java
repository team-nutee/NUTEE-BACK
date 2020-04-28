package kr.nutee.nuteebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.persistence.Persistence;

@SpringBootApplication
public class NuteeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(NuteeBackendApplication.class, args);
    }

}
