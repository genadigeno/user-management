package user.management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users_outbox_table", schema = "gvggroup")
@Getter @Setter
@EntityListeners(AuditingEntityListener.class)
public class UserOutBox {
    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    private User user;

    @CreatedDate
    private LocalDateTime created;
}
