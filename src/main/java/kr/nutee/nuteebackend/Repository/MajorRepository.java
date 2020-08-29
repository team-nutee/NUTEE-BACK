package kr.nutee.nuteebackend.Repository;


import kr.nutee.nuteebackend.Domain.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface MajorRepository extends JpaRepository<Major, Long> {

}
