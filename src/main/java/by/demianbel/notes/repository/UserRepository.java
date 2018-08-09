package by.demianbel.notes.repository;

import by.demianbel.notes.dbo.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
