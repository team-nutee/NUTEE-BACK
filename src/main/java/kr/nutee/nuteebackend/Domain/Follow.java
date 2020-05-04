package kr.nutee.nuteebackend.Domain;

import kr.nutee.nuteebackend.Domain.common.LogDateTime;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Follow extends LogDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "following_id")
    private Follow following;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private Follow follower;
}
