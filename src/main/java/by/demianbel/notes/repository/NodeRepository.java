package by.demianbel.notes.repository;

import by.demianbel.notes.dbo.NodeEntity;
import by.demianbel.notes.dbo.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NodeRepository extends JpaRepository<NodeEntity, Long> {

    Optional<NodeEntity> findByUserAndIdAndActiveIsTrue(UserEntity currentUser, Long nodeId);

    List<NodeEntity> findByUserAndActiveIsTrueAndParentNodeIsNull(UserEntity currentUser);

    Optional<NodeEntity> findFirstByUserAndActiveIsTrueAndName(UserEntity currentUser, String name);

    List<NodeEntity> findAllByUserAndActiveIsTrueAndNameLike(UserEntity currentUser, String name);
}
