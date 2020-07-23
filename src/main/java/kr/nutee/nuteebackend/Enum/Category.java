package kr.nutee.nuteebackend.Enum;

import lombok.Getter;

@Getter
public enum Category {
    INTER1("INTER1"),
    INTER2("INTER2"),
    INTER3("INTER3"),
    INTER4("INTER4"),
    INTER5("INTER5");

    private final String category;

    Category (String category){
        this.category = category;
    }
}
