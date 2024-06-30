package user.management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", schema = "gvggroup")
@Getter @Setter
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue
    private int id;

    @Column(nullable=false, unique=true)
    private String username;

    @Column(nullable=false, unique=true)
    private String email;

    @Column(nullable=false)
    private String password;

    @CreatedDate
    private LocalDateTime created;

    @Column
    private boolean enabled;
}
