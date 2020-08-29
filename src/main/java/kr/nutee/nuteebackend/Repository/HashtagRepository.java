package kr.nutee.nuteebackend.Repository;

import kr.nutee.nuteebackend.Domain.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Hashtag findByName(String name);
}
