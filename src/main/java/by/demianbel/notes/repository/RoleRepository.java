package by.demianbel.notes.repository;

import by.demianbel.notes.dbo.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
}
