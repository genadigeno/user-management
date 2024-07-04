package user.management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "user_roles", schema = "gvggroup")
@Getter @Setter
public class UserRole implements GrantedAuthority {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "ROLE")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public String getAuthority() {
        return role.name();
    }
}
