package kr.nutee.nuteebackend.Domain.common;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter @Setter
@MappedSuperclass
public abstract class LogDateTime {
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
