package by.demianbel.notes.repository;

import by.demianbel.notes.dbo.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByName(String name);

    Optional<UserEntity> findByIdAndActiveIsTrue(Long userId);
}
