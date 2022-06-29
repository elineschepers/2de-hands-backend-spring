package be.ucll.tweedehandsbackend.Repositories;

import be.ucll.tweedehandsbackend.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserById (Long id);

    User findBySchoolNumber(String schoolnumber);

    User findUserByEmail(String email);

    User findUserByUuid(UUID id);

    Optional<User> findByEmail(String username);

    Optional<User> findUserBySchoolNumber(String schoolNumber);
}
