package kr.nutee.nuteebackend.Repository;


import kr.nutee.nuteebackend.Domain.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface InterestRepository extends JpaRepository<Interest, Long> {

}
