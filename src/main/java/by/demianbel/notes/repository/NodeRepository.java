package by.demianbel.notes.repository;

import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NodeRepository extends JpaRepository<NodeEntity, Long> {

    Optional<NodeEntity> findByUserAndIdAndActive(UserEntity currentUser, Long nodeId, boolean active);

}
