package kr.nutee.nuteebackend.Enum;

import lombok.Getter;

@Getter
public enum Category {
    INTER1("INTER1"),
    INTER2("INTER2"),
    INTER3("INTER3"),
    INTER4("INTER4"),
    INTER5("INTER5"),
    INTER6("INTER6"),
    INTER7("INTER7"),
    INTER8("INTER8"),
    MAJOR1("MAJOR1"),
    MAJOR2("MAJOR2"),
    MAJOR3("MAJOR3"),
    MAJOR4("MAJOR4");

    private final String category;

    Category (String category){
        this.category = category;
    }
}
