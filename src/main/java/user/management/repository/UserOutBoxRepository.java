package user.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import user.management.model.User;
import user.management.model.UserOutBox;

@Repository
public interface UserOutBoxRepository extends JpaRepository<UserOutBox, Integer> {
    @Modifying
    @Query("delete from UserOutBox ub where ub.user.id = ?1")
    void deleteByUser(int userId);

    UserOutBox findByUser(User user);
}
