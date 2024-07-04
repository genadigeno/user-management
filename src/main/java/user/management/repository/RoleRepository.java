package user.management.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import user.management.model.Role;
import user.management.model.UserRole;

@Repository
public interface RoleRepository extends CrudRepository<UserRole, Integer> {
    UserRole findByRole(Role role);
}
