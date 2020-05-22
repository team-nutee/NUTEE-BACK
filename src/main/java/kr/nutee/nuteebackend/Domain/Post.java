package kr.nutee.nuteebackend.Domain;

import kr.nutee.nuteebackend.Domain.common.LogDateTime;
import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends LogDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String content;

    private boolean isDeleted;

    private boolean isBlocked;

    @OneToMany (mappedBy = "post", cascade = CascadeType.PERSIST)
    private final List<Image> images = new ArrayList<>();

    @OneToMany (mappedBy = "post", cascade = CascadeType.PERSIST)
    private final List<Likes> likes = new ArrayList<>();

    @OneToMany (mappedBy = "post", cascade = CascadeType.PERSIST)
    private final List<Comment> comments = new ArrayList<>();

    @OneToMany (mappedBy = "post", cascade = CascadeType.PERSIST)
    private final List<Report> reporters = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retweet_id")
    private Post retweet;

    private String interest;

    private String major;
}
