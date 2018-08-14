package by.demianbel.notes.repository;

import by.demianbel.notes.dbo.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findRoleEntityByNameEquals(String name);
}
