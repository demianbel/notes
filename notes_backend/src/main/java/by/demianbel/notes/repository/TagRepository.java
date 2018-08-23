package by.demianbel.notes.repository;

import by.demianbel.notes.dbo.TagEntity;
import by.demianbel.notes.dbo.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<TagEntity, Long> {

    List<TagEntity> findAllByUserAndActiveAndNameLike(UserEntity user, boolean active, String name);

    Optional<TagEntity> findFirstByUserAndActiveAndName(UserEntity user, boolean active, String name);

    Optional<TagEntity> findFirstByUserAndName(UserEntity user, String name);

    Optional<TagEntity> findByUserAndId(UserEntity currentUser, Long id);

    Optional<TagEntity> findByUserAndIdAndActive(UserEntity currentUser, Long id, boolean active);

    List<TagEntity> findByUserAndActiveAndIdIn(UserEntity currentUser, boolean active, List<Long> tagIds);

    List<TagEntity> findByUserAndActive(UserEntity currentUser, boolean active);
}
